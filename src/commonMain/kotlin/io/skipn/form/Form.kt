package io.skipn.form

import io.skipn.FileData
import io.skipn.FormEndpoint
import io.skipn.builder.launch
import io.skipn.elements.DomElement
import io.skipn.events.*
import io.skipn.form.InputField.Companion.convertType
import io.skipn.observers.attributeOf
import io.skipn.observers.classesOf
import io.skipn.observers.divOf
import io.skipn.provide.locate
import io.skipn.provide.pin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.datetime.LocalDateTime
import kotlinx.html.*
import kotlin.reflect.KProperty

expect suspend inline fun <reified RESP: Any> postForm(state: FormState<RESP>): RESP

sealed class FormField<T: Any?>

typealias FormValidatorFunc = FormValidator.() -> Unit

class ValueField<T: Any?>(
        val value: T
) : FormField<T>() {
    override fun toString(): String {
        return value.toString()
    }
}

class InputField<T: Any?>(
        val name: String,
        var required: Boolean,
        val valueAttr: MutableStateFlow<T?>,
        val error: MutableStateFlow<String?>,
) : FormField<T>() {

    var touched = false
        private set

    fun touch() {
        if (touched) return
        touched = true
    }

    companion object {

        inline fun <reified T: Any?> create(property: KProperty<T>): InputField<T> {
            val required = null !is T

            return InputField(
                    property.name,
                    required,
                    MutableStateFlow(null),
                    MutableStateFlow(null),
            )
        }

        inline fun <reified T> convertType(): InputType {
            return when(T::class) {
                Int::class -> InputType.number
                Float::class -> InputType.number
                Double::class -> InputType.number
                LocalDateTime::class -> InputType.dateTimeLocal
                FileData::class -> InputType.file
                Boolean::class -> InputType.checkBox
                else -> InputType.text
            }
        }

        inline fun <reified T: Any?> convertValue(valueString: String): T? {
            if (valueString == "") return null

            return when(T::class) {
                Int::class -> valueString.toInt() as T
                Float::class -> valueString.toFloat() as T
                Double::class -> valueString.toDouble() as T
                LocalDateTime::class -> LocalDateTime.parse(valueString) as T
                Boolean::class -> (valueString == "true") as T
//                FileData::class -> FileData()
                else -> valueString as T
            }
        }
    }
}

class FormState<RESP: Any>(val endpoint: FormEndpoint<*, RESP>) {

    val inputs = hashMapOf<String, FormField<*>>()
    val _error = MutableStateFlow<String?>(null)
    val validators = arrayListOf<FormValidatorFunc>()

    fun addInput(input: InputField<*>) {
        inputs[input.name] = input
    }

    fun <T: Any?> addValue(property: KProperty<T>, value: T) {
        inputs[property.name] = ValueField(value)
    }

    private fun validateAllCustom(): HashMap<String, String> {
        return hashMapOf<String, String>().apply {
            validateCustom(endpoint.validator)?.let {
                putAll(it)
            }
            validators.forEach {
                validateCustom(it)?.let { validator ->
                    putAll(validator)
                }
            }
        }
    }

    fun validateTouched() {
        val custom = validateAllCustom()

        inputs.filter { (it.value as? InputField<*>)?.touched ?: false }.forEach {
            val input = it.value as InputField<*>

            if (input.required && input.valueAttr.value == null) {
                input.error.value = "Kötelező megadni"
            }
            else {
                val customError = custom.get(it.key)
                input.error.value = customError
            }
        }
    }

    fun validateAll(): Boolean {
        var valid = true

        val custom = validateAllCustom()

        inputs.filter { it.value is InputField<*> }.forEach {
            val input = it.value as InputField<*>

            input.touch()

            if (input.required && input.valueAttr.value == null) {
                input.error.value = "Kötelező megadni"
                if (valid) valid = false
            }
            else {
                val customError = custom[it.key]
                input.error.value = customError

                if (customError != null && valid)
                    valid = false
            }
        }
        return valid
    }

    private fun validateCustom(_validator: FormValidatorFunc?) : Map<String, String>? {
        val validator = _validator ?: return null

        // Create snapshot of every input value
        val snapshot = inputs.filterValues { it is InputField<*> }.mapValues {
            (it.value as InputField<*>).valueAttr.value
        }

        // Perform custom logic against form snapshot
        val customValidator = FormValidator(snapshot).apply {
            validator()
        }

        return customValidator.validationError
    }

    inline fun <reified T: Any?> findOrCreate(property: KProperty<T>, default: T?): InputField<T> {
        return getInput(property) ?: InputField.create(property).also {
            it.valueAttr.value = default
            addInput(it)
        }
    }

    inline fun <reified T: Any?> getInput(property: KProperty<T>) = inputs[property.name] as? InputField<T>

    operator fun plus(formState: FormState<RESP>): FormState<RESP> {
        return FormState(endpoint).also {
            it.inputs.putAll(inputs)
            it.inputs.putAll(formState.inputs)
            it.validators.addAll(validators)
            it.validators.addAll(formState.validators)
        }
    }

    fun resetError() {
        _error.value = null
    }

    fun error(id: String) {
        _error.value = id
    }

    fun touch(input: InputField<*>) {
        input.touch()
        resetError()
    }
}

class FormBuilder<RESP: Any>(val elem: FORM, val state: FormState<RESP>) {

    inline fun <reified T: Any?> KProperty<T>.Input(
        label: String,
        placeholder: String,
        type: InputType? = null,
        default: T? = null,
        list: String? = null
    ): InputField<T> {
        val input = state.findOrCreate(this, default)

        with(elem) {
            InputComp(input, label, placeholder, list, type ?: convertType<T>(), "w-full")
        }

        return input
    }

    fun KProperty<Boolean>.CheckBox(
        text: String? = null,
        body: DomElement? = null,
        default: Boolean = false
    ): InputField<Boolean> {
        val input = state.findOrCreate(this, default)

        with(elem) {
            CheckBoxComp(input, text, body)
        }

        return input
    }

    fun FlowContent.Submit(text: String) {
        button(classes = "mx-3 p-2 rounded bg-green-400 hover:bg-green-500 shadow-lg") {
            onClick {
                launch {
                    submitDefault()
                }
            }
            span("text-lg text-white") { +text }
        }
    }

    suspend fun submitDefault() {
        if (!state.validateAll()) return

        postForm(state as FormState<Any>)
    }
}

class FormValidator(val inputs: Map<String, Any?>) {

    private var _validationError: HashMap<String, String>? = null

    val validationError: Map<String, String>?
    get() = _validationError

    inline fun <reified T: Any?> validate(input: KProperty<T>, checkAgainst: InputValidator.(T) -> Unit) {
        InputValidator(inputs).apply {
            valueFor(input)?.let {
                checkAgainst(it)
            }
        }.errorMessage?.let { msg ->
            fail(input.name, msg)
        }
    }

    fun fail(input: String, msg: String) {
        val validationError = _validationError ?: hashMapOf<String, String>().also {
            this._validationError = it
        }
        validationError[input] = msg
    }
}
class InputValidator(val inputs: Map<String, Any?>) {
    // If field is required but null, this message will be ignored
    // and required error message will be shown
    var errorMessage: String? = null

    fun fail(message: String) {
        // Throw away subsequent errors. Only keep the first one to show,
        // if field is required but null, this message will be ignored
        // and required error message will be shown
        if (errorMessage != null) return
        errorMessage = message
    }

    inline fun <reified T: Any?> valueFor(field: KProperty<T>): T? {
        return inputs[field.name] as? T
    }
}

inline fun <reified RESP: Any> FlowContent.Form(
        endpoint: FormEndpoint<*, RESP>,
        noinline validator: FormValidatorFunc? = null,
        crossinline body: FormBuilder<RESP>.() -> Unit,
) = Form(
    FormState(endpoint).apply {
        validator?.let {
            validators.add(validator)
        }
    },
    body
)

inline fun <reified RESP: Any> FlowContent.Form(
        state: FormState<RESP>,
        crossinline body: FormBuilder<RESP>.() -> Unit,
) {
    form(classes = "w-full mx-auto", method = FormMethod.post, encType = FormEncType.multipartFormData) {
        preventDefaultSubmit()

        pin(state)

        FormBuilder(this, state).apply {
            body()
        }

        divOf(state._error) { msg ->
            if (msg == null) return@divOf
            p("text-red-500 text-sm italic pt-2") {
                +msg
            }
        }
    }
}

inline fun <reified T: Any?> FlowContent.InputComp(
    input: InputField<T>,
    label: String,
    placeholder: String,
    list: String? = null,
    type: InputType,
    wrapperClasses: String
) {
    val state: FormState<*> = locate()

    launch {
        input.valueAttr.drop(1).collectLatest {
            input.touch()
            state.validateTouched()
        }
    }

    div("w-full px-3 mb-6 $wrapperClasses") {
        label("block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2") {
            htmlFor = label
            +label
        }
        input(name = input.name, type = type) {
            this.placeholder = placeholder

            list?.let {
                this.list = it
            }

            classesOf(input.error) { error ->
                +"appearance-none block w-full bg-gray-200 text-gray-700 border rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white"
                if (error != null)
                    +"border-red-500"
                else
                    +"border-gray-400"
            }

            attributeOf("value", input.valueAttr) { value ->
                value?.toString() ?: ""
            }
//            onMounted { elem ->
//                input.valueAttr.value = InputField.convertValue(elem.getAttribute("value") ?: "")
//            }
            onInput { _, elem ->
                input.valueAttr.value = InputField.convertValue(elem.getAttribute("value") ?: "")
            }
        }
        divOf(input.error) { msg ->
            if (msg == null) return@divOf
            p("text-red-500 text-xs italic") {
                +msg
            }
        }
    }
}

fun FlowContent.CheckBoxComp(
    input: InputField<Boolean>,
    text: String? = null,
    body: DomElement? = null
) {
    val state: FormState<*> = locate()

    launch {
        input.valueAttr.drop(1).collectLatest {
            input.touch()
            state.validateTouched()
        }
    }

    div("flex items-start") {
        if (text != null) {
            onClick {
                input.valueAttr.value = input.valueAttr.value?.not()
            }
        }

        div("flex items-center h-5") {
            input(classes = "focus:ring-green-500 h-5 w-5 text-green-500 border-gray-300 rounded") {
                name = input.name
                type = InputType.checkBox

                attributeOf("checked", input.valueAttr) { value ->
                    value.toString()
                }
            }
        }
        div("ml-3 text-sm") {
            text?.let {
                span("form-input") {
                    +text
                }
            }
            body?.let {
                body()
            }
        }
    }
    divOf(input.error) { msg ->
        if (msg == null) return@divOf
        p("text-red-500 text-xs italic") {
            +msg
        }
    }
}
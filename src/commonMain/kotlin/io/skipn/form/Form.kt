package io.skipn.form

import io.skipn.FileData
import io.skipn.FormEndpoint
import io.skipn.builder.launch
import io.skipn.events.*
import io.skipn.observers.classesOf
import io.skipn.observers.divOf
import io.skipn.provide.locate
import io.skipn.provide.pin
import io.skipn.utils.buildApiJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.html.*
import kotlin.reflect.KProperty

expect suspend inline fun <reified RESP: Any> postForm(state: FormState): RESP

class FormState(val endpoint: FormEndpoint<*, *>) {

    val inputs = hashMapOf<String, Input<*>>()

    fun addInput(input: Input<*>) {
        inputs[input.name] = input
    }

    class Input<T: Any?>(
        val name: String,
        val required: Boolean,
        val type: InputType,
        val valueAttr: MutableStateFlow<T?>,
        val error: MutableStateFlow<String?>,
    ) {

        var touched = false
        private set

        fun touch() {
            if (touched) return
            touched = true
        }

        companion object {

            inline fun <reified T: Any?> create(input: KProperty<T>, password: Boolean): Input<T> {
                val required = null !is T
                val error = MutableStateFlow<String?>(null)

                val type = if (password) InputType.password
                else convertType<T>()

                return Input(
                    input.name,
                    required,
                    type,
                    MutableStateFlow(null),
                    error,
                )
            }

            inline fun <reified T> convertType(): InputType {
                return when(T::class) {
                    Int::class -> InputType.number
                    Float::class -> InputType.number
                    Double::class -> InputType.number
                    LocalDateTime::class -> InputType.dateTimeLocal
                    FileData::class -> InputType.file
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
//                FileData::class -> FileData()
                    else -> valueString as T
                }
            }
        }
    }

    fun validateTouched() {
        val custom = validateCustom()

        inputs.filter { it.value.touched }.forEach {
            val input = it.value

            if (input.required && input.valueAttr.value == null) {
                input.error.value = "Kötelező megadni"
            }
            else {
                val customError = custom?.get(it.key)
                input.error.value = customError
            }
        }
    }

    fun validateAll(): Boolean {
        var valid = true

        val custom = validateCustom()

        inputs.forEach {
            val input = it.value

            input.touch()

            if (input.required && input.valueAttr.value == null) {
                input.error.value = "Kötelező megadni!"
                if (valid) valid = false
            }
            else {
                val customError = custom?.get(it.key)
                input.error.value = customError

                if (customError != null && valid)
                    valid = false
            }
        }
        return valid
    }

    private fun validateCustom() : Map<String, String>? {
        val validator = endpoint.validator ?: return null

        // Create snapshot of every input value
        val snapshot = inputs.mapValues {
            it.value.valueAttr.value
        }

        // Perform custom logic against form snapshot
        val customValidator = FormValidator(snapshot).apply {
            validator()
        }

        return customValidator.validationError
    }
}

class FormBuilder(val elem: FORM, val state: FormState) {

    inline fun <reified T: Any?> KProperty<T>.Input(
        label: String,
        placeholder: String,
        password: Boolean = false
    ) {
        val input = FormState.Input.create(this, password).also {
            state.addInput(it)
        }

        with(elem) {
            InputComp(input, label, placeholder, "w-full")
        }
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

        postForm<Any>(state)
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
        crossinline body: FormBuilder.() -> Unit,
        crossinline onSuccess: (RESP) -> Unit
) {
    form(classes = "w-full mx-auto", method = FormMethod.post, encType = FormEncType.multipartFormData) {
        preventDefaultSubmit()

        val state = pin(FormState(endpoint))
        val builder = FormBuilder(this, state)

//        val handler = attachSubmitHandler(endpoint, builder, onSuccess)
//        builder.submitHandler = handler



        builder.apply {
            body()
        }
    }
}

inline fun <reified T: Any?> FlowContent.InputComp(
    input: FormState.Input<T>,
    label: String,
    placeholder: String,
    wrapperClasses: String
) {
    val state: FormState = locate()

    div("w-full px-3 mb-6 $wrapperClasses") {
        label("block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2") {
            htmlFor = label
            +label
        }
        input(name = input.name, type = input.type) {
            this.placeholder = placeholder

            classesOf(input.error) { error ->
                +"appearance-none block w-full bg-gray-200 text-gray-700 border rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white"
                if (error != null)
                    +"border-red-500"
                else
                    +"border-gray-400"
            }

            onMounted { elem ->
                input.valueAttr.value = FormState.Input.convertValue(elem.getAttribute("value") ?: "")
            }
            onInput { _, elem ->
                input.touch()

                input.valueAttr.value = FormState.Input.convertValue(elem.getAttribute("value") ?: "")
                state.validateTouched()
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
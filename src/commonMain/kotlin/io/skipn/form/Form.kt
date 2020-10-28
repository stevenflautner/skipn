package io.skipn.form

import io.skipn.Endpoint
import io.skipn.FileData
import io.skipn.elements.DomElement
import io.skipn.events.onInput
import io.skipn.events.onMounted
import io.skipn.events.submitHandler
import io.skipn.notifiers.Stateful
import io.skipn.notifiers.StatefulValue
import io.skipn.observers.divOf
import io.skipn.observers.classes
import io.skipn.provide.locate
import io.skipn.provide.pinpoint
import kotlinx.datetime.LocalDateTime
import kotlinx.html.*
import kotlin.reflect.KProperty

class FormState : Stateful() {
    val inputs = hashMapOf<String, Input>()

    fun validateAll(): Boolean {
        var valid = true
        inputs.values.forEach {
            if (!it.validator() && valid)
                valid = false
        }
        return valid
    }

    fun addInput(input: Input) {
        inputs[input.name] = input
    }

    class Input(
        val name: String,
        val required: Boolean,
        val type: InputType,
        val value: StatefulValue<String>,
        val error: StatefulValue<String?>,
        val validator: () -> Boolean
    ) {

        companion object {
            inline fun <reified T: Any?> create(name: String, noinline validate: ((T) -> String?)?): Input {
                val required = null !is T
                val value = StatefulValue("")
                val error = StatefulValue<String?>(null)

                return Input(
                    name,
                    required,
                    convertType<T>(),
                    value,
                    error,
                    createValidator(required, value, error, validate)
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
            inline fun <reified T: Any?> createValidator(
                    required: Boolean,
                    value_: StatefulValue<String>,
                    error: StatefulValue<String?>,
                    noinline validate: ((T) -> String?)?
            ) : () -> Boolean = v@ {
                val value = value_.getValue()

                if (required && value == "") {
                    error.setValue("Kötelező megadni")
                    return@v false
                }
                if (validate == null) {
                    error.setValue(null)
                    return@v true
                }

                if (required && value == "") {
                    error.setValue("Kötelező megadni")
                    return@v false
                }

                val validateError = validate(value as T)
                if (validateError == null) {
                    error.setValue(null)
                    return@v true
                }

                error.setValue(validateError)
                return@v false
            }
        }
    }
}

class FormBuilder {

    val elements = arrayListOf<DomElement>()
    lateinit var submitHandler: () -> Unit

    class Row {
        val elements = arrayListOf<DomElement>()

        fun element(element: DomElement) {
            elements.add(element)
        }

        fun FlowContent.build() {
            div("flex flex-wrap md:mb-6") {
                elements.forEach {
                    it()
                }
            }
        }

        inline fun <reified T: Any?> KProperty<T>.Input(
            label: String,
            placeholder: String,
            noinline validate: ((T) -> String?)? = null
        ) = element {
            val formState : FormState = locate()
            val input = FormState.Input.create(this@Input.name, validate)
            formState.addInput(input)

            InputComp(input, label, placeholder, "md:w-1/${elements.size} md:mb-0")
        }
    }

    fun element(element: DomElement) {
        elements.add(element)
    }

    inline fun <reified RESP: Any> FlowContent.build(endpoint: Endpoint<*, RESP>, crossinline onSuccess: (RESP) -> Unit) {
        form(classes = "w-full mx-auto", method = FormMethod.post, encType = FormEncType.multipartFormData) {
            val formState = pinpoint(FormState())

            submitHandler = submitHandler(endpoint, formState, onSuccess)

            elements.forEach {
                it()
            }
        }
    }

    fun Row(row: Row.() -> Unit) = element {
        Row().apply(row).apply {
            build()
        }
    }

    inline fun <reified T: Any?> KProperty<T>.Input(
        label: String,
        placeholder: String,
        noinline validate: ((T) -> String?)? = null
    ) = element {
        val input = FormState.Input.create(this@Input.name, validate)
        val formState : FormState = locate()
        formState.addInput(input)

        InputComp(input, label, placeholder, "w-full")
    }

    fun Submit(text: String) = element {
        button(classes = "mx-3 p-2 rounded bg-green-400 hover:bg-green-500 shadow-lg") {
            span("text-lg text-white") { +text }
        }
    }

    fun submitForm() {
        submitHandler()
    }
}

inline fun <reified RESP: Any> FlowContent.Form(
        endpoint: Endpoint<*, RESP>,
        form: FormBuilder.() -> Unit,
        crossinline onSuccess: (RESP) -> Unit
) = FormBuilder().apply(form).apply {
    build(endpoint, onSuccess)
}

fun FlowContent.InputComp(
        input: FormState.Input,
        label: String,
        placeholder: String,
        wrapperClasses: String
) {
    div("w-full px-3 mb-6 $wrapperClasses") {
        label("block uppercase tracking-wide text-gray-700 text-xs font-bold mb-2") {
            htmlFor = label
            +label
        }
        input(name = input.name, type = input.type) {
            this.placeholder = placeholder

            classes(input.error) {
                ("appearance-none block w-full bg-gray-200 text-gray-700 border rounded py-3 px-4 leading-tight focus:outline-none focus:bg-white " +
                        if (it != null) "border-red-500" else "border-gray-400")
            }

            onMounted { elem ->
                input.value.setValue(elem.getAttribute("value") ?: "")

            }
            onInput { _, elem ->
                input.value.setValue(elem.getAttribute("value") ?: "")
                input.validator()
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
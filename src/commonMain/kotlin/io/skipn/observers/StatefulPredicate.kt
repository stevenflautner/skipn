package io.skipn.observers

import io.skipn.notifiers.StatefulNotifier
import io.skipn.notifiers.StatefulValue

typealias StatefulPredicate = () -> Boolean

typealias StatefulType = () -> Unit
typealias StatefulValueType <T> = (T) -> Unit

class StatefulBuilderPredicate(val stateful: StatefulNotifier<StatefulType>, val predicate: StatefulPredicate)
class StatefulBuilderValuePredicate<T: Any>(val stateful: StatefulValue<T>, val predicate: StatefulPredicate)
class StatefulBuilderValuePredicate1<T: Any, D>(val stateful: StatefulValue<T>, val predicate: (T) -> D?)




interface StatefulFilter<T, R> {
    var child: StatefulFilter<R, *>?
    fun run(builder: StatefulFilterBuilder<*, *>, parentResult: T) : Any
}

class StatefulTransformFilter<T, R: Any>(
    val transform: (T) -> R,
) : StatefulFilter<T, R> {

    override var child: StatefulFilter<R, *>? = null

    fun <CR: Any>  map(transform: (R) -> CR): StatefulTransformFilter<R, CR> {
        val filter = StatefulTransformFilter(transform)
        child = filter
        return filter
    }
    fun condition(condition: (R) -> Boolean): StatefulConditionFilter<R> {
        val filter = StatefulConditionFilter(condition)
        child = filter
        return filter
    }

    override fun run(builder: StatefulFilterBuilder<*, *>, parentResult: T): Any {
        val result = transform(parentResult)

        val child = child ?: return result
        return child.run(builder, result)
    }
}

class StatefulConditionFilter<T: Any>(
    val condition: (T) -> Boolean
) : StatefulFilter<T, T> {

    override var child: StatefulFilter<T, *>? = null

    fun <CR: Any> map(transform: (T) -> CR): StatefulTransformFilter<T, CR> {
        val filter = StatefulTransformFilter(transform)
        child = filter
        return filter
    }
    fun condition(condition: (T) -> Boolean): StatefulConditionFilter<T> {
        val filter = StatefulConditionFilter(condition)
        child = filter
        return filter
    }

    override fun run(builder: StatefulFilterBuilder<*, *>, parentResult: T): Any {
        if (!condition(parentResult))
            builder.fail()

        val child = child ?: return parentResult
        return child.run(builder, parentResult)
    }
}

class StatefulFilterBuilder<T: Any, R: Any>(
    val stateful: StatefulValue<T>,
    init: StatefulFilterBuilder<T, *>.() -> StatefulFilter<*, R>) {

    var conditionsPassed = true
    var child: StatefulFilter<T, *>? = null

    init {
        init()
    }

    fun map(transform: (T) -> Any): StatefulTransformFilter<T, Any> {
        val filter = StatefulTransformFilter(transform)
        child = filter
        return filter
    }
    fun condition(condition: (T) -> Boolean): StatefulConditionFilter<T> {
        val filter = StatefulConditionFilter(condition)
        child = filter
        return filter
    }

    fun fail() {
        if (conditionsPassed)
            conditionsPassed = false
    }

    fun runFilter() : R? {
        conditionsPassed = true
        return child?.run(this, stateful.getValue()) as? R
    }
}

fun <T: Any, R: Any> StatefulValue<T>.filter(filter: StatefulFilterBuilder<T, *>.() -> StatefulFilter<*, R>)
        = StatefulFilterBuilder(this, filter)
//fun <T> StatefulFilterBuilder<T>.condition(condition: (T) -> Boolean)
//        = filters.add(StatefulConditionFilter(this, condition))
//fun <T, R> StatefulFilterBuilder<T>.map(transform: (T) -> R)
//        = filters.add(StatefulTransformFilter(builder, transform))

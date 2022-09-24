package site.starsone.kxorm.condition

import kotlin.reflect.KProperty
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 *
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/23 14:34
 *
 */
class ConditionWhere<R : Any>(val kProperty: KProperty<R>, val op: String, val value: R) {
    fun toSql(): String {
        val opStr = when (op) {
            "eq" -> "="
            "gt" -> ">="
            "like" -> "like"
            else -> ""
        }
        return if (kProperty.returnType.withNullability(false) == String::class.starProjectedType) {
            "${kProperty.name} $opStr '$value'"
        } else {
            "${kProperty.name} $opStr $value"
        }

    }

    infix fun <T : Any> and(conditionWhere: ConditionWhere<T>): List<ConditionWhere<out Any>> {
        return listOf(this, conditionWhere)
    }

}

infix fun <R : Any> KProperty<R>.eq(s: R): ConditionWhere<R> {
    return ConditionWhere(this, "eq", s)
}

infix fun <R : Any> KProperty<R>.gt(s: R): ConditionWhere<R> {
    return ConditionWhere(this, "gt", s)
}

infix fun <R : Any> KProperty<R>.like(s: R): ConditionWhere<R> {
    return ConditionWhere(this, "like", s)
}

package site.starsone.kxorm.condition

import kotlin.reflect.KProperty


/**
 * 排序
 *
 * @param R
 * @property kProperty
 * @property orderType 排序类型,Type.asc或Type.desc
 * @constructor Create empty Condition order by
 */
class ConditionOrderBy<R : Any>(val kProperty: KProperty<R>, val orderType: Type) {
    fun toOrderBySql(): String {
        val orderByStr = when (orderType) {
            Type.ASC -> {
                "asc"
            }
            else->{
                "desc"
            }
        }
        return  "${kProperty.name} $orderByStr"
    }

    enum class Type{
        ASC,DESC
    }
}

fun <R : Any> KProperty<R>.orderByAsc(): ConditionOrderBy<R> {
    return ConditionOrderBy(this, ConditionOrderBy.Type.ASC)
}

fun <R : Any> KProperty<R>.orderByDesc(): ConditionOrderBy<R> {
    return ConditionOrderBy(this, ConditionOrderBy.Type.DESC)
}


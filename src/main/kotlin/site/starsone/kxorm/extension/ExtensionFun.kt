package site.starsone.kxorm

import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 * 全局通用的扩展方法
 *
 */


/**
 * 扩展方法,快捷将string转为Pair类型
 *
 * @param T
 * @param kclass
 * @return
 */
fun <T : Any> String.toFileNameType(kclass: KClass<T>): Pair<String, KClass<out T>> {
    return Pair(this, kclass)
}

/**
 * 判断Ktype参数类型是否与类相同
 *
 * @param kClass
 * @return
 */
fun KType.isSameClass(kClass: KClass<out Any>): Boolean {
    return withNullability(false) == kClass.starProjectedType
}

fun Date.toFormatString(pattern: String="yyyy-MM-dd HH:mm:ss") :String{
    return SimpleDateFormat(pattern).format(this)
}







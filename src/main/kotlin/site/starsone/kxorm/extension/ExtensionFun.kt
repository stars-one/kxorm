package site.starsone.kxorm

import kotlin.reflect.KClass

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











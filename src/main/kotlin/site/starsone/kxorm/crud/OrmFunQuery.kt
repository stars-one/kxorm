package site.starsone.kxorm.crud

import org.h2.jdbc.JdbcResultSet
import site.starsone.kxorm.toFileNameType
import java.io.File
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KType
import kotlin.reflect.full.*

/**
 * 查询的相关方法工具类
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/17 14:46
 *
 */
object OrmFunQuery {
    /**
     * 获取查询后的实体列表数据
     *
     * @param T
     * @param resultSet
     * @param kclass
     * @return
     */
    private fun <T : Any> getQueryList(resultSet: JdbcResultSet, kclass: KClass<T>): List<T> {
        val map = hashMapOf<String, Pair<KType, KMutableProperty<Any>>>()

        val defaultValueList = arrayListOf<Any>()
        kclass.primaryConstructor?.parameters?.forEach {
            map[it.name!!] =
                Pair(
                    it.type,
                    kclass.declaredMemberProperties.find { pro -> pro.name == it.name } as KMutableProperty<Any>)
            if (it.type == String::class.starProjectedType) {
                defaultValueList.add("")
            }
            if (it.type == Int::class.starProjectedType) {
                defaultValueList.add(0)
            }
            if (it.type == File::class.starProjectedType) {
                defaultValueList.add(File(""))
            }
        }

        val list = arrayListOf<T>()
        // 遍历打印数据
        while (resultSet.next()) {
            //实例化对象
            val dataClassObject = kclass.primaryConstructor?.call(*defaultValueList.toTypedArray())

            map.forEach { t, v ->
                val type = v.first
                val kMutableProperty = v.second
                //获取查询后的数据,并赋值给对应
                if (type == File::class.starProjectedType) {
                    val result = getFieldValue(resultSet, t.toFileNameType(String::class))
                    kMutableProperty.setter.call(dataClassObject, File(result))
                }
                if (type == String::class.starProjectedType) {
                    val result = getFieldValue(resultSet, t.toFileNameType(String::class))
                    kMutableProperty.setter.call(dataClassObject, result)
                }
                if (type == Int::class.starProjectedType) {
                    val result = getFieldValue(resultSet, t.toFileNameType(Int::class))
                    println(result)
                    kMutableProperty.setter.call(dataClassObject, result)
                }
            }
            list.add(dataClassObject!!)
        }

        return list
    }

    /**
     * 从Jdbc查询的结果中获取字段数值
     *
     * @param T
     * @param resultSet jdbc的查询结果
     * @param fileNameType Pair<String,KClass<out T> 字段的名及字段类型
     * @return
     */
    private fun <T : Any> getFieldValue(resultSet: JdbcResultSet, fileNameType: Pair<String, KClass<out T>>): T? {
        val fieldName = fileNameType.first
        val kclass = fileNameType.second
        val method = getMethod(resultSet::class, kclass)
        val result = method?.call(resultSet, fieldName)
        return result
    }

    /**
     * 获取ResultSet对应获取类型数值的方法(getString,getInt等)
     *
     * @param T
     * @param resultSetKclass
     * @param kClass
     * @return
     */
    private fun <T : Any> getMethod(resultSetKclass: KClass<out JdbcResultSet>, kClass: KClass<out T>): KFunction<T>? {
        val methodName = when {
            kClass == String::class -> "getString"
            kClass == Int::class -> "getInt"
            else -> ""
        }
        return resultSetKclass.declaredMemberFunctions.find {
            val isMethodNameTrue = it.name == methodName
            if (isMethodNameTrue) {
                //参数
                val flag = it.parameters.any { param ->
//                    println("${it.type.withNullability(false)} ${String::class.createType()}")
                    param.type.withNullability(false) == String::class.createType()
                }
                isMethodNameTrue && flag
            } else {
                false
            }
        } as KFunction<T>?

    }

    /**
     * 查询列表数据
     *
     * @param conn
     * @param kclass
     * @return
     */
    fun <T : Any> queryListByClass(conn: Connection, kclass: KClass<T>): List<T> {
        val statement = conn.createStatement()
        // 查询数据

        val resultSet = statement.executeQuery("select * from ${kclass.simpleName}") as JdbcResultSet
        val queryList = getQueryList(resultSet, kclass)
        return queryList
    }

    /**
     * 查询列表数据
     *
     * @param conn
     * @param kclass
     * @return
     */
    fun <T : Any> queryListByCondition(conn: Connection, kclass: KClass<T>, condition: String): List<T> {
        val statement = conn.createStatement()
        // 查询数据

        val sql = "select * from ${kclass.simpleName} where $condition"
        println("查询的sql语句: ${sql}")
        val resultSet = statement.executeQuery(sql) as JdbcResultSet
        val queryList = getQueryList(resultSet, kclass)
        return queryList
    }
}

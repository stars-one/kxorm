package site.starsone.kxorm.crud

import java.io.File
import java.sql.Connection
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 * 插入的相关方法工具类
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/17 14:46
 *
 */
object OrmFunInsert {
    fun <T : Any> insert(conn: Connection, data: T): Int {
        //类转为具体对应创表sql
        val kclass = data::class

        val paramList = arrayListOf<String>()
        val valueList = arrayListOf<Any>()
        val map = hashMapOf<String, KType>()
        kclass.primaryConstructor?.parameters?.forEach {
            map[it.name!!] = it.type
        }
        kclass.declaredMemberProperties.forEach {
            println(it.name)
            paramList.add(it.name)
            if (it.returnType.withNullability(false) == String::class.starProjectedType ||it.returnType.withNullability(false) == File::class.starProjectedType) {
                //string类型和file类型需要特殊处理
                valueList.add("""'${it.getter.call(data).toString()}'""")
            } else {
                valueList.add(it.getter.call(data).toString())
            }
        }
        val paramStr = paramList.joinToString()
        val valueStr = valueList.joinToString()
        val sql = """
        INSERT INTO ${kclass.simpleName} ( ${paramStr}) VALUES(${valueStr})
    """.trimIndent()
        val statement = conn.createStatement()
        val rows = statement.executeUpdate(sql)
        statement.close()
        return rows
    }

    fun <T : Any> insert(conn: Connection, data: List<T>): Int {
        TODO("批量插入数据")
    }
}

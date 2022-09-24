package site.starsone.kxorm.crud

import site.starsone.kxorm.condition.ConditionWhere
import java.io.File
import java.sql.Connection
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 * 更新的相关方法工具类
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/17 14:46
 *
 */
object OrmFunUpdate {
    /**
     * 更新数据(新数据强制覆盖旧数据)
     *
     * @param T
     * @param conn 数据库连接
     * @param data 数据
     * @param condition where条件(不包含where关键字)
     * @return
     */
    fun <T : Any> updateForce(conn: Connection, data: T, condition: ConditionWhere<out Any>): Int {
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
            if (it.returnType.withNullability(false) == String::class.starProjectedType || it.returnType.withNullability(
                    false
                ) == File::class.starProjectedType
            ) {
                //string类型和file类型需要特殊处理
                valueList.add("""'${it.getter.call(data).toString()}'""")
            } else {
                valueList.add(it.getter.call(data).toString())
            }
        }
        val sb = StringBuilder("update ${kclass.simpleName} set ")

        val setSqlList = arrayListOf<String>()
        paramList.forEachIndexed { index, param ->
            //排查where条件的的列
            if (condition.kProperty.name != param) {
                val value = valueList[index]
                setSqlList.add("$param = $value")
            }
        }
        sb.append(setSqlList.joinToString())
        sb.append(" where ${condition.toSql()}")
        val sql = sb.toString()
        println("更新sql: $sql")
        val statement = conn.createStatement()
        val rows = statement.executeUpdate(sql)
        statement.close()
        return rows
    }

}

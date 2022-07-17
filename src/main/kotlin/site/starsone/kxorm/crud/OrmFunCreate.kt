package site.starsone.kxorm.crud

import site.starsone.kxorm.SqlParam
import java.io.File
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

/**
 * 创表相关方法
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/17 14:46
 *
 */
object OrmFunCreate {
    /**
     * 创表语句
     *
     * @param kclass
     * @return
     */
    fun createSql(kclass: KClass<out Any>): String {
        //类转为具体对应创表sql

        val map = hashMapOf<String, KType>()
        kclass.primaryConstructor?.parameters?.forEach {
            map[it.name!!] = it.type
        }
        println(map.toString())


        val tableName = kclass.simpleName!!

        val paramList = arrayListOf<Pair<String, String>>()

        val sqlparm = SqlParam(tableName, paramList)


        map.forEach { (t, v) ->
            if (v == String::class.starProjectedType || v == File::class.starProjectedType) {
                paramList.add(Pair(t, "varchar(500)"))
            }
            if (v == Int::class.starProjectedType) {
                paramList.add(Pair(t, "INTEGER"))
            }
            //todo 其他类型的适配，转驼峰，注解，主键
        }

        val paramstring = sqlparm.paramList.joinToString {
            it.first + " " + it.second
        }
        println(paramstring)
        val sql = """
        CREATE TABLE ${sqlparm.tableName}( $paramstring )
    """.trimIndent()
        return sql
    }

    /**
     * 根据类创建表
     *
     * @param conn
     * @param kclass
     */
    fun createTableByClass(conn: Connection, kclass: KClass<out Any>) {
        val statement = conn.createStatement()
        val sql = createSql(kclass)
        println("创表语句: $sql")
        statement.executeUpdate(sql)
        statement.close()
    }

    /**
     * 判断表是否存在 参考连接(https://www.thinbug.com/q/19518265)
     *
     * @param conn
     * @param tableName
     * @return
     */
    fun isTableExist(conn: Connection, tableName: String): Boolean {
        //注意这里要转大写判断
        val rset = conn.metaData.getTables(null, null, tableName.toUpperCase(), null)
        val flag = rset.next()
        return flag
    }
}

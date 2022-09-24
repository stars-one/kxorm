package site.starsone.kxorm.crud

import java.sql.Connection
import kotlin.reflect.KClass

/**
 * 更新的相关方法工具类
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/17 14:46
 *
 */
object OrmFunDelete {
    /**
     * 删除单条数据
     *
     * @param conn
     * @param kclass
     * @return
     */
    fun <T : Any> delete(conn: Connection, kclass: KClass<T>, condition: String): Int {
        val statement = conn.createStatement()
        // 查询数据

        val sql = "delete from ${kclass.simpleName} where $condition"
        println("删除sql语句: ${sql}")
        val rows = statement.executeUpdate(sql)
        statement.close()
        return rows
    }

    /**
     * 删除表的所有数据
     *
     * @param conn
     * @param kclass
     * @return
     */
    fun <T : Any> deleteAll(conn: Connection, kclass: KClass<T>): Int {
        val statement = conn.createStatement()
        // 查询数据
        val sql = "delete from ${kclass.simpleName}"
        println("删除sql语句: ${sql}")
        val rows = statement.executeUpdate(sql)
        return rows
    }
}

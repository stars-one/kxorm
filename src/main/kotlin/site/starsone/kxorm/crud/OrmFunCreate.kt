package site.starsone.kxorm.crud

import site.starsone.kxorm.bean.TableInfo
import site.starsone.kxorm.db.KxDb
import java.sql.Connection
import kotlin.reflect.KClass

/**
 * 创表相关方法
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/17 14:46
 *
 */
object OrmFunCreate {
    /**
     * 根据tableInfo信息生成创表sql
     *
     * @param tableInfo
     * @return
     */
    fun createSqlByTableInfo(tableInfo: TableInfo): String {
        val tableName = tableInfo.tableName
        val paramList = arrayListOf<Pair<String, String>>()
        tableInfo.columns.forEach {
            val pair = Pair(it.columnName, it.columnType)
            paramList.add(pair)
        }

        val paramstring = paramList.joinToString {
            it.first + " " + it.second
        }

        val sql = """
            CREATE TABLE ${tableName}( $paramstring )
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
        createTableByClassName(conn,kclass.qualifiedName!!)
    }

    /**
     * 根据类名创建表
     *
     * @param conn
     * @param className 全类名
     */
    fun createTableByClassName(conn: Connection, className:String) {
        val statement = conn.createStatement()
        val tableInfo = KxDb.kxDbConnConfig.registerClassList[className]
        if (tableInfo != null) {
            val sql =  createSqlByTableInfo(tableInfo)
            println("创表语句: $sql")
            statement.executeUpdate(sql)
            statement.close()
        }
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

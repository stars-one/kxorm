package site.starsone.kxorm.crud

import site.starsone.kxorm.db.KxDb
import java.io.File
import java.sql.Connection
import kotlin.reflect.full.declaredMemberProperties
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
    /**
     * 插入单条数据
     *
     * @param T
     * @param conn
     * @param data
     * @return
     */
    fun <T : Any> insert(conn: Connection, data: T): Int {
        val sql = generateInsertSql(data)
        if (sql.isNotBlank()) {
            val statement = conn.createStatement()
            val rows = statement.executeUpdate(sql)
            statement.close()
            return rows
        }
        return 0
    }

    /**
     * 批量插入多条数据
     *
     * @param T
     * @param conn
     * @param data
     * @return
     */
    fun <T : Any> insert(conn: Connection, data: List<T>): Int {
        val statement = conn.createStatement()
        data.forEach {
            val sql = generateInsertSql(it)
            if (sql.isNotBlank()) {
                statement.addBatch(sql)
            }
        }
        val arr = statement.executeBatch()
        statement.close()
        return arr.sum()
    }

    /**
     * 将实体类转为对应的insert语句
     *
     * @param T
     * @param data
     * @return
     */
    private fun <T : Any> generateInsertSql(data: T): String {
        //类转为具体对应创表sql
        val kclass = data::class

        val tableInfo = KxDb.kxDbConnConfig.getTableInfoByClass(kclass)
        if (tableInfo != null) {
            val tableName = tableInfo.tableName
            val columns = tableInfo.columns

            val paramList = arrayListOf<String>()
            val valueList = arrayListOf<Any>()

            kclass.declaredMemberProperties.forEach {
                //通过实体字段名找到对应的列信息
                val columnInfo = columns.firstOrNull { column ->
                    column.fieldName == it.name
                }
                //数据库的参数名使用列信息里的columnName字段
                paramList.add(columnInfo!!.columnName)

                //获取实体数据类对象的数值
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
            val paramStr = paramList.joinToString()
            val valueStr = valueList.joinToString()

            val sql = """
                INSERT INTO ${tableName} ( ${paramStr}) VALUES(${valueStr})
            """.trimIndent()
            return sql
        } else {
            println("tableInfo为空!!")
        }
        return ""
    }
}

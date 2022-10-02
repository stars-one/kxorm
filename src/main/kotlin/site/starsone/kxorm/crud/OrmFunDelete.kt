package site.starsone.kxorm.crud

import site.starsone.kxorm.db.KxDb
import java.io.File
import java.sql.Connection
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 * 更新的相关方法工具类
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/17 14:46
 *
 */
object OrmFunDelete {

    /**
     * 删除数据
     *
     * @param conn
     * @param kclass
     * @param condition where条件语句(不用包含where关键字)
     *
     * @return 删除操作的影响行数
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
        // 删除数据
        val sql = "delete from ${kclass.simpleName}"
        println("删除sql语句: ${sql}")
        val rows = statement.executeUpdate(sql)
        return rows
    }

    /**
     * 删除某条数据
     *
     * @param conn
     * @param kclass
     * @return
     */
    fun <T : Any> delete(conn: Connection, bean: T): Int {
        val statement = conn.createStatement()
        val sql = generateDeleteSql(bean)
        if (sql.isNotBlank()) {
            val rows = statement.executeUpdate(sql)
            return rows
        } else {
            println("生成删除sql为空")
        }
        return 0
    }

    /**
     * 删除某条数据
     *
     * @param conn
     * @param kclass
     * @return
     */
    fun <T : Any> delete(conn: Connection, bean: List<T>): Int {
        if (bean.isNotEmpty()) {
            val statement = conn.createStatement()
            bean.forEach {
                val sql = generateDeleteSql(it)
                statement.addBatch(sql)
            }
            val arr = statement.executeBatch()
            return arr.sum()
        }
        return 0
    }


    private fun <T : Any> generateDeleteSql(bean: T): String {
        val kclass = bean::class
        val tableInfo = KxDb.kxDbConnConfig.getTableInfoByClass(kclass)
        val pkColumn = tableInfo?.getPkColumnInfo()
        val kProperty = kclass.declaredMemberProperties.find { it.name == pkColumn?.fieldName }

        if (kProperty != null) {
            val whereCondition =
                if (kProperty.returnType.withNullability(false) == String::class.starProjectedType || kProperty.returnType.withNullability(
                        false
                    ) == File::class.starProjectedType
                ) {
                    //string类型和file类型需要特殊处理
                    """'${kProperty.getter.call(bean).toString()}'"""
                } else {
                    kProperty.getter.call(bean).toString()
                }

            // 删除数据
            val sql = "delete from ${tableInfo?.tableName} where ${pkColumn?.columnName} = ${whereCondition}"
            return sql
        } else {
            println("删除失败,没有找到主键,请检查${kclass.qualifiedName}是否设置了主键注解!!")
        }
        return ""
    }
}

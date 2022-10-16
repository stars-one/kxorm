package site.starsone.kxorm.crud

import com.github.yitter.idgen.YitIdHelper
import site.starsone.kxorm.annotation.PkType
import site.starsone.kxorm.db.KxDb
import site.starsone.kxorm.isSameClass
import site.starsone.kxorm.toFormatString
import java.io.File
import java.sql.Connection
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import kotlin.reflect.full.declaredMemberProperties

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
                val columnInfo = columns.first { column ->
                    column.fieldName == it.name
                }
                //数据库的参数名使用列信息里的columnName字段
                paramList.add(columnInfo.columnName)

                //如果当前列是主键
                //todo 插入语句兼容其他类型

                //获取实体数据类对象的数值
                val columnReturnType = it.returnType
                var value = when {
                    //string类型和file类型需要特殊处理
                    columnReturnType.isSameClass(String::class) -> {
                        """'${it.getter.call(data).toString()}'"""
                    }
                    columnReturnType.isSameClass(File::class) -> {
                        """'${it.getter.call(data).toString()}'"""
                    }
                    //日期类型
                    columnReturnType.isSameClass(ChronoLocalDateTime::class) -> {
                        val dataResult = it.getter.call(data)
                        """'${it.getter.call(data).toString()}'"""
                    }
                    columnReturnType.isSameClass(Date::class) -> {
                        val result =  it.getter.call(data) as Date
                        """'${result.toFormatString()}'"""
                    }

                    else -> it.getter.call(data).toString()
                }

                //如果是主键,则使用自动生成ID
                if (columnInfo.isPk) {
                    val idResult = when (columnInfo.pkType) {
                        PkType.ASSIGN_ID -> YitIdHelper.nextId().toString()
                        PkType.ASSIGN_UUID -> UUID.randomUUID().toString()
                        PkType.NONE -> ""
                    }

                    //选用了对应的ID生成方式,才会修改数值
                    if (idResult.isNotBlank()) {
                       value = idResult
                    }
                }

                valueList.add(value)
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



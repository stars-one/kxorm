package site.starsone.kxorm

import org.h2.jdbc.JdbcResultSet
import site.starsone.kxorm.crud.OrmFunCreate
import site.starsone.kxorm.crud.OrmFunInsert
import site.starsone.kxorm.crud.OrmFunQuery
import java.io.File
import java.sql.DriverManager
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.withNullability


/**
 *
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/14 22:54
 *
 */
fun main() {

    val kclass =ItemData::class

    val dbUrl = "jdbc:h2:D:/temp/h2db/test"
    val user = ""
    val pwd = ""

    val conn = DriverManager.getConnection(dbUrl, user, pwd)
    if (!OrmFunCreate.isTableExist(conn, kclass.simpleName!!)) {
        //创表
        OrmFunCreate.createTableByClass(conn, kclass)
    }

    //插入
    repeat(3) {
        val data = ItemData(File("D:\\temp\\myd.png"),"D:\\temp","myd.png","https://xx.com","https://jkjk","20.4MB","2020-12-1$it")
        OrmFunInsert.insert(conn,data)
    }

    //查询
    val queryListByClass = OrmFunQuery.queryListByClass(conn, kclass)
    println(queryListByClass.size)
    println(queryListByClass.toString())

}



fun main1() {
    val dbUrl = "jdbc:h2:D:/temp/h2db/test"
    val user = ""
    val pwd = ""

    val conn = DriverManager.getConnection(dbUrl, user, pwd)

    println(OrmFunCreate.isTableExist(conn, "USER_INF"))
    // 获取数据库操作对象statement
    val statement = conn.createStatement()

    // 建库
    statement.execute("DROP TABLE IF EXISTS USER_INF")
    // 建表
    statement.execute("CREATE TABLE USER_INF(id INTEGER PRIMARY KEY, name VARCHAR(100), sex VARCHAR(2))")
    // 插入数据操作
    statement.executeUpdate("INSERT INTO USER_INF VALUES(2, 'tom2', '男') ")


    // 查询数据
    val resultSet = statement.executeQuery("select * from USER_INF") as JdbcResultSet

    // 遍历打印数据
    while (resultSet.next()) {
        println(
            resultSet.getInt("id").toString() + ", "
                    + resultSet.getString("name") + ", "
                    + resultSet.getString("sex")

        )
        val resultSetKclass = resultSet::class

        resultSetKclass.declaredMemberFunctions.forEach { method ->


            if (method.name == "getInt") {
                //参数
                val flag = method.parameters.any {
//                    println("${it.type.withNullability(false)} ${String::class.createType()}")
                    it.type.withNullability(false) == String::class.createType()
                }
                if (flag) {
                    val result = method.call(resultSet, "id")
                    println(result)
                }

            }
        }
    }
    // 关闭数据库连接

    statement.close()
    // add application code here
    conn.close()

}



data class ItemData(var file: File, var dirName: String, var fileName: String, var url: String, var downloadLink: String, var fileSize: String, var time: String)


data class UserInfo(var id: Int, var name: String, var sex: String)

data class Student(var name: String, var age: Int)

data class SqlParam(val tableName: String, val paramList: List<Pair<String, String>>)

interface ColumnTypeMap {
    fun stringType(): String
    fun intType(): String
}

class H2DataColumnTypeMap : ColumnTypeMap {
    override fun stringType(): String {
        return "VARCHAR(200)"
    }

    override fun intType(): String {
        return "INTEGER"
    }

}

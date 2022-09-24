package site.starsone.kxorm

import org.h2.jdbc.JdbcResultSet
import site.starsone.kxorm.crud.OrmFunCreate
import java.sql.DriverManager
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.withNullability

/**
 * 通过JDBC创建H2DataBase库,创表,插入数据及查询数据
 * @author StarsOne
 * @date Create in  2022/09/24 14:04
 */
fun createDbAndQuery() {
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

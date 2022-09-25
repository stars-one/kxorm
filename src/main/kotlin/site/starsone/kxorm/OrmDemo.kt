package site.starsone.kxorm


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

package site.starsone.kxorm.bean

import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * 表源信息(注册的时候将data class解析完此对象保存)
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/09/24 21:50
 *
 */
class TableInfo {
    /**
     * 表名
     */
    var tableName: String = ""

    /**
     * 源类
     */
    var clazz: KClass<out Any>? = null

    /**
     * 数据库字段
     */
    val columns = arrayListOf<TableColumnInfo>()

    /**
     * 根据实体字段名查询列信息
     *
     * @param fieldName
     * @return
     */
    fun getColumnByFieldName(fieldName: String):TableColumnInfo? {
        return columns.firstOrNull() { it.fieldName==fieldName }
    }
}

class TableColumnInfo {
    /**
     * 是否主键
     */
    var isPk = false

    /**
     * 数据库字段名
     */
    var columnName = ""

    /**
     * 数据库字段类型
     */
    var columnType: String = ""


    /**
     * 实体里的字段名称
     */
    var fieldName: String = ""

    /**
     * 实体里的字段类型
     */
    var fieldType: KType? = null
}

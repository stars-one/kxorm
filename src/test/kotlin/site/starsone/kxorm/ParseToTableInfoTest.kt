package site.starsone.kxorm

import org.junit.Test
import site.starsone.kxorm.annotation.Table
import site.starsone.kxorm.annotation.TableColumn
import site.starsone.kxorm.annotation.TableColumnPk
import site.starsone.kxorm.bean.ItemData
import site.starsone.kxorm.bean.TableColumnInfo
import site.starsone.kxorm.bean.TableInfo
import java.io.File
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

/**
 *
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/09/24 23:57
 *
 */
class ParseToTableInfoTest {
    @Test
    fun test() {
        val data = ItemData("232", File("D:\\temp"), "mydirName11", 20)
        myData(data)
    }

    private fun myData(data: ItemData) {

        val jclass = data::class.java
        val kclass = data::class

        val tableInfo = TableInfo()
        tableInfo.clazz = data::class
        //数据表名,如果使用@Table注解,则使用Table注解中的数据,否则默认为当前类名
        var tableName = data::class.simpleName!!

        val tableAnnotation = jclass.declaredAnnotations.firstOrNull { it.annotationClass == Table::class }
        tableAnnotation?.let {
            val table = it as Table
            tableName = table.tableName
        }
        tableInfo.tableName = tableName

        //下面的操作主要是保存了对应的实体字段属性类型和属性名,并转为了数据库字段属性类型和属性名
        kclass.primaryConstructor?.parameters?.forEach { kParameter ->
            val columnInfo = TableColumnInfo()

            //根据实体数据类型转为数据库数据类型
            columnInfo.fieldType = kParameter.type
            columnInfo.fieldName = kParameter.name!!

            //todo 可以考虑用属性委托来整
            if (columnInfo.fieldType == String::class.starProjectedType || columnInfo.fieldType == File::class.starProjectedType) {
                columnInfo.columnType = "varchar(500)"
            }

            if (columnInfo.fieldType == Int::class.starProjectedType) {
                columnInfo.columnType = "INTEGER"
            }

            tableInfo.columns.add(columnInfo)
        }


        //保存3个字段信息: columnName columnType isPk(是否为主键)
        val fields = jclass.declaredFields
        fields.forEach { field ->
            //根据字段名找到之前的某列信息columnInfo
            val fieldName = field.name
            val columnInfo = tableInfo.getColumnByFieldName(fieldName)

            //todo 这里考虑加上字段名验证(不允许和数据库中的关键字同名)

            val declaredAnnotations = field.declaredAnnotations
            if (declaredAnnotations.isEmpty()) {
                columnInfo?.columnName = columnInfo?.fieldName!!
                //这里isPk字段默认是false,不需要设置
                // columnInfo.isPk = false
            }

            declaredAnnotations.forEach {
                //todo 考虑转属性名转为下划线等
                //如果存在有TableColumn注解,则使用注解上的属性作为数据库字段名,否则就设置与实体类的成员变量名一致
                if (it.annotationClass == TableColumn::class) {
                    val tableColumn = it as TableColumn
                    columnInfo?.columnName = tableColumn.columnName
                }

                //判断是否含TableColumnPk注解(即为主键)
                if (it.annotationClass == TableColumnPk::class) {
                    columnInfo?.isPk = true
                }
            }
        }

        println(tableInfo.tableName)
    }

}

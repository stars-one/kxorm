package site.starsone.kxorm.db

import site.starsone.kxorm.annotation.Table
import site.starsone.kxorm.annotation.TableColumn
import site.starsone.kxorm.annotation.TableColumnPk
import site.starsone.kxorm.bean.TableColumnInfo
import site.starsone.kxorm.bean.TableInfo
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

/**
 * 数据库连接配置类初始化
 *
 * @param url 数据库地址
 * @param user 数据库用户名
 * @param pwd 数据库密码
 */
class KxDbConnConfig(val url: String, val user: String, val pwd: String) {
    val registerClassList = LinkedHashMap<String, TableInfo>()

    /**
     * 注册实体类
     *
     * @param kClassArr
     */
    fun registerClass(vararg kClassArr: KClass<out Any>): KxDbConnConfig {
        kClassArr.forEach {
            registerClassList[it.qualifiedName!!] = parseDataToTableInfo(it)
        }
        return this
    }

    /**
     * 数据库连接
     *
     * @return
     */
    fun connect(): Connection {
        //todo 兼容不同数据库驱动
        val conn = DriverManager.getConnection(url, user, pwd)
        return conn
    }

    /**
     * 解析实体类转为数据库信息
     *
     * @param data
     * @return
     */
    private fun parseDataToTableInfo(data: KClass<out Any>): TableInfo {

        val jclass = data.java
        val kclass = data

        val tableInfo = TableInfo()
        tableInfo.clazz = kclass
        //数据表名,如果使用@Table注解,则使用Table注解中的数据,否则默认为当前类名
        var tableName = kclass.simpleName!!

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

            //先设置默认的数据库字段名与数据实体字段名一致,后续判断是否有对应的TableColumn注解从而进行更改
            columnInfo?.columnName = columnInfo?.fieldName!!

            val declaredAnnotations = field.declaredAnnotations

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

        return tableInfo
    }

    /**
     * 判断类是否已经注册
     *
     * @param className 全类名
     */
    fun isClassRegister(className: String): Boolean {
        return registerClassList.keys.contains(className)
    }

    /**
     * 判断类是否已经注册
     *
     * @param kClass
     */
    fun isClassRegister(kClass: KClass<out Any>): Boolean {
        val name = kClass.qualifiedName!!
        return isClassRegister(name)
    }

    /**
     * 根据全类名获取表信息
     *
     * @param className
     */
    fun getTableInfoByClassName(className: String): TableInfo? {
        return registerClassList[className]
    }

    /**
     * 根据全类名获取表信息
     *
     * @param className
     */
    fun getTableInfoByClass(kClass: KClass<out Any>): TableInfo? {
        return getTableInfoByClassName(kClass.qualifiedName!!)
    }

}

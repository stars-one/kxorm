package site.starsone.kxorm.db

import site.starsone.kxorm.crud.OrmFunCreate
import site.starsone.kxorm.crud.OrmFunInsert
import site.starsone.kxorm.crud.OrmFunQuery
import java.sql.Connection
import kotlin.reflect.KClass

/**
 *
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/07/17 14:23
 *
 */
class KxDb {
    companion object {
        lateinit var connection: Connection

        lateinit var kxDbConnConfig: KxDbConnConfig

        /**
         * 初始化操作(含创表)
         *
         * @param kxDbConnConfig
         */
        fun init(kxDbConnConfig: KxDbConnConfig) {
            this.kxDbConnConfig = kxDbConnConfig
            //数据库连接
            connection = kxDbConnConfig.connect()
            //自动创表
            val registerClassList = kxDbConnConfig.registerClassList
            registerClassList.forEach {
                //判断表是否存在
                if (!isTableExist(it.simpleName!!)) {
                    //创表
                    OrmFunCreate.createTableByClass(connection, it)
                }
            }
        }

        //判断数据库中表是否存在    https://www.thinbug.com/q/19518265
        fun isTableExist(tableName: String): Boolean {
            return OrmFunCreate.isTableExist(connection, tableName)
        }

        /**
         * 插入数据
         *
         * @param T
         * @param data
         * @return
         */
        fun <T : Any> insert(data: T): Int {
            return OrmFunInsert.insert(connection, data)
        }

        /**
         * 更新数据
         *
         * @param data 数据
         * @return
         */
        fun <T : Any> update(data: T): Int {
            //todo Orm更新数据实现
            TODO()
        }

        /**
         * 获取查询后的实体列表数据
         *
         * @param T
         * @param resultSet
         * @param kclass
         * @return
         */
        fun <T : Any> getQueryList(kclass: KClass<T>): List<T> {
            if (kxDbConnConfig.registerClassList.contains(kclass)) {
                return OrmFunQuery.queryListByClass(connection, kclass)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

    }
}

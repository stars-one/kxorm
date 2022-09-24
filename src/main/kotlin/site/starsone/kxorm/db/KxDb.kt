package site.starsone.kxorm.db

import site.starsone.kxorm.condition.ConditionWhere
import site.starsone.kxorm.crud.*
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
            TODO("Orm更新数据实现")
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
            //判断是否类已被注册
            if (kxDbConnConfig.registerClassList.contains(kclass)) {
                return OrmFunQuery.queryListByClass(connection, kclass)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 根据条件,获取数据列表
         *
         * @param T
         * @param kclass 实体类class
         * @param lambda 传递单条件lambda,如Student.class::name eq "john"
         * @receiver
         * @return
         */
        fun <T : Any> getQueryListByCondition(kclass: KClass<T>, lambda: () -> ConditionWhere<out Any>): List<T> {
            val condition = lambda.invoke()
            if (kxDbConnConfig.registerClassList.contains(kclass)) {
                return OrmFunQuery.queryListByCondition(connection, kclass, condition.toSql())
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 根据条件,获取数据列表
         *
         * @param T
         * @param kclass 实体类class
         * @param lambda 条件where语句(不用含where关键字)
         * @receiver
         * @return
         */
        fun <T : Any> getQueryListByCondition(kclass: KClass<T>, condition: String): List<T> {
            if (kxDbConnConfig.registerClassList.contains(kclass)) {
                return OrmFunQuery.queryListByCondition(connection, kclass, condition)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 获取查询后的实体列表数据
         *
         * @param T
         * @param resultSet
         * @param kclass
         * @return
         */
        /*fun <T : Any> getQueryListByCondition(kclass: KClass<T>,lambda:()->List<ConditionWhere<out Any>>): List<T> {
            TODO("多条件如何实现??")
            if (kxDbConnConfig.registerClassList.contains(kclass)) {
                return OrmFunQuery.queryListByClass(connection, kclass)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }*/

        /**
         * 删除实体类
         * @param T
         * @param kclass
         * @param lambda
         * @receiver
         * @return
         */
        fun <T : Any> delete(kclass: KClass<T>, lambda: () -> ConditionWhere<out Any>): Int {
            val condition = lambda.invoke()
            if (kxDbConnConfig.registerClassList.contains(kclass)) {
                return OrmFunDelete.delete(connection, kclass, condition.toSql())
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 删除实体类
         * @param T
         * @param kclass
         * @param lambda
         * @receiver
         * @return
         */
        fun <T : Any> delete(kclass: KClass<T>, condition: String): Int {
            if (kxDbConnConfig.registerClassList.contains(kclass)) {
                return OrmFunDelete.delete(connection, kclass, condition)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 删除表的所有数据
         *
         * @param T
         * @param kclass
         * @return
         */
        fun <T : Any> deleteAll(kclass: KClass<T>): Int {
            return OrmFunDelete.deleteAll(connection, kclass)
        }

        /**
         * 删除某个实体类
         * @param T
         * @param bean
         * @return
         */
        fun <T : Any> delete(bean: T): Int {
            TODO("删除某个表")
        }

        /**
         * 更新实体类
         *
         * @param T
         * @param bean
         * @return
         */
        fun <T : Any> updateForce(bean: T, lambda: () -> ConditionWhere<out Any>): Int {
            val condition = lambda.invoke()
            val kclass = bean::class

            if (kxDbConnConfig.registerClassList.contains(kclass)) {
                return OrmFunUpdate.updateForce(connection, bean, condition)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }

        }


    }
}

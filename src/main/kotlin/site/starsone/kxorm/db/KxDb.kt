package site.starsone.kxorm.db

import com.github.yitter.contract.IdGeneratorOptions
import com.github.yitter.idgen.YitIdHelper
import site.starsone.kxorm.condition.ConditionOrderBy
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
            // 雪花算法,全局初始化一次
            // 创建 IdGeneratorOptions 对象，可在构造函数中输入 WorkerId：
            val options = IdGeneratorOptions(6)
            YitIdHelper.setIdGenerator(options)

            this.kxDbConnConfig = kxDbConnConfig
            //数据库连接
            connection = kxDbConnConfig.connect()
            //自动创表
            val registerClassList = kxDbConnConfig.registerClassList
            registerClassList.forEach {
                //判断表是否存在
                if (!isTableExist(it.value.tableName)) {
                    //创表
                    OrmFunCreate.createTableByClassName(connection, it.key)
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
        inline fun <reified T : Any> insert(data: T): Int {
            val kclass = T::class
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunInsert.insert(connection, data)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 插入数据
         *
         * @param T
         * @param data
         * @return
         */
        inline fun <reified T : Any> insert(data: List<T>): Int {
            val kclass = T::class
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunInsert.insert(connection, data)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
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
         * 更新实体类
         *
         * @param T
         * @param bean
         * @return
         */
        inline fun <reified T : Any> updateForce(bean: T): Int {
            val kclass = T::class
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunUpdate.updateForce(connection, bean)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 更新实体类
         *
         * @param T
         * @param beanList
         * @return
         */
        inline fun <reified T : Any> updateForce(beanList: List<T>): Int {
            val kclass = T::class
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunUpdate.updateForce(connection, beanList)
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
        fun <T : Any> getQueryList(kclass: KClass<T>): List<T> {
            //判断是否类已被注册
            if (kxDbConnConfig.isClassRegister(kclass)) {
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
         * @param whereLambda 传递单条件lambda,如Student.class::name eq "john"
         * @receiver
         * @return
         */
        fun <T : Any> getQueryListByCondition(kclass: KClass<T>, whereLambda: () -> ConditionWhere<out Any>): List<T> {
            val condition = whereLambda.invoke()
            if (kxDbConnConfig.isClassRegister(kclass)) {
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
         * @param whereLambda 传递单条件lambda,如{Student.class::name eq "john"}
         * @param orderLambda 传递单排序lambda,如{ItemData::myCount.orderByAsc()}
         * @receiver
         * @return
         */
        fun <T : Any> getQueryListByCondition(kclass: KClass<T>, whereLambda: () -> ConditionWhere<out Any>,orderLambda: () -> ConditionOrderBy<out Any>): List<T> {
            val condition = whereLambda.invoke()
            val order = orderLambda.invoke()
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunQuery.queryListByCondition(connection, kclass, condition.toSql(),order.toOrderBySql())
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }


        /**
         * 分页查询列表数据
         *
         * @param T
         * @param kclass
         * @param pageNo 页码(从1开始)
         * @param pageSize 每页数量
         * @param whereLambda
         * @param orderLambda
         * @receiver
         * @receiver
         * @return
         */
        fun <T : Any> getQueryListByPage(kclass: KClass<T>,pageNo: Int,pageSize: Int, whereLambda: () -> ConditionWhere<out Any>,orderLambda: () -> ConditionOrderBy<out Any>): List<T> {
            val condition = whereLambda.invoke()
            val order = orderLambda.invoke()
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunQuery.queryListByPage(connection, kclass, condition.toSql(),order.toOrderBySql(),pageNo,pageSize)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 根据条件,获取数据列表
         *
         * @param T
         * @param kclass 实体类class
         * @param condition 条件where语句(不用含where关键字,不需要啊的话传空白字符串即可)
         * @param orderBy 排序(不用含order by关键字,不需要的话传空白字符串即可)
         * @receiver
         * @return
         */
        fun <T : Any> getQueryListByCondition(kclass: KClass<T>, condition: String, orderBy:String): List<T> {
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunQuery.queryListByCondition(connection, kclass, condition,orderBy)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 根据条件,获取数据列表(分页)
         *
         * @param T
         * @param kclass 实体类class
         * @param condition 条件where语句(不用含where关键字)
         * @param pageNo 页码(从1开始)
         * @param pageSize 每页数量
         * @receiver
         * @return
         */
        fun <T : Any> getQueryListByPage(
            kclass: KClass<T>, condition: String,
            orderBy:String,
            pageNo: Int,
            pageSize: Int
        ): List<T> {
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunQuery.queryListByPage(connection, kclass, condition, orderBy,pageNo, pageSize)
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
        fun <T : Any> delete(kclass: KClass<T>, condition: String): Int {
            if (kxDbConnConfig.isClassRegister(kclass)) {
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
            val kclass = bean::class
            if (kxDbConnConfig.isClassRegister(bean::class)) {
                return OrmFunDelete.delete(connection, bean)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

        /**
         * 删除某个实体类
         * @param T
         * @param list
         * @return
         */
        inline fun <reified T : Any> delete(list: List<T>): Int {
            val kclass = T::class
            if (kxDbConnConfig.isClassRegister(kclass)) {
                return OrmFunDelete.delete(connection, list)
            } else {
                throw Exception("${kclass.simpleName}类还未进行注册操作!!")
            }
        }

    }
}

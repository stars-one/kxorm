package site.starsone.kxorm.db

import site.starsone.kxorm.crud.OrmFunCreate
import java.sql.Connection

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

        fun init(kxDbConnConfig: KxDbConnConfig) {
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
            return OrmFunCreate.isTableExist(connection,tableName)
        }
    }
}

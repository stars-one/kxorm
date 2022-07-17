package site.starsone.kxorm.db

import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.KClass

/**
 * 数据库连接配置类初始化
 *
 * @param url 数据库地址
 * @param user 数据库用户名
 * @param pwd 数据库密码
 */
class KxDbConnConfig(val url: String, val user: String, val pwd: String) {
    val registerClassList = arrayListOf<KClass<Any>>()

    /**
     * 注册实体类
     *
     * @param kClassArr
     */
    fun registerClass(vararg kClassArr: KClass<Any>): KxDbConnConfig {
        registerClassList.addAll(kClassArr.toList())
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
}

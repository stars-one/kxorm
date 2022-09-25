package site.starsone.kxorm

import org.junit.Test
import site.starsone.kxorm.bean.ItemData
import site.starsone.kxorm.condition.eq
import site.starsone.kxorm.db.KxDb
import site.starsone.kxorm.db.KxDbConnConfig
import java.io.File
import java.util.*


class KxOrmTest {

    /**
     * 数据库连接及账号密码
     */
    val dbUrl = "jdbc:h2:D:/temp/h2db/test"
    val user = ""
    val pwd = ""

    /**
     * 表的实体类
     */
    val kclass = ItemData::class

    /**
     * 初始化数据库连接配置
     *
     */
    private fun initDb() {
        val kxDbConnConfig = KxDbConnConfig(dbUrl, user, pwd).registerClass(kclass)
        KxDb.init(kxDbConnConfig)
    }

    init {
        //在类的初始化就调用此方法,避免下面的测试方法每次都要加的问题
        initDb()
    }

    /**
     * 测试插入数据
     *
     */
    @Test
    fun testCreateAndInsert() {
        initDb()
        val data = ItemData(
            "122",
            File("D:\\temp\\myd.png"),
            "D:\\temp",
            12
        )
        val result = KxDb.insert(data)
        assert(result==1)
    }

    /**
     * 查询所有数据
     */
    @Test
    fun queryAllList() {
        initDb()
        //查询
        val queryList = KxDb.getQueryList(kclass)
        println(queryList.toString())
        assert(queryList.isNotEmpty())
    }

    /**
     * 根据条件查询列表
     *
     */
    @Test
    fun queryListByCondition() {
        val list = KxDb.getQueryListByCondition(ItemData::class,"MYCOUNT > 10")
//        val list = KxDb.getQueryListByCondition(ItemData::class){
//            ItemData::myCount gt 10
//        }
        println("myCount大于10: $list")
        assert(list.isNotEmpty())
    }

    /**
     * 根据条件查询列表
     *
     */
    @Test
    fun delete() {
        val data = ItemData("232",File("D:\\temp"),"mydirName11",20)
        KxDb.insert(data)
        val row = KxDb.delete(ItemData::class){
            ItemData::dataId eq "232"
        }
        assert(row==1)
    }

    @Test
    fun update() {

        val dataId = UUID.randomUUID().toString()
        val data = ItemData(dataId,File("D:\\temp"),"mydirName11",20)
        KxDb.insert(data)

        data.myCount = 45
        val row = KxDb.updateForce(data){
            ItemData::dataId eq dataId
        }
        assert(row==1)
    }


}



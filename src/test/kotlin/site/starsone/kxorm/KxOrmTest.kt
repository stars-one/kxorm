package site.starsone.kxorm

import org.junit.Test
import site.starsone.kxorm.bean.ItemData
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
        val data = ItemData(
            "122",
            File("D:\\temp\\myd.png"),
            "D:\\temp",
            12
        )
        val result = KxDb.insert(data)
        assert(result == 1)
    }

    /**
     * 测试插入数据
     *
     */
    @Test
    fun testCreateAndBatchInsert() {
        val list = arrayListOf<ItemData>()
        repeat(5) {
            val data = ItemData(
                "122-$it",
                File("D:\\temp\\myd.png"),
                "D:\\temp",
                12
            )
            list.add(data)
        }

        val result = KxDb.insert(list)
        println("批量插入数据:$result")
        assert(result > 0)
    }

    /***
     * 测试批量更新方法
     */
    @Test
    fun testCreateAndBatchUpdate() {
        val list = arrayListOf<ItemData>()
        repeat(5) {
            val data = ItemData(
                "8122-$it",
                File("D:\\temp\\myd.png"),
                "D:\\temp",
                12
            )
            list.add(data)
        }
        //插入操作
        val result = KxDb.insert(list)
        println("批量插入数据:$result")

        //更新操作
        list.forEachIndexed { index, itemData ->
            itemData.myCount = index + 50
        }

        val result2 = KxDb.updateForce(list)

        assert(result == result2)
    }

    /**
     * 查询所有数据
     */
    @Test
    fun queryAllList() {
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
        val list = KxDb.getQueryListByCondition(ItemData::class, "MYCOUNT > 10")
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
        val data = ItemData("28832", File("D:\\temp"), "mydirName11", 20)
        KxDb.insert(data)
        val row = KxDb.delete(data)
        assert(row == 1)
    }

    /**
     * 根据条件查询列表
     *
     */
    @Test
    fun deleteList() {
        initDb()
        val list = arrayListOf<ItemData>()
        repeat(5) {
            val data = ItemData("28832-$it", File("D:\\temp"), "mydirName11", 20)
            list.add(data)
        }
        KxDb.insert(list)
        val row = KxDb.delete(list)
        println("删除行数:$row")
        assert(row == list.size)
    }

    @Test
    fun update() {

        val dataId = UUID.randomUUID().toString()
        val data = ItemData(dataId, File("D:\\temp"), "mydirName11", 20)
        KxDb.insert(data)

        data.myCount = 45
        val row = KxDb.updateForce(data)
        assert(row == 1)
    }


}



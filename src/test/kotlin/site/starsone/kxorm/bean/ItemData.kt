package site.starsone.kxorm.bean

import java.io.File

/**
 * 测试数据库实体类(三种类型)
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/09/24 00:16
 */

data class ItemData(
    var file: File,
    var dirName: String,
    var myCount:Int
)

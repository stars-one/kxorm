package site.starsone.kxorm.annotation

/**
 *  字段名
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/09/24 21:40
 *
 */
@Target(AnnotationTarget.FIELD)
annotation class TableColumn(val columnName: String)

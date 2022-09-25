package site.starsone.kxorm.annotation

/**
 * 表名注解
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/09/24 21:37
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class Table(
    val tableName: String
)

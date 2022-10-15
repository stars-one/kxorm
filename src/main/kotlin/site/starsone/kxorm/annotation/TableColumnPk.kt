package site.starsone.kxorm.annotation

/**
 * 主键注解
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2022/09/24 21:39
 *
 */
@Target(AnnotationTarget.FIELD)
annotation class TableColumnPk(val type: PkType=PkType.NONE)

/**
 * Pk type
 * - NONE 用户自行设置
 * - ASSIGN_UUID 插入前生成UUID
 * - ASSIGN_ID 插入前生成雪花ID
 */
enum class PkType {
    NONE, ASSIGN_UUID, ASSIGN_ID
}

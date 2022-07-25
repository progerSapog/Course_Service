package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи skill_keyword_link
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param skillId   id навыка
 * @param keywordId id ключевого слова
 */
case class SkillKeyWordLink(skillId: UUID, keywordId: UUID) extends ILinkTable

object SkillKeyWordLink extends SQLSyntaxSupport[SkillKeyWordLink] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "skill_keyword_link"

  val skwl: QuerySQLSyntaxProvider[SQLSyntaxSupport[SkillKeyWordLink], SkillKeyWordLink] = SkillKeyWordLink.syntax("s_kw_l")
  val skwlC: ColumnName[SkillKeyWordLink] = SkillKeyWordLink.column
}
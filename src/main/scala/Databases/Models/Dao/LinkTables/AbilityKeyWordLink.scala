package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи ability_keyword_link
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param abilityId id умения
 * @param keywordId id ключевого слова
 */
case class AbilityKeyWordLink(abilityId: UUID, keywordId: UUID) extends ILinkTable

object AbilityKeyWordLink extends SQLSyntaxSupport[AbilityKeyWordLink] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "ability_keyword_link"

  val akwl: QuerySQLSyntaxProvider[SQLSyntaxSupport[AbilityKeyWordLink], AbilityKeyWordLink] = AbilityKeyWordLink.syntax("a_kw_l")
  val akwlC: ColumnName[AbilityKeyWordLink] = AbilityKeyWordLink.column
}
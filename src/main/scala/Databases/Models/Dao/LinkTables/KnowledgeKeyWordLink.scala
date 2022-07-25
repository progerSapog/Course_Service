package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи knowledge_keyword_link
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param knowledgeId id знания
 * @param keywordId   id ключевого слова
 */
case class KnowledgeKeyWordLink(knowledgeId: UUID, keywordId: UUID) extends ILinkTable

object KnowledgeKeyWordLink extends SQLSyntaxSupport[KnowledgeKeyWordLink] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "knowledge_keyword_link"

  val kkwl: QuerySQLSyntaxProvider[SQLSyntaxSupport[KnowledgeKeyWordLink], KnowledgeKeyWordLink] = KnowledgeKeyWordLink.syntax("k_kw_l")
  val kkwlC: ColumnName[KnowledgeKeyWordLink] = KnowledgeKeyWordLink.column
}
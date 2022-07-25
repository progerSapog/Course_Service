package Databases.Models.Dao.Plugs

import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы Знаний (knowledge).
 *
 * Объект компаньон позволяющий работать с таблицей knowledge
 * при помощи typesafe dsl
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 */
case class KnowledgePlug(id: UUID, name: String) extends IPlug

object KnowledgePlug extends SQLSyntaxSupport[KnowledgePlug] {
  val k: QuerySQLSyntaxProvider[SQLSyntaxSupport[KnowledgePlug], KnowledgePlug] = KnowledgePlug.syntax("k")
  val kC: ColumnName[KnowledgePlug] = KnowledgePlug.column
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "knowledge"

  def apply(r: ResultName[KnowledgePlug])(rs: WrappedResultSet): KnowledgePlug =
    new KnowledgePlug(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )
}
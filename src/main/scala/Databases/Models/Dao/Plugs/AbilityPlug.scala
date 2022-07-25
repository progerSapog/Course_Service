package Databases.Models.Dao.Plugs

import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы Умений (ability).
 *
 * Объект компаньон позволяющий работать с таблицей ability
 * при помощи typesafe dsl
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 */
case class AbilityPlug(id: UUID, name: String) extends IPlug

object AbilityPlug extends SQLSyntaxSupport[AbilityPlug] {
  val a: QuerySQLSyntaxProvider[SQLSyntaxSupport[AbilityPlug], AbilityPlug] = AbilityPlug.syntax("a")
  val aC: ColumnName[AbilityPlug] = AbilityPlug.column
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "ability"

  def apply(r: ResultName[AbilityPlug])(rs: WrappedResultSet): AbilityPlug =
    new AbilityPlug(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )
}
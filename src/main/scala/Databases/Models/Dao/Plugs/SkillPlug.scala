package Databases.Models.Dao.Plugs

import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы Навыков (skill).
 *
 * Объект компаньон позволяющий работать с таблицей skill
 * при помощи typesafe dsl
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 */
case class SkillPlug(id: UUID, name: String) extends IPlug

object SkillPlug extends SQLSyntaxSupport[SkillPlug] {
  val s: QuerySQLSyntaxProvider[SQLSyntaxSupport[SkillPlug], SkillPlug] = SkillPlug.syntax("s")
  val sC: ColumnName[SkillPlug] = SkillPlug.column
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "skill"

  def apply(r: ResultName[SkillPlug])(rs: WrappedResultSet): SkillPlug =
    new SkillPlug(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )
}

package Databases.Models.Dao.Plugs

import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы Курсов (course).
 *
 * Объект компаньон позволяющий работать с таблицей course
 * при помощи typesafe dsl
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 */
case class CoursePlug(id: UUID, name: String) extends IPlug

object CoursePlug extends SQLSyntaxSupport[CoursePlug] {
  val c: QuerySQLSyntaxProvider[SQLSyntaxSupport[CoursePlug], CoursePlug] = CoursePlug.syntax("c")
  val cC: ColumnName[CoursePlug] = CoursePlug.column
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "course"

  def apply(r: ResultName[CoursePlug])(rs: WrappedResultSet): CoursePlug =
    new CoursePlug(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )
}
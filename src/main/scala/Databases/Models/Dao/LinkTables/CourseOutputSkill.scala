package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи course_output_skill
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param courseId id курса
 * @param skillId  id выходного курса
 */
case class CourseOutputSkill(courseId: UUID, skillId: UUID) extends ILinkTable

object CourseOutputSkill extends SQLSyntaxSupport[CourseOutputSkill] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "course_output_skill"

  val cos: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseOutputSkill], CourseOutputSkill] = CourseOutputSkill.syntax("c_o_s")
  val cosC: ColumnName[CourseOutputSkill] = CourseOutputSkill.column
}
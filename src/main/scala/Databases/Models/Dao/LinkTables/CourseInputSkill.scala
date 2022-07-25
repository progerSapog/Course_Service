package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи course_input_skill
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param courseId id курса
 * @param skillId  id входного курса
 */
case class CourseInputSkill(courseId: UUID, skillId: UUID) extends ILinkTable

object CourseInputSkill extends SQLSyntaxSupport[CourseInputSkill] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "course_input_skill"

  val cis: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseInputSkill], CourseInputSkill] = CourseInputSkill.syntax("c_i_s")
  val cisC: ColumnName[CourseInputSkill] = CourseInputSkill.column
}
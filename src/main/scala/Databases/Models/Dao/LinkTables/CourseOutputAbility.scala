package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи course_output_ability
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param courseId  id курса
 * @param abilityId id выходного умения
 */
case class CourseOutputAbility(courseId: UUID, abilityId: UUID) extends ILinkTable

object CourseOutputAbility extends SQLSyntaxSupport[CourseOutputAbility] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "course_output_ability"

  val coa: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseOutputAbility], CourseOutputAbility] = CourseOutputAbility.syntax("c_o_a")
  val coaC: ColumnName[CourseOutputAbility] = CourseOutputAbility.column
}
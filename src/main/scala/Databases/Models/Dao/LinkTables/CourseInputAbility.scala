package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи course_input_ability
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param courseId  id курса
 * @param abilityId id входного умения
 */
case class CourseInputAbility(courseId: UUID, abilityId: UUID) extends ILinkTable

object CourseInputAbility extends SQLSyntaxSupport[CourseInputAbility] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "course_input_ability"

  val cia: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseInputAbility], CourseInputAbility] = CourseInputAbility.syntax("c_i_a")
  val ciaC: ColumnName[CourseInputAbility] = CourseInputAbility.column
}
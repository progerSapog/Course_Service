package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи course_input_knowledge
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param courseId    id курса
 * @param knowledgeId id входного знания
 */
case class CourseInputKnowledge(courseId: UUID, knowledgeId: UUID) extends ILinkTable

object CourseInputKnowledge extends SQLSyntaxSupport[CourseInputKnowledge] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "course_input_knowledge"

  val cik: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseInputKnowledge], CourseInputKnowledge] = CourseInputKnowledge.syntax("c_i_k")
  val cikC: ColumnName[CourseInputKnowledge] = CourseInputKnowledge.column
}
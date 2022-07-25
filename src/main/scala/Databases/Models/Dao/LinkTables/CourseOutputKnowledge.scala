package Databases.Models.Dao.LinkTables

import scalikejdbc._

import java.util.UUID

/**
 * Представление таблицы связи course_output_knowledge
 *
 * Объект компаньон, позволяющий работать с данным отображением при помощи
 * type safe DSL
 *
 * @param courseId    id курса
 * @param knowledgeId id выходного знания
 */
case class CourseOutputKnowledge(courseId: UUID, knowledgeId: UUID) extends ILinkTable

object CourseOutputKnowledge extends SQLSyntaxSupport[CourseOutputKnowledge] {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "course_output_knowledge"

  val cok: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseOutputKnowledge], CourseOutputKnowledge] = CourseOutputKnowledge.syntax("c_o_k")
  val cokC: ColumnName[CourseOutputKnowledge] = CourseOutputKnowledge.column
}
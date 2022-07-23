package Databases.Models.Domain

import java.util.UUID

/**
 * Класс Course
 * В отлчии от CourseEntity может содержать логику
 *
 * @see IModel
 * */
case class Course(id: UUID,
                  name: String,
                  inputSkills: Seq[Skill],
                  outputSkills: Seq[Skill],
                  inputAbility: Seq[Ability],
                  outputAbility: Seq[Ability],
                  inputKnowledge: Seq[Knowledge],
                  outputKnowledge: Seq[Knowledge]
                 ) extends IModel {

  /**
   * Проверка, что переданный ЗУН является входным для курса
   *
   * @param kas ЗУН для проверки
   * @return true если ЗУН является входным
   */
  def isInput(kas: IKAS): Boolean = kas match {
    case k: Knowledge => inputKnowledge.contains(k)
    case s: Skill => inputSkills.contains(s)
    case a: Ability => inputAbility.contains(a)
    case _ => throw new IllegalArgumentException
  }

  /**
   * Проверка, что переданный ЗУН является выходным для курса
   *
   * @param kas ЗУН для проверки
   * @return true если ЗУН является выходным
   */
  def isOutput(kas: IKAS): Boolean = kas match {
    case k: Knowledge => outputKnowledge.contains(k)
    case s: Skill => outputSkills.contains(s)
    case a: Ability => outputAbility.contains(a)
    case _ => throw new IllegalArgumentException
  }
}
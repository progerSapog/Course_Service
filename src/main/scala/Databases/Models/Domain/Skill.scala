package Databases.Models.Domain

import java.util.UUID

/**
 * Класс Skill
 * В отлчии от SkillEntity может содержать логику
 *
 * @see IKSA
 * */
case class Skill(id: UUID,
                 name: String,
                 keyWords: Seq[SkillKeyWord]) extends IKAS

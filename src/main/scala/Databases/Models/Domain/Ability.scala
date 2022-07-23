package Databases.Models.Domain

import java.util.UUID

/**
 * Класс Ability
 * В отлчии от AbilityEntity может содержать логику
 *
 * @see IKSA
 * */
case class Ability(id: UUID,
                   name: String,
                   keyWords: Seq[AbilityKeyWord]) extends IKAS

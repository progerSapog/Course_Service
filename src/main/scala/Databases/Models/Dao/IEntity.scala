package Databases.Models.Dao

import java.util.UUID

/**
 * Трейт для Entity классов
 * */
trait IEntity {
  val id: UUID
  val name: String
}

/**
 * Трейт для ЗУН'ов.
 * Содержит множество ключевых слов
 */
trait IKASEntity extends IEntity {
  val keyWords: Seq[IKeyWordEntity]
}

/**
 * Маркерный трейт для ключевых слов
 */
trait IKeyWordEntity extends IEntity

/**
 * Маркерный трейт для классов "пробок"
 */
trait IPlug extends IEntity
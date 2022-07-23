package Databases.Models.Domain

import java.util.UUID

/**
 * Трейт для моделей
 * */
trait IModel {
  val id: UUID
  val name: String
}

/**
 * Маркерный трейт для ключевых слов
 */
trait IKeyWord extends IModel

/**
 * Трейт ЗУН'ов. Содержит ключевые слова
 * */
trait IKAS extends IModel {
  val keyWords: Seq[IKeyWord]
}
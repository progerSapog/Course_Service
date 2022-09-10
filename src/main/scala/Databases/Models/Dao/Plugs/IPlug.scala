package Databases.Models.Dao.Plugs

import java.util.UUID

/**
 * Маркерный трейт для отображения таблиц сущностей (т.е. исключая таблицы связи).
 */
trait IPlug {
  val id: UUID
  val name: String
}


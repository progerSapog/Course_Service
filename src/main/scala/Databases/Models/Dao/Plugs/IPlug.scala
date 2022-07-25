package Databases.Models.Dao.Plugs

import Databases.Models.Dao.IEntity

/**
 * Маркерный трейт для отображения таблиц сущностей (т.е. исключая таблицы связи).
 */
trait IPlug extends IEntity

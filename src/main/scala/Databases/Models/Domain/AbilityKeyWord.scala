package Databases.Models.Domain

import java.util.UUID

/**
 * Класс сущности - ключевые слова умения
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 */
case class AbilityKeyWord(id: UUID,
                          name: String) extends IKeyWord

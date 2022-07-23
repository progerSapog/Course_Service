package Databases.Models.Domain

import java.util.UUID

/**
 * Класс сущности - ключевые слова знания
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 */
case class KnowledgeKeyWord(id: UUID,
                            name: String) extends IKeyWord

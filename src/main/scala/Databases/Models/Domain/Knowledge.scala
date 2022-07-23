package Databases.Models.Domain

import java.util.UUID

/**
 * Класс Knowledge
 * В отлчии от KnowledgeEntity может содержать логику
 *
 * @see IKSA
 * */
case class Knowledge(id: UUID,
                     name: String,
                     keyWords: Seq[KnowledgeKeyWord]) extends IKAS

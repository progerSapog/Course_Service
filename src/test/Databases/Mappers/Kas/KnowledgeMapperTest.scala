package Databases.Mappers.Kas

import Databases.Mappers.Keywords.KnowledgeKeyWordMapper
import Databases.Models.Dao.Kas.KnowledgeEntity
import Databases.Models.Dao.Keywords.KnowledgeKeyWordEntity
import Databases.Models.Domain.Knowledge
import org.specs2.mutable.Specification

import java.util.UUID

object KnowledgeMapperTest extends Specification {
  val keyword: Seq[KnowledgeKeyWordEntity] = Seq(
    KnowledgeKeyWordEntity(UUID.randomUUID(), "keyword1"),
    KnowledgeKeyWordEntity(UUID.randomUUID(), "keyword2"),
  )
  val entity: KnowledgeEntity = KnowledgeEntity(
    id = UUID.randomUUID(),
    name = "knowledge1",
    keyWords = keyword
  )

  "from entity to model" in {
    KnowledgeMapper.entity2Model(entity) mustEqual Knowledge(
      id = entity.id,
      name = entity.name,
      keyWords = keyword.map(KnowledgeKeyWordMapper.entity2Model)
    )
  }

  "from model to entity" in {
    KnowledgeMapper.model2Entity(
      Knowledge(
        id = entity.id,
        name = entity.name,
        keyWords = keyword.map(KnowledgeKeyWordMapper.entity2Model))
    ) mustEqual entity
  }
}

package Databases.Mappers.Keywords

import Databases.Models.Dao.Keywords.KnowledgeKeyWordEntity
import Databases.Models.Domain.KnowledgeKeyWord
import org.specs2.mutable.Specification

import java.util.UUID

object KnowledgeKeyWordMapperTest extends Specification {
  val entity: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
    id = UUID.randomUUID(),
    name = "KnowledgeKeyword1"
  )

  "from entity to model" in {
    KnowledgeKeyWordMapper.entity2Model(entity) mustEqual KnowledgeKeyWord(
      id = entity.id,
      name = entity.name
    )
  }

  "from model to entity" in {
    KnowledgeKeyWordMapper.model2Entity(KnowledgeKeyWord(
      id = entity.id,
      name = entity.name)) mustEqual entity
  }
}

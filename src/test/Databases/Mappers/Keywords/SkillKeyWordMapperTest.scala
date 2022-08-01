package Databases.Mappers.Keywords

import Databases.Models.Dao.Keywords.SkillKeyWordEntity
import Databases.Models.Domain.SkillKeyWord
import org.specs2.mutable.Specification

import java.util.UUID

object SkillKeyWordMapperTest extends Specification {
  val entity: SkillKeyWordEntity = SkillKeyWordEntity(
    id = UUID.randomUUID(),
    name = "SkillKeyword1"
  )

  "from entity to model" in {
    SkillKeyWordMapper.entity2Model(entity) mustEqual SkillKeyWord(
      id = entity.id,
      name = entity.name
    )
  }

  "from model to entity" in {
    SkillKeyWordMapper.model2Entity(SkillKeyWord(
      id = entity.id,
      name = entity.name)) mustEqual entity
  }

}

package Databases.Mappers.Keywords

import Databases.Models.Dao.Keywords.AbilityKeyWordEntity
import Databases.Models.Domain.AbilityKeyWord
import org.specs2.mutable.Specification

import java.util.UUID

object AbilityKeyWordMapperTest extends Specification {
  val entity: AbilityKeyWordEntity = AbilityKeyWordEntity(
    id = UUID.randomUUID(),
    name = "AbilityKeyword1"
  )

  "from entity to model" in {
    AbilityKeyWordMapper.entity2Model(entity) mustEqual AbilityKeyWord(
      id = entity.id,
      name = entity.name
    )
  }

  "from model to entity" in {
    AbilityKeyWordMapper.model2Entity(AbilityKeyWord(
        id = entity.id,
        name = entity.name)) mustEqual entity
  }
}

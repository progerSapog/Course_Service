package Databases.Mappers.Kas

import Databases.Mappers.Keywords.AbilityKeyWordMapper
import Databases.Models.Dao.Kas.AbilityEntity
import Databases.Models.Dao.Keywords.AbilityKeyWordEntity
import Databases.Models.Domain.Ability
import org.specs2.mutable.Specification

import java.util.UUID

object AbilityMapperTest extends Specification {
  val keyword: Seq[AbilityKeyWordEntity] = Seq(
    AbilityKeyWordEntity(UUID.randomUUID(), "keyword1"),
    AbilityKeyWordEntity(UUID.randomUUID(), "keyword2"),
  )
  val entity: AbilityEntity = AbilityEntity(
    id = UUID.randomUUID(),
    name = "ability1",
    keyWords = keyword
  )

  "from entity to model" in {
    AbilityMapper.entity2Model(entity) mustEqual Ability(
      id = entity.id,
      name = entity.name,
      keyWords = keyword.map(AbilityKeyWordMapper.entity2Model)
    )
  }

  "from model to entity" in {
    AbilityMapper.model2Entity(
      Ability(
        id = entity.id,
        name = entity.name,
        keyWords = keyword.map(AbilityKeyWordMapper.entity2Model))
    ) mustEqual entity
  }
}
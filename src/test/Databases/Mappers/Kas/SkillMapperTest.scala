package Databases.Mappers.Kas

import Databases.Mappers.Keywords.SkillKeyWordMapper
import Databases.Models.Dao.Kas.SkillEntity
import Databases.Models.Dao.Keywords.SkillKeyWordEntity
import Databases.Models.Domain.Skill
import org.specs2.mutable.Specification

import java.util.UUID

object SkillMapperTest extends Specification {
  val keyword: Seq[SkillKeyWordEntity] = Seq(
    SkillKeyWordEntity(UUID.randomUUID(), "keyword1"),
    SkillKeyWordEntity(UUID.randomUUID(), "keyword2"),
  )
  val entity: SkillEntity = SkillEntity(
    id = UUID.randomUUID(),
    name = "skill1",
    keyWords = keyword
  )

  "from entity to model" in {
    SkillMapper.entity2Model(entity) mustEqual Skill(
      id = entity.id,
      name = entity.name,
      keyWords = keyword.map(SkillKeyWordMapper.entity2Model)
    )
  }

  "from model to entity" in {
    SkillMapper.model2Entity(
      Skill(
        id = entity.id,
        name = entity.name,
        keyWords = keyword.map(SkillKeyWordMapper.entity2Model))
    ) mustEqual entity
  }
}
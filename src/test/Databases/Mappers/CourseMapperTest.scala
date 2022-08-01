package Databases.Mappers

import Databases.Mappers.Kas.AbilityMapperTest.keyword
import Databases.Mappers.Kas.{AbilityMapper, KnowledgeMapper, SkillMapper}
import Databases.Mappers.Keywords.AbilityKeyWordMapper
import Databases.Models.Dao.CourseEntity
import Databases.Models.Dao.Kas.{AbilityEntity, KnowledgeEntity, SkillEntity}
import Databases.Models.Dao.Keywords.{AbilityKeyWordEntity, KnowledgeKeyWordEntity, SkillKeyWordEntity}
import Databases.Models.Domain.{Ability, Course}
import org.specs2.mutable.Specification

import java.util.UUID

object CourseMapperTest extends Specification {
  val inputKnowledgeKeyWords: Seq[KnowledgeKeyWordEntity] = Seq(
    KnowledgeKeyWordEntity(UUID.randomUUID(), "InKnowledgeKeyword1"),
    KnowledgeKeyWordEntity(UUID.randomUUID(), "InKnowledgeKeyword2"),
  )
  val outputKnowledgeKeyWords: Seq[KnowledgeKeyWordEntity] = Seq(
    KnowledgeKeyWordEntity(UUID.randomUUID(), "OutKnowledgeKeyword1"),
    KnowledgeKeyWordEntity(UUID.randomUUID(), "OutKnowledgeKeyword2"),
  )

  val inputKnowledge: Seq[KnowledgeEntity] = Seq(
    KnowledgeEntity(UUID.randomUUID(), "InKnowledge1", keyWords = inputKnowledgeKeyWords),
    KnowledgeEntity(UUID.randomUUID(), "InKnowledge2", keyWords = inputKnowledgeKeyWords),
  )
  val outputKnowledge: Seq[KnowledgeEntity] = Seq(
    KnowledgeEntity(UUID.randomUUID(), "OutKnowledge1", keyWords = outputKnowledgeKeyWords),
    KnowledgeEntity(UUID.randomUUID(), "OutKnowledge2", keyWords = outputKnowledgeKeyWords),
  )


  val inputAbilityKeyWords: Seq[AbilityKeyWordEntity] = Seq(
    AbilityKeyWordEntity(UUID.randomUUID(), "InAbilityKeyword1"),
    AbilityKeyWordEntity(UUID.randomUUID(), "InAbilityKeyword2"),
  )
  val outputAbilityKeyWords: Seq[AbilityKeyWordEntity] = Seq(
    AbilityKeyWordEntity(UUID.randomUUID(), "OutAbilityKeyword1"),
    AbilityKeyWordEntity(UUID.randomUUID(), "OutAbilityKeyword2"),
  )

  val inputAbilities: Seq[AbilityEntity] = Seq(
    AbilityEntity(UUID.randomUUID(), "InAbility1", keyWords = inputAbilityKeyWords),
    AbilityEntity(UUID.randomUUID(), "InAbility2", keyWords = inputAbilityKeyWords),
  )
  val outputAbilities: Seq[AbilityEntity] = Seq(
    AbilityEntity(UUID.randomUUID(), "OutAbility1", keyWords = outputAbilityKeyWords),
    AbilityEntity(UUID.randomUUID(), "OutAbility2", keyWords = outputAbilityKeyWords),
  )


  val inputSkillsKeyWords: Seq[SkillKeyWordEntity] = Seq(
    SkillKeyWordEntity(UUID.randomUUID(), "InSkillKeyword1"),
    SkillKeyWordEntity(UUID.randomUUID(), "InSkillKeyword2"),
  )
  val outputSkillsKeyWords: Seq[SkillKeyWordEntity] = Seq(
    SkillKeyWordEntity(UUID.randomUUID(), "OutSkillKeyword1"),
    SkillKeyWordEntity(UUID.randomUUID(), "OutSkillKeyword2"),
  )

  val inputSkills: Seq[SkillEntity] = Seq(
    SkillEntity(UUID.randomUUID(), "InSkill1", keyWords = inputSkillsKeyWords),
    SkillEntity(UUID.randomUUID(), "InSkill2", keyWords = inputSkillsKeyWords),
  )
  val outputSkills: Seq[SkillEntity] = Seq(
    SkillEntity(UUID.randomUUID(), "OutSkill1", keyWords = outputSkillsKeyWords),
    SkillEntity(UUID.randomUUID(), "OutSkill2", keyWords = outputSkillsKeyWords)
  )


  val entity: CourseEntity = CourseEntity(
    id = UUID.randomUUID(),
    name = "Курс1",
    inputSkills = inputSkills,
    outputSkills = outputSkills,
    inputAbilities = inputAbilities,
    outputAbilities = outputAbilities,
    inputKnowledge = inputKnowledge,
    outputKnowledge = outputKnowledge
  )

  "from entity to model" in {
    CourseMapper.entity2Model(entity) mustEqual Course(
      id = entity.id,
      name = entity.name,
      inputSkills = inputSkills.map(SkillMapper.entity2Model),
      outputSkills = outputSkills.map(SkillMapper.entity2Model),
      inputAbility = inputAbilities.map(AbilityMapper.entity2Model),
      outputAbility = outputAbilities.map(AbilityMapper.entity2Model),
      inputKnowledge = inputKnowledge.map(KnowledgeMapper.entity2Model),
      outputKnowledge = outputKnowledge.map(KnowledgeMapper.entity2Model)
    )
  }

  "from model to entity" in {
    CourseMapper.model2Entity(
      Course(
        id = entity.id,
        name = entity.name,
        inputSkills = inputSkills.map(SkillMapper.entity2Model),
        outputSkills = outputSkills.map(SkillMapper.entity2Model),
        inputAbility = inputAbilities.map(AbilityMapper.entity2Model),
        outputAbility = outputAbilities.map(AbilityMapper.entity2Model),
        inputKnowledge = inputKnowledge.map(KnowledgeMapper.entity2Model),
        outputKnowledge = outputKnowledge.map(KnowledgeMapper.entity2Model)
      )
    ) mustEqual entity
  }
}
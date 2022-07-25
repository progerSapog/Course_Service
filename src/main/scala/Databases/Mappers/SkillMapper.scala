package Databases.Mappers

import Databases.Models.Dao.SkillEntity
import Databases.Models.Domain.Skill

/**
 * Маппер SkillEntity/Skill
 *
 * @see ISkillMapper
 * */
object SkillMapper extends ISkillMapper {
  /**
   * Перевод из SkillEntity в Skill
   *
   * @param skillEntity Entity для перевода
   * @return полученная Model
   */
  override def entity2Model(skillEntity: SkillEntity): Skill =
    Skill(skillEntity.id,
      skillEntity.name,
      skillEntity.keyWords.map(SkillKeyWordMapper.entity2Model))

  /**
   * Перевод из Skill в SkillEntity
   *
   * @param skill Model для перевода
   * @return полученная Entity
   */
  override def model2Entity(skill: Skill): SkillEntity =
    SkillEntity(skill.id,
      skill.name,
      skill.keyWords.map(SkillKeyWordMapper.model2Entity))
}
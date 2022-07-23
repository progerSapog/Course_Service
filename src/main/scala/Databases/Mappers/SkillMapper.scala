package Databases.Mappers

import Databases.Models.Dao.{IKASEntity, SkillEntity}
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
    Skill(skillEntity.id, skillEntity.name)

  /**
   * Перевод из Skill в SkillEntity
   *
   * @param skill Model для перевода
   * @return полученная Entity
   */
  override def model2Entity(skill: Skill): SkillEntity =
    SkillEntity(skill.id, skill.name)

  /**
   * Перевод из общего вида ЗУН'ов - IKSAEntity в SkillEntity
   *
   * @param entity ЗУН общего вида для перевода
   * @return ЗУН вида SkillEntity
   */
  override def iKASEntity2Entity(entity: IKASEntity): SkillEntity =
    SkillEntity(entity.id, entity.name)
}

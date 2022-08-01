package Databases.Mappers.Kas

import Databases.Mappers.IAbilityMapper
import Databases.Mappers.Keywords.AbilityKeyWordMapper
import Databases.Models.Dao.Kas.AbilityEntity
import Databases.Models.Domain.Ability

/**
 * Маппер AbilityEntity/Ability
 *
 * @see IAbilityMapper
 * */
object AbilityMapper extends IAbilityMapper {
  /**
   * Перевод из AbilityEntity в Ability
   *
   * @param abilityEntity Entity для перевода
   * @return полученная Model
   */
  override def entity2Model(abilityEntity: AbilityEntity): Ability =
    Ability(abilityEntity.id,
      abilityEntity.name,
      abilityEntity.keyWords.map(AbilityKeyWordMapper.entity2Model))

  /**
   * Перевод из Ability в AbilityEntity
   *
   * @param ability Model для перевода
   * @return полученная Entity
   */
  override def model2Entity(ability: Ability): AbilityEntity =
    AbilityEntity(ability.id,
      ability.name,
      ability.keyWords.map(AbilityKeyWordMapper.model2Entity))
}

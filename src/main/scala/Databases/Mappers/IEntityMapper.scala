package Databases.Mappers

import Databases.Models.Dao._
import Databases.Models.Domain._

/**
 * Общий трейт для мапперов entity и model классов
 *
 * @tparam EntityType тип Entity
 * @tparam ModelType  тип Model
 * @see IEntity
 * @see IModel
 */
sealed trait IEntityMapper[EntityType <: IEntity, ModelType <: IModel] {
  /**
   * Перевод Entity в Model
   *
   * @param entity которая будет переведена
   * @return полученная Model
   */
  def entity2Model(entity: EntityType): ModelType

  /**
   * Перевод Model в Entity
   *
   * @param model которая будет передевена
   * @return полученна Entity
   */
  def model2Entity(model: ModelType): EntityType
}

trait ICourseMapper extends IEntityMapper[CourseEntity, Course]

trait IKnowledgeKeyWordMapper extends IEntityMapper[KnowledgeKeyWordEntity, KnowledgeKeyWord]

trait IAbilityKeyWordMapper extends IEntityMapper[AbilityKeyWordEntity, AbilityKeyWord]

trait ISkillKeyWordMapper extends IEntityMapper[SkillKeyWordEntity, SkillKeyWord]

trait ISkillMapper extends IEntityMapper[SkillEntity, Skill]

trait IAbilityMapper extends IEntityMapper[AbilityEntity, Ability]

trait IKnowledgeMapper extends IEntityMapper[KnowledgeEntity, Knowledge]

package Databases.Mappers

import Databases.Models.Dao.SkillKeyWordEntity
import Databases.Models.Domain.SkillKeyWord

object SkillKeyWordMapper extends ISkillKeyWordMapper {
  /**
   * Перевод Entity в Model
   *
   * @param entity которая будет переведена
   * @return полученная Model
   */
  override def entity2Model(entity: SkillKeyWordEntity): SkillKeyWord =
    SkillKeyWord(entity.id, entity.name)

  /**
   * Перевод Model в Entity
   *
   * @param model которая будет передевена
   * @return полученна Entity
   */
  override def model2Entity(model: SkillKeyWord): SkillKeyWordEntity =
    SkillKeyWordEntity(model.id, model.name)
}
package Databases.Mappers.Keywords

import Databases.Mappers.IAbilityKeyWordMapper
import Databases.Models.Dao.Keywords.AbilityKeyWordEntity
import Databases.Models.Domain.AbilityKeyWord

object AbilityKeyWordMapper extends IAbilityKeyWordMapper {
  /**
   * Перевод Entity в Model
   *
   * @param entity которая будет переведена
   * @return полученная Model
   */
  override def entity2Model(entity: AbilityKeyWordEntity): AbilityKeyWord =
    AbilityKeyWord(entity.id, entity.name)

  /**
   * Перевод Model в Entity
   *
   * @param model которая будет передевена
   * @return полученна Entity
   */
  override def model2Entity(model: AbilityKeyWord): AbilityKeyWordEntity =
    AbilityKeyWordEntity(model.id, model.name)
}

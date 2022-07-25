package Databases.Mappers

import Databases.Models.Dao.KnowledgeKeyWordEntity
import Databases.Models.Domain.KnowledgeKeyWord

object KnowledgeKeyWordMapper extends IKnowledgeKeyWordMapper {
  /**
   * Перевод Entity в Model
   *
   * @param entity которая будет переведена
   * @return полученная Model
   */
  override def entity2Model(entity: KnowledgeKeyWordEntity): KnowledgeKeyWord =
    KnowledgeKeyWord(entity.id, entity.name)

  /**
   * Перевод Model в Entity
   *
   * @param model которая будет передевена
   * @return полученна Entity
   */
  override def model2Entity(model: KnowledgeKeyWord): KnowledgeKeyWordEntity =
    KnowledgeKeyWordEntity(model.id, model.name)
}
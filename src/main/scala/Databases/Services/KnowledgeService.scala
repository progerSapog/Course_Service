package Databases.Services

import Databases.Mappers.KnowledgeMapper
import Databases.Models.Dao.KnowledgeEntity
import Databases.Models.Domain.Knowledge
import scalikejdbc.interpolation.SQLSyntax

import java.util.UUID

/**
 * Сервис для работы с Knowledge
 *
 * @see KnowledgeEntity
 */
object KnowledgeService extends IKnowledgeService {
  /**
   * Получение всех Knowledge
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Model
   */
  override def findAll(limit: Int, offset: Int, orderBy: SQLSyntax, sort: SQLSyntax, dbName: String): Seq[Knowledge] =
    KnowledgeEntity.findAll(limit, offset, orderBy, sort, dbName).map(KnowledgeMapper.entity2Model)

  /**
   * Получение Knowledge по id
   *
   * @param id     Model которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Model если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID, dbName: String): Option[Knowledge] =
    KnowledgeEntity.findById(id, dbName).map(KnowledgeMapper.entity2Model)

  /**
   * Вставка новой Knowledge
   *
   * @param model  которую необходимо вставить
   * @param dbName имя БД с которой мы хотим работать
   */
  override def insert(model: Knowledge, dbName: String): Unit =
    KnowledgeEntity.insert(KnowledgeMapper.model2Entity(model), dbName)

  /**
   * Удаление Knowledge по id
   *
   * @param id     Model которую необходимо удалить
   * @param dbName имя БД с которой мы хотим работать
   */
  override def deleteById(id: UUID, dbName: String): Unit =
    KnowledgeEntity.deleteById(id, dbName)

  /**
   * Обновление Knowledge
   *
   * @param model  которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  override def update(model: Knowledge, dbName: String): Unit =
    KnowledgeEntity.update(KnowledgeMapper.model2Entity(model), dbName)
}

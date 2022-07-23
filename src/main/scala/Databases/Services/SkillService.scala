package Databases.Services

import Databases.Mappers.SkillMapper
import Databases.Models.Dao.SkillEntity
import Databases.Models.Domain.Skill
import scalikejdbc.interpolation.SQLSyntax

import java.util.UUID

/**
 * Сервис для работы с Skill
 *
 * @see SkillEntity
 */
object SkillService extends ISkillService {
  /**
   * Получение всех Skill
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Model
   */
  override def findAll(limit: Int, offset: Int, orderBy: SQLSyntax, sort: SQLSyntax, dbName: String): Seq[Skill] =
    SkillEntity.findAll(limit, offset, orderBy, sort, dbName).map(SkillMapper.entity2Model)

  /**
   * Получение Skill по id
   *
   * @param id     Model которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Model если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID, dbName: String): Option[Skill] =
    SkillEntity.findById(id, dbName).map(SkillMapper.entity2Model)

  /**
   * Вставка новой Skill
   *
   * @param model  которую необходимо вставить
   * @param dbName имя БД с которой мы хотим работать
   */
  override def insert(model: Skill, dbName: String): Unit =
    SkillEntity.insert(SkillMapper.model2Entity(model), dbName)

  /**
   * Удаление Skill по id
   *
   * @param id     Model которую необходимо удалить
   * @param dbName имя БД с которой мы хотим работать
   */
  override def deleteById(id: UUID, dbName: String): Unit =
    SkillEntity.deleteById(id, dbName)

  /**
   * Обновление Skill
   *
   * @param model  которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  override def update(model: Skill, dbName: String): Unit =
    SkillEntity.update(SkillMapper.model2Entity(model), dbName)
}

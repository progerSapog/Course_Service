//package Databases.Services
//
//import Databases.Mappers.AbilityMapper
//import Databases.Models.Dao.Kas.AbilityEntity
//import Databases.Models.Domain.Ability
//import scalikejdbc.interpolation.SQLSyntax
//
//import java.util.UUID
//
///**
// * Сервис для работы с Ability
// *
// * @see AbilityEntity
// */
//object AbilityService extends IAbilityService {
//  /**
//   * Получение всех Ability
//   *
//   * @param limit   кол-во записей которые необходимо получить
//   * @param offset  отсутуп от начала полученных записей
//   * @param orderBy поле по которому необходимо отсортировать записи
//   * @param sort    порядок сортировки
//   * @param dbName  имя БД с которой мы хотим работать
//   * @return последовательность всех Model
//   */
//  override def findAll(limit: Int, offset: Int, orderBy: SQLSyntax, sort: SQLSyntax, dbName: String): Seq[Ability] =
//    AbilityEntity.findAll(limit, offset, orderBy, sort, dbName).map(AbilityMapper.entity2Model)
//
//  /**
//   * Получение Ability по id
//   *
//   * @param id     Model которую необходимо получить
//   * @param dbName имя БД с которой мы хотим работать
//   * @return Optional с Model если такая есть в БД, иначе Option.empty
//   */
//  override def findById(id: UUID, dbName: String): Option[Ability] =
//    AbilityEntity.findById(id, dbName).map(AbilityMapper.entity2Model)
//
//  /**
//   * Вставка новой Ability
//   *
//   * @param model  которую необходимо вставить
//   * @param dbName имя БД с которой мы хотим работать
//   */
//  override def insert(model: Ability, dbName: String): Unit =
//    AbilityEntity.insert(AbilityMapper.model2Entity(model), dbName)
//
//  /**
//   * Удаление Ability по id
//   *
//   * @param id     Model которую необходимо удалить
//   * @param dbName имя БД с которой мы хотим работать
//   */
//  override def deleteById(id: UUID, dbName: String): Unit =
//    AbilityEntity.deleteById(id, dbName)
//
//  /**
//   * Обновление Ability
//   *
//   * @param model  которое будет обновлено
//   * @param dbName имя БД с которой мы хотим работать
//   */
//  override def update(model: Ability, dbName: String): Unit =
//    AbilityEntity.update(AbilityMapper.model2Entity(model), dbName)
//}

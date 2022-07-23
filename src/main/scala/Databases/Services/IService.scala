package Databases.Services

import Databases.Configurations.{ASC, Id}
import Databases.Models.Domain._
import scalikejdbc.interpolation.SQLSyntax

import java.util.UUID

/**
 * Общий Трейт всех сервисов
 *
 * @tparam ModelType тип Model с которым работает сервис
 */
sealed trait IService[ModelType <: IModel] {
  /**
   * Получение всех Model
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Model
   */
  def findAll(limit: Int = 100,
              offset: Int = 0,
              orderBy: SQLSyntax = Id.value,
              sort: SQLSyntax = ASC.value,
              dbName: String): Seq[ModelType]

  /**
   * Получение Model по id
   *
   * @param id     Model которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Model если такая есть в БД, иначе Option.empty
   */
  def findById(id: UUID, dbName: String): Option[ModelType]

  /**
   * Вставка новой Model
   *
   * @param model  которую необходимо вставить
   * @param dbName имя БД с которой мы хотим работать
   */
  def insert(model: ModelType, dbName: String): Unit

  /**
   * Удаление Model по id
   *
   * @param id     Model которую необходимо удалить
   * @param dbName имя БД с которой мы хотим работать
   */
  def deleteById(id: UUID, dbName: String): Unit

  /**
   * Обновление Model
   *
   * @param model  которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  def update(model: ModelType, dbName: String): Unit
}

/**
 * Частные, параметризированные трейты сервисов.
 * */
trait ISkillService extends IService[Skill]

trait IAbilityService extends IService[Ability]

trait IKnowledgeService extends IService[Knowledge]

trait ICourseService extends IService[Course]



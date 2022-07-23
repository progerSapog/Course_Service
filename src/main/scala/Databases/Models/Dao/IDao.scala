package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import scalikejdbc.ParameterBinderFactory
import scalikejdbc.interpolation.SQLSyntax

import java.util.UUID

/**
 * Общий Трейт всех DAO
 *
 * @tparam EntityType тип Entity с которым работает DAO
 */
sealed trait IDao[EntityType <: IEntity] {
  implicit val uuidFactory: ParameterBinderFactory[UUID] = ParameterBinderFactory[UUID] {
    value => (stmt, idx) => stmt.setObject(idx, value)
  }

  /**
   * Получение всех Entity из таблицы
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Entity из таблицы
   */
  def findAll(limit: Int = 100,
              offset: Int = 0,
              orderBy: SQLSyntax = Id.value,
              sort: SQLSyntax = ASC.value,
              dbName: String): Seq[EntityType]

  /**
   * Получение Entity из таблицы по id
   *
   * @param id     Entity которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  def findById(id: UUID, dbName: String): Option[EntityType]

  /**
   * Вставка новой Entity в таблицу
   *
   * @param entity Entity которую необходимо вставить в таблицу
   * @param dbName - имя БД с которой мы хотим работать
   */
  def insert(entity: EntityType, dbName: String): Unit

  /**
   * Вставка сразу нескольких KAS в БД
   *
   * @param entityList список KAS которые мы хотим вставить
   * @param dbName     имя БД с которой мы хотим работать
   */
  def insertMultiRows(entityList: Seq[EntityType], dbName: String): Unit

  /**
   * Удаление Entity из таблицы по id
   *
   * @param id     Entity которую необходимо удалить
   * @param dbName имя БД с которой мы хотим работать
   */
  def deleteById(id: UUID, dbName: String): Unit

  /**
   * Обновление Entity в таблице
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  def update(entity: EntityType, dbName: String): Unit
}

trait ICourseDao extends IDao[CourseEntity]

/**
 * DAO Трейты для ЗУНов
 *
 * @tparam IKASType тип KAS
 */
sealed trait IKASDao[IKASType <: IKASEntity] extends IDao[IKASType]

trait ISkillDao extends IKASDao[SkillEntity]

trait IAbilityDao extends IKASDao[AbilityEntity]

trait IKnowledgeDao extends IKASDao[KnowledgeEntity]


/**
 * DAO трейт для ключевых слов
 *
 * @tparam KeyWordType тип ключевого слова
 */
sealed trait IKeyWordDao[KeyWordType <: IEntity] extends IDao[KeyWordType]

trait IKnowledgeKeyWordDao extends IKeyWordDao[KnowledgeKeyWordEntity]

trait IAbilityKeyWordDao extends IKeyWordDao[AbilityKeyWordEntity]

trait ISkillKeyWordDao extends IKeyWordDao[SkillKeyWordEntity]


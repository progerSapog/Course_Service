package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.Kas.{AbilityEntity, KnowledgeEntity, SkillEntity}
import Databases.Models.Dao.Keywords.{AbilityKeyWordEntity, KnowledgeKeyWordEntity, SkillKeyWordEntity}
import Databases.Models.Dao.Plugs.{AbilityPlug, IPlug, KnowledgePlug, SkillPlug}
import scalikejdbc.{DBSession, ParameterBinderFactory}
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
   * @return последовательность всех Entity из таблицы
   */
  def findAll(limit: Int = 100,
              offset: Int = 0,
              orderBy: SQLSyntax = Id.value,
              sort: SQLSyntax = ASC.value)
             (implicit session: DBSession): Seq[EntityType]

  /**
   * Получение Entity из таблицы по id
   *
   * @param id     Entity которую необходимо получить
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  def findById(id: UUID)
              (implicit session: DBSession): Option[EntityType]

  /**
   * Вставка новой Entity в таблицу
   *
   * @param entity Entity которую необходимо вставить в таблицу
   */
  def insert(entity: EntityType)
            (implicit session: DBSession): Unit

  /**
   * Вставка сразу нескольких KAS'ов в БД
   *
   * @param entityList список KAS'ов которые мы хотим вставить
   */
  def insertMultiRows(entityList: Seq[EntityType])
                     (implicit session: DBSession): Unit

  /**
   * Удаление Entity из таблицы по id
   *
   * @param id     Entity которую необходимо удалить
   */
  def deleteById(id: UUID)
                (implicit session: DBSession): Unit

  /**
   * Обновление Entity в таблице
   *
   * @param entity Entity которое будет обновлено
   */
  def update(entity: EntityType)
            (implicit session: DBSession): Unit
}

trait ICourseDao extends IDao[CourseEntity]

/**
 * DAO Трейты для KAS'ов
 *
 * @tparam KASType тип KAS'а
 */
sealed trait IKASDao[KASType <: IKASEntity, PlugType <: IPlug] extends IDao[KASType] {
  /**
   * Вставка связей KAS'а и ключевых слов в таблицу связи "имяKAS_keyword_link"
   *
   * @param kasEntity KAS для которого всталвяются связи с ключевыми слови
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  def insertKeyWord(kasEntity: KASType)
                   (implicit dbSession: DBSession): Unit

  /**
   * Выборка ключевых слов KAS'а через таблицу связи "имяKAS_keyword_link"
   *
   * @param plug      KAS для которого ищутся ключевые слова
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные навыки
   */
  def findKeyWords(plug: PlugType)
                  (implicit dbSession: DBSession): Seq[IKeyWordEntity]

  /**
   * Удаление связей KAS'а и ключевых слов из таблицы связи "имяKAS_keyword_link"
   *
   * @param kasEntity KAS, связи с котором будут удалены
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  def deleteKeyWords(kasEntity: KASType)
                    (implicit dbSession: DBSession): Unit
}

trait IKnowledgeDao extends IKASDao[KnowledgeEntity, KnowledgePlug]

trait IAbilityDao extends IKASDao[AbilityEntity, AbilityPlug]

trait ISkillDao extends IKASDao[SkillEntity, SkillPlug]


/**
 * DAO трейт для ключевых слов
 *
 * @tparam KeyWordType тип ключевого слова
 */
sealed trait IKeyWordDao[KeyWordType <: IEntity] extends IDao[KeyWordType]

trait IKnowledgeKeyWordDao extends IKeyWordDao[KnowledgeKeyWordEntity]

trait IAbilityKeyWordDao extends IKeyWordDao[AbilityKeyWordEntity]

trait ISkillKeyWordDao extends IKeyWordDao[SkillKeyWordEntity]
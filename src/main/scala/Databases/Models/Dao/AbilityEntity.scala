package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.AbilityKeyWordEntity.akw
import Databases.Models.Dao.LinkTables.AbilityKeyWordLink
import Databases.Models.Dao.LinkTables.AbilityKeyWordLink.{akwl, akwlC}
import Databases.Models.Dao.Plugs.AbilityPlug
import Databases.Models.Dao.Plugs.AbilityPlug.{a, aC}
import scalikejdbc._

import java.util.UUID

/**
 * Сущность Умение (ability).
 * Содержит связанные с данным умением ключевые слова
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 * @see IKASEntity
 */
case class AbilityEntity(id: UUID,
                         name: String,
                         keyWords: Seq[AbilityKeyWordEntity]) extends IKASEntity

/**
 * Объект компаньон, выполняющий роль DAO.
 * Реализует стандартные CRUD операции.
 *
 * @see IAbilityDao
 * @see UUIDFactory
 */
object AbilityEntity extends IAbilityDao {

  /**
   * Вставка связей Умения и ключевых слов в таблицу связи ability_keyword_link
   *
   * @param kasEntity умение для которого ищутся ключевые слова
   * @param session   имплисит, позволяющий вызывать метод внутри сессии
   */
  override def insertKeyWord(kasEntity: AbilityEntity)
                            (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = kasEntity.keyWords.map(word => Seq(kasEntity.id, word.id))

    withSQL {
      insertInto(AbilityKeyWordLink)
        .namedValues(
          akwlC.abilityId -> sqls.?,
          akwlC.keywordId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Выборка ключевых слов Умения через таблицу связи ability_keyword_link
   *
   * @param abilityPlug умение для которого ищутся ключевые слова
   * @param session     имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные ключевые слова
   */
  override def selectKeyWords(abilityPlug: AbilityPlug)
                             (implicit session: DBSession): Seq[AbilityKeyWordEntity] = {
    withSQL {
      selectFrom(AbilityKeyWordEntity as akw)
        .leftJoin(AbilityKeyWordLink as akwl)
        .on(akw.id, akwl.abilityId)
        .where.eq(akwl.abilityId, abilityPlug.id)
    }.map(AbilityKeyWordEntity(akw.resultName)).collection.apply()
  }

  /**
   * Удаление связей умения и ключевых слов из таблицы ability_keyword_link
   *
   * @param ability умение, связи с котором будут удалены
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  override def deleteKeyWords(ability: AbilityEntity)
                             (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(AbilityKeyWordLink)
        .where.eq(akwlC.abilityId, ability.id)
    }.update.apply()
  }

  /**
   * Вставка умения в БД
   *
   * @param entity Умение которое необходимо вставить в таблицу
   */
  override def insert(entity: AbilityEntity)
                     (implicit session: DBSession): Unit = {
    withSQL {
      insertInto(AbilityPlug)
        .namedValues(
          aC.id -> entity.id,
          aC.name -> entity.name
        )
    }.update.apply()

    insertKeyWord(entity)
  }


  /**
   * Вставка сразу нескольких ability в БД
   *
   * @param abilities список abilities которые мы хотим вставить
   */
  override def insertMultiRows(abilities: Seq[AbilityEntity])
                              (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = abilities.map(ability => Seq(ability.id, ability.name))

    withSQL {
      insertInto(AbilityPlug)
        .namedValues(
          aC.id -> sqls.?,
          aC.name -> sqls.?
        )
    }.batch(batchParams: _*).apply()

    abilities.foreach(insertKeyWord)
  }

  /**
   * Получение Умения по id
   *
   * @param id Умения которое необходимо получить
   * @return Optional с Умением
   */
  override def findById(id: UUID)
                       (implicit session: DBSession): Option[AbilityEntity] = {
    val abilityPlug: Option[AbilityPlug] =
      withSQL {
        select.from(AbilityPlug as a)
          .where.eq(a.id, id)
      }.map(AbilityPlug(a.resultName)).single.apply()

    abilityPlug.map(plug => AbilityEntity(
      id = plug.id,
      name = plug.name,
      keyWords = AbilityEntity.selectKeyWords(plug))
    )
  }

  /**
   * Получение указанного кол-ва умений
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @return последовательность всех Умений из таблицы
   */
  override def findAll(limit: Int = 100,
                       offset: Int = 0,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value)
                      (implicit session: DBSession): Seq[AbilityEntity] = {
    val abilitiesPlugs: Seq[AbilityPlug] =
      withSQL {
        select.all(a).from(AbilityPlug as a)
          .orderBy(orderBy).append(sort)
          .limit(limit)
          .offset(offset)
      }.map(AbilityPlug(a.resultName)).collection.apply()

    abilitiesPlugs.map(plug => AbilityEntity(
      id = plug.id,
      name = plug.name,
      keyWords = AbilityEntity.selectKeyWords(plug))
    )
  }

  /**
   * Обновление записи об Умениий
   *
   * @param entity Умение которое будет обновлено
   */
  override def update(entity: AbilityEntity)
                     (implicit session: DBSession): Unit = {
    withSQL {
      QueryDSL.update(AbilityPlug)
        .set(
          aC.name -> entity.name
        ).where.eq(aC.id, entity.id)
    }.update.apply()

    deleteKeyWords(entity)
    insertKeyWord(entity)
  }

  /**
   * Удаление умения по его ID
   *
   * @param id Entity которую необходимо удалить
   */
  override def deleteById(id: UUID)
                         (implicit session: DBSession): Unit =
    withSQL {
      deleteFrom(AbilityPlug)
        .where.eq(aC.id, id)
    }.update.apply()

}
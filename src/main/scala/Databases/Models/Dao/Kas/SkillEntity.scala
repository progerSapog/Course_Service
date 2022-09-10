package Databases.Models.Dao.Kas

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.Keywords.SkillKeyWordEntity
import Databases.Models.Dao.Keywords.SkillKeyWordEntity.skw
import Databases.Models.Dao.LinkTables.SkillKeyWordLink
import Databases.Models.Dao.LinkTables.SkillKeyWordLink.{skwl, skwlC}
import Databases.Models.Dao.Plugs.SkillPlug
import Databases.Models.Dao.Plugs.SkillPlug.{s, sC}
import Databases.Models.Dao.{IKASEntity, ISkillDao}
import scalikejdbc._

import java.util.UUID

/**
 * Сущность Навык (skill)
 * Содержит связанные с данным навыком ключевые слова
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 * @see IKASEntity
 */
case class SkillEntity(id: UUID,
                       name: String,
                       keyWords: Seq[SkillKeyWordEntity]) extends IKASEntity

/**
 * Объект компаньон, выполняющий роль DAO.
 * Реализует стандартные CRUD операции.
 * Позвоялет работать с данным отображением при помощи type safe DSL
 *
 * @see ISkillDao
 * @see UUIDFactory
 */
object SkillEntity extends ISkillDao {

  /**
   * Вставка связей Навыка и ключевых слов в таблицу связи skill_keyword_link
   *
   * @param skill   навык для которого ищутся ключевые слова
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  override def insertKeyWord(skill: SkillEntity)
                            (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = skill.keyWords.map(word => Seq(skill.id, word.id))

    withSQL {
      insertInto(SkillKeyWordLink)
        .namedValues(
          skwlC.skillId -> sqls.?,
          skwlC.keywordId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Выборка ключевых слов Навыка через таблицу связи skill_keyword_link
   *
   * @param skillPlug навык для которого ищутся ключевые слова
   * @param session   имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные ключевые слова
   */
  override def findKeyWords(skillPlug: SkillPlug)
                           (implicit session: DBSession): Seq[SkillKeyWordEntity] = {
    withSQL {
      selectFrom(SkillKeyWordEntity as skw)
        .leftJoin(SkillKeyWordLink as skwl)
        .on(skw.id, skwl.keywordId)
        .where.eq(skwl.skillId, skillPlug.id)
    }.map(SkillKeyWordEntity(skw.resultName)).collection.apply()
  }

  /**
   * Удаление связей навыка и ключевых слов из таблицы skill_keyword_link
   *
   * @param skill   навык, связи с котором будут удалены
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  override def deleteKeyWords(skill: SkillEntity)
                             (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(SkillKeyWordLink)
        .where.eq(skwlC.skillId, skill.id)
    }.update.apply()
  }

  /**
   * Вставка навыка в БД
   *
   * @param entity Навык который необходимо вставить в таблицу
   */
  override def insert(entity: SkillEntity)
                     (implicit session: DBSession): Unit = {
    withSQL {
      insertInto(SkillPlug)
        .namedValues(
          sC.id -> entity.id,
          sC.name -> entity.name
        )
    }.update.apply()

    insertKeyWord(entity)
  }

  /**
   * Вставка сразу нескольких skill в БД
   *
   * @param skills список skills которые мы хотим вставить
   */
  override def insertMultiRows(skills: Seq[SkillEntity])
                              (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = skills.map(skill => Seq(skill.id, skill.name))

    withSQL {
      insertInto(SkillPlug)
        .namedValues(
          sC.id -> sqls.?,
          sC.name -> sqls.?
        )
    }.batch(batchParams: _*).apply()

    skills.foreach(insertKeyWord)
  }

  /**
   * Получение Навыка по id
   *
   * @param id Навык который необходимо получить
   * @return Optional с Навыком
   */
  override def findById(id: UUID)
                       (implicit session: DBSession): Option[SkillEntity] = {
    val skillPlug: Option[SkillPlug] =
      withSQL {
        select.from(SkillPlug as s)
          .where.eq(s.id, id)
      }.map(SkillPlug(s.resultName)).single.apply()

    skillPlug.map(plug => SkillEntity(
      id = plug.id,
      name = plug.name,
      keyWords = SkillEntity.findKeyWords(plug))
    )
  }

  /**
   * Получение указаного кол-ва навыков
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @return последовательность всех Entity из таблицы
   */
  override def findAll(limit: Int = 100,
                       offset: Int = 0,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value)
                      (implicit session: DBSession): Seq[SkillEntity] = {
    val skillPlugs: Seq[SkillPlug] =
      withSQL {
        select.all(s).from(SkillPlug as s)
          .orderBy(orderBy).append(sort)
          .limit(limit)
          .offset(offset)
      }.map(SkillPlug(s.resultName)).collection.apply()

    skillPlugs.map(plug => SkillEntity(
      id = plug.id,
      name = plug.name,
      keyWords = SkillEntity.findKeyWords(plug))
    )
  }

  /**
   * Обновление записи о Навыке
   *
   * @param entity Entity которое будет обновлено
   */
  override def update(entity: SkillEntity)
                     (implicit session: DBSession): Unit = {
    withSQL {
      QueryDSL.update(SkillPlug)
        .set(
          sC.name -> entity.name
        ).where.eq(sC.id, entity.id)
    }.update.apply()

    deleteKeyWords(entity)
    insertKeyWord(entity)
  }

  /**
   * Удаление навыка по его id
   *
   * @param id Entity которую необходимо удалить
   */
  override def deleteById(id: UUID)
                         (implicit session: DBSession): Unit =
    withSQL {
      deleteFrom(SkillPlug)
        .where.eq(sC.id, id)
    }.update.apply()
}
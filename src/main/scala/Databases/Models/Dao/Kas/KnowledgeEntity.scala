package Databases.Models.Dao.Kas

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.Keywords.KnowledgeKeyWordEntity
import Databases.Models.Dao.Keywords.KnowledgeKeyWordEntity.kkw
import Databases.Models.Dao.LinkTables.KnowledgeKeyWordLink
import Databases.Models.Dao.LinkTables.KnowledgeKeyWordLink.{kkwl, kkwlC}
import Databases.Models.Dao.Plugs.KnowledgePlug
import Databases.Models.Dao.Plugs.KnowledgePlug.{k, kC}
import Databases.Models.Dao.{IKASEntity, IKnowledgeDao}
import scalikejdbc._

import java.util.UUID

/**
 * Сущность Знание (ability).
 * Содержит связанные с данным знанием ключевые слова
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 * @see IKASEntity
 */
case class KnowledgeEntity(id: UUID,
                           name: String,
                           keyWords: Seq[KnowledgeKeyWordEntity]) extends IKASEntity

/**
 * Объект компаньон, выполняющий роль DAO.
 * Реализует стандартные CRUD операции.
 *
 * @see IKnowledgeDao
 * @see UUIDFactory
 */
object KnowledgeEntity extends IKnowledgeDao {

  /**
   * Вставка связей Знания и ключевых слов в таблицу связи knowledge_keyword_link
   *
   * @param kasEntity знание для которого ищутся ключевые слова
   * @param session   имплисит, позволяющий вызывать метод внутри сессии
   */
  override def insertKeyWord(kasEntity: KnowledgeEntity)
                            (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = kasEntity.keyWords.map(word => Seq(kasEntity.id, word.id))

    withSQL {
      insertInto(KnowledgeKeyWordLink)
        .namedValues(
          kkwlC.knowledgeId -> sqls.?,
          kkwlC.keywordId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Выборка ключевых слов Знания через таблицу связи knowledge_keyword_link
   *
   * @param knowledgePlug знание для которого ищутся ключевые слова
   * @param session       имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные ключевые слова
   */
  override def selectKeyWords(knowledgePlug: KnowledgePlug)
                             (implicit session: DBSession): Seq[KnowledgeKeyWordEntity] = {
    withSQL {
      selectFrom(KnowledgeKeyWordEntity as kkw)
        .leftJoin(KnowledgeKeyWordLink as kkwl)
        .on(kkw.id, kkwl.keywordId)
        .where.eq(kkwl.knowledgeId, knowledgePlug.id)
    }.map(KnowledgeKeyWordEntity(kkw.resultName)).collection.apply()
  }

  /**
   * Удаление связей знания и ключевых слов из таблицы knowledge_keyword_link
   *
   * @param knowledge умение, связи с котором будут удалены
   * @param session   имплисит, позволяющий вызывать метод внутри сессии
   */
  override def deleteKeyWords(knowledge: KnowledgeEntity)
                             (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(KnowledgeKeyWordLink)
        .where.eq(kkwlC.knowledgeId, knowledge.id)
    }.update.apply()
  }

  /**
   * Вставка знания в БД
   *
   * @param entity Знание которое необходимо вставить в таблицу
   */
  override def insert(entity: KnowledgeEntity)
                     (implicit session: DBSession): Unit = {
    withSQL {
      insertInto(KnowledgePlug)
        .namedValues(
          kC.id -> entity.id,
          kC.name -> entity.name
        )
    }.update.apply()

    insertKeyWord(entity)
  }

  /**
   * Вставка сразу нескольких Knowledge в БД
   *
   * @param knowledge список knowledge которые мы хотим вставить
   */
  override def insertMultiRows(knowledge: Seq[KnowledgeEntity])
                              (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = knowledge.map(know => Seq(know.id, know.name))

    withSQL {
      insertInto(KnowledgePlug)
        .namedValues(
          kC.id -> sqls.?,
          kC.name -> sqls.?
        )
    }.batch(batchParams: _*).apply()

    knowledge.foreach(insertKeyWord)
  }

  /**
   * Поулчение Знания по id
   *
   * @param id     Знание которое необходимо получить
   * @return Optional с Знанием
   */
  override def findById(id: UUID)
                       (implicit session: DBSession): Option[KnowledgeEntity] = {
    val knowledgePlug: Option[KnowledgePlug] =
      withSQL {
        select.from(KnowledgePlug as k)
          .where.eq(k.id, id)
      }.map(KnowledgePlug(k.resultName)).single.apply()

    knowledgePlug.map(plug => KnowledgeEntity(
      id = plug.id,
      name = plug.name,
      keyWords = KnowledgeEntity.selectKeyWords(plug))
    )
  }

  /**
   * Получение указанного кол-ва знаний
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @return последовательность всех Знаний из таблицы
   */
  override def findAll(limit: Int = 100,
                       offset: Int = 0,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value)
                      (implicit session: DBSession): Seq[KnowledgeEntity] = {
    val knowledgePlugs: Seq[KnowledgePlug] =
      withSQL {
        select.all(k).from(KnowledgePlug as k)
          .orderBy(orderBy).append(sort)
          .limit(limit)
          .offset(offset)
      }.map(KnowledgePlug(k.resultName)).collection.apply()

    knowledgePlugs.map(plug => KnowledgeEntity(
      id = plug.id,
      name = plug.name,
      keyWords = KnowledgeEntity.selectKeyWords(plug))
    )
  }

  /**
   * Обновление записи о Знание
   *
   * @param entity Entity которое будет обновлено
   */
  override def update(entity: KnowledgeEntity)
                     (implicit session: DBSession): Unit = {
    withSQL {
      QueryDSL.update(KnowledgePlug)
        .set(
          kC.name -> entity.name
        ).where.eq(kC.id, entity.id)
    }.update.apply()

    deleteKeyWords(entity)
    insertKeyWord(entity)
  }

  /**
   * Удаление знания по его ID
   *
   * @param id     Entity которую необходимо удалить
   */
  override def deleteById(id: UUID)
                         (implicit session: DBSession): Unit =
    withSQL {
      deleteFrom(KnowledgePlug)
        .where.eq(kC.id, id)
    }.update.apply()
}
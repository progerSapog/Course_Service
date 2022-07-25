package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.KnowledgeKeyWordEntity.kkw
import Databases.Models.Dao.LinkTables.KnowledgeKeyWordLink
import Databases.Models.Dao.LinkTables.KnowledgeKeyWordLink.{kkwl, kkwlC}
import Databases.Models.Dao.Plugs.KnowledgePlug
import Databases.Models.Dao.Plugs.KnowledgePlug.{k, kC}
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
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  override def insertKeyWord(kasEntity: KnowledgeEntity)
                            (implicit dbSession: DBSession): Unit = {
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
   * @param dbSession     имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные ключевые слова
   */
  override def selectKeyWords(knowledgePlug: KnowledgePlug)
                             (implicit dbSession: DBSession): Seq[KnowledgeKeyWordEntity] = {
    withSQL {
      selectFrom(KnowledgeKeyWordEntity as kkw)
        .leftJoin(KnowledgeKeyWordLink as kkwl)
        .on(kkw.id, kkwl.knowledgeId)
        .where.eq(kkwl.knowledgeId, knowledgePlug.id)
    }.map(KnowledgeKeyWordEntity(kkw.resultName)).collection.apply()
  }

  /**
   * Удаление связей знания и ключевых слов из таблицы knowledge_keyword_link
   *
   * @param knowledge умение, связи с котором будут удалены
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  override def deleteKeyWords(knowledge: KnowledgeEntity)
                             (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(KnowledgeKeyWordLink)
        .where.eq(kkwlC.knowledgeId, knowledge.id)
    }.update.apply()
  }

  /**
   * Вставка знания в БД
   *
   * @param entity Знание которое необходимо вставить в таблицу
   * @param dbName - имя БД с которой мы хотим работать
   */
  override def insert(entity: KnowledgeEntity, dbName: String = defaultDBName): Unit = {
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(KnowledgePlug)
          .namedValues(
            kC.id -> entity.id,
            kC.name -> entity.name
          )
      }.update.apply()

      insertKeyWord(entity)
    }
  }

  /**
   * Вставка сразу нескольких Knowledge в БД
   *
   * @param knowledge список knowledge которые мы хотим вставить
   * @param dbName    имя БД с которой мы хотим работать
   */
  override def insertMultiRows(knowledge: Seq[KnowledgeEntity], dbName: String = defaultDBName): Unit = {
    val batchParams: Seq[Seq[Any]] = knowledge.map(know => Seq(know.id, know.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(KnowledgePlug)
          .namedValues(
            kC.id -> sqls.?,
            kC.name -> sqls.?
          )
      }.batch(batchParams: _*).apply()

      knowledge.foreach(insertKeyWord)
    }
  }

  /**
   * Поулчение Знания по id
   *
   * @param id     Знание которое необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Знанием
   */
  override def findById(id: UUID, dbName: String = defaultDBName): Option[KnowledgeEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
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
  }

  /**
   * Получение указанного кол-ва знаний
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Знаний из таблицы
   */
  override def findAll(limit: Int = 100,
              offset: Int = 0,
              orderBy: SQLSyntax = Id.value,
              sort: SQLSyntax = ASC.value,
              dbName: String = defaultDBName): Seq[KnowledgeEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
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
  }

  /**
   * Обновление записи о Знание
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  override def update(entity: KnowledgeEntity, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
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
   * @param dbName имя БД с которой мы хотим работать
   */
  override def deleteById(id: UUID, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        deleteFrom(KnowledgePlug)
          .where.eq(kC.id, id)
      }.update.apply()
    }
}
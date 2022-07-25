package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.LinkTables.SkillKeyWordLink
import Databases.Models.Dao.LinkTables.SkillKeyWordLink.{skwl, skwlC}
import Databases.Models.Dao.Plugs.SkillPlug
import Databases.Models.Dao.Plugs.SkillPlug.{s, sC}
import Databases.Models.Dao.SkillKeyWordEntity.skw
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
   * @param skill     навык для которого ищутся ключевые слова
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  override def insertKeyWord(skill: SkillEntity)
                            (implicit dbSession: DBSession): Unit = {
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
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные ключевые слова
   */
  override def selectKeyWords(skillPlug: SkillPlug)
                             (implicit dbSession: DBSession): Seq[SkillKeyWordEntity] = {
    withSQL {
      selectFrom(SkillKeyWordEntity as skw)
        .leftJoin(SkillKeyWordLink as skwl)
        .on(skw.id, skwl.skillId)
        .where.eq(skwl.skillId, skillPlug.id)
    }.map(SkillKeyWordEntity(skw.resultName)).collection.apply()
  }

  /**
   * Удаление связей навыка и ключевых слов из таблицы skill_keyword_link
   *
   * @param skill     навык, связи с котором будут удалены
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  override def deleteKeyWords(skill: SkillEntity)
                             (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(SkillKeyWordLink)
        .where.eq(skwlC.skillId, skill.id)
    }.update.apply()
  }

  /**
   * Вставка навыка в БД
   *
   * @param entity Навык который необходимо вставить в таблицу
   * @param dbName - имя БД с которой мы хотим работать
   */
  override def insert(entity: SkillEntity, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
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
   * @param dbName имя БД с которой мы хотим работать
   */
  override def insertMultiRows(skills: Seq[SkillEntity], dbName: String = defaultDBName): Unit = {
    val batchParams: Seq[Seq[Any]] = skills.map(skill => Seq(skill.id, skill.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(SkillPlug)
          .namedValues(
            sC.id -> sqls.?,
            sC.name -> sqls.?
          )
      }.batch(batchParams: _*).apply()

      skills.foreach(insertKeyWord)
    }
  }

  /**
   * Получение Навыка по id
   *
   * @param id     Навык который необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Навыком
   */
  override def findById(id: UUID, dbName: String = defaultDBName): Option[SkillEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val skillPlug: Option[SkillPlug] =
        withSQL {
          select.from(SkillPlug as s)
            .where.eq(s.id, id)
        }.map(SkillPlug(s.resultName)).single.apply()

      skillPlug.map(plug => SkillEntity(
        id = plug.id,
        name = plug.name,
        keyWords = SkillEntity.selectKeyWords(plug))
      )
    }
  }

  /**
   * Получение указаного кол-ва навыков
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Entity из таблицы
   */
  override def findAll(limit: Int = 100,
              offset: Int = 0,
              orderBy: SQLSyntax = Id.value,
              sort: SQLSyntax = ASC.value,
              dbName: String = defaultDBName): Seq[SkillEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
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
        keyWords = SkillEntity.selectKeyWords(plug))
      )
    }
  }

  /**
   * Обновление записи о Навыке
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  override def update(entity: SkillEntity, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
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
   * @param id     Entity которую необходимо удалить
   * @param dbName имя БД с которой мы хотим работать
   */
  override def deleteById(id: UUID, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        deleteFrom(SkillPlug)
          .where.eq(sC.id, id)
      }.update.apply()
    }
}
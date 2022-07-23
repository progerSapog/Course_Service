package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.KnowledgeKeyWordEntity.kkw
import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы knowledge - знание
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
 * Позвоялет работать с данным отображением при помощи type safe DSL
 *
 * @see IKnowledgeDao
 * @see UUIDFactory
 */
object KnowledgeEntity extends SQLSyntaxSupport[KnowledgeEntity] with IKnowledgeDao {

  import Databases.Models.Dao.KnowledgeEntity.KnowledgeKeyWordLink.{kkwl, kkwlC}
  import Databases.Models.Dao.KnowledgeEntity.KnowledgePlug.kp

  /**
   * Класс промежуточного представления Знания, содержит только id и name
   *
   * @param id   столбец id (UUID)
   * @param name столбец name (VARCHAR(255))
   */
  private case class KnowledgePlug(id: UUID, name: String) extends IPlug

  private object KnowledgePlug extends SQLSyntaxSupport[KnowledgePlug] {
    val kp: QuerySQLSyntaxProvider[SQLSyntaxSupport[KnowledgePlug], KnowledgePlug] = KnowledgePlug.syntax("kp")
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "knowledge"

    def apply(r: ResultName[KnowledgePlug])(rs: WrappedResultSet): KnowledgePlug =
      new KnowledgePlug(
        id = UUID.fromString(rs.get(r.id)),
        name = rs.string(r.name)
      )
  }

  /**
   * Маппер из KnowledgePlug в KnowledgeEntity
   */
  private object KnowledgePlugMapper {
    /**
     * Перевод из KnowledgePlug в KnowledgeEntity.
     * Дополняет данные из KnowledgePlug связанными с знанием ключевыми словами
     *
     * @param plug      который будет переведен
     * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
     * @return полученный CourseEntity
     */
    def plug2Entity(plug: KnowledgePlug)(implicit dbSession: DBSession): KnowledgeEntity = {
      KnowledgeEntity(
        id = plug.id,
        name = plug.name,
        keyWords = selectKeyWords(plug)
      )
    }
  }

  /**
   * Представление таблицы связи knowledge_keyword_link
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param knowledgeId id знания
   * @param keywordID   id ключевого слова
   */
  private case class KnowledgeKeyWordLink(knowledgeId: UUID, keywordID: UUID)

  private object KnowledgeKeyWordLink extends SQLSyntaxSupport[KnowledgeKeyWordLink] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "knowledge_keyword_link"
    val kkwl: QuerySQLSyntaxProvider[SQLSyntaxSupport[KnowledgeKeyWordLink], KnowledgeKeyWordLink] = KnowledgeKeyWordLink.syntax("k_kw_l")
    val kkwlC: ColumnName[KnowledgeKeyWordLink] = KnowledgeKeyWordLink.column
  }

  override val schemaName: Some[String] = Some("courses")
  override val tableName = "knowledge"
  var defaultDBName = "default"

  val k: QuerySQLSyntaxProvider[SQLSyntaxSupport[KnowledgeEntity], KnowledgeEntity] = KnowledgeEntity.syntax("k")
  val kC: ColumnName[KnowledgeEntity] = KnowledgeEntity.column

  /**
   * Вставка связей знаний и ключевых слов в таблицу knowledge_keyword_link
   *
   * @param knowledge знание для которого ищутся ключевые слова
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertKeyWord(knowledge: KnowledgeEntity)
                           (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = knowledge.keyWords.map(word => Seq(knowledge.id, word.id))

    withSQL {
      insertInto(KnowledgeKeyWordLink)
        .namedValues(
          kkwlC.knowledgeId -> sqls.?,
          kkwlC.keywordId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Выборка ключевых слов знания через knowledge_keyword_link
   *
   * @param knowledgePlug знание для которого ищутся ключевые слова
   * @param dbSession   имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные навыки
   */
  private def selectKeyWords(knowledgePlug: KnowledgePlug)
                            (implicit dbSession: DBSession): Seq[KnowledgeKeyWordEntity] = {
    withSQL {
      selectFrom(KnowledgeKeyWordEntity as kkw)
        .leftJoin(KnowledgeKeyWordLink as kkwl)
        .on(kkw.id, kkwl.abilityId)
        .where.eq(kkwl.abilityId, knowledgePlug.id)
    }.map(KnowledgeKeyWordEntity(kkw.resultName)).collection.apply()
  }

  /**
   * Удаление связей знания и ключевых слов из таблицы knowledge_keyword_link
   *
   * @param knowledge   умение, связи с котором будут удалены
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteKeyWords(knowledge: KnowledgeEntity)
                            (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(KnowledgeKeyWordLink)
        .where.eq(kkwlC.courseId, knowledge.id)
    }.update.apply()
  }

  /**
   * Вставка знания в БД
   *
   * @param entity Entity которую необходимо вставить в таблицу
   * @param dbName - имя БД с которой мы хотим работать
   */
  def insert(entity: KnowledgeEntity, dbName: String = defaultDBName): Unit = {
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(KnowledgeEntity)
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
  override def insertMultiRows(knowledge: Seq[KnowledgeEntity], dbName: String): Unit = {
    val batchParams: Seq[Seq[Any]] = knowledge.map(know => Seq(know.id, know.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(KnowledgeEntity)
          .namedValues(
            kC.id -> sqls.?,
            kC.name -> sqls.?
          )
      }.batch(batchParams: _*).apply()

      knowledge.foreach(insertKeyWord)
    }
  }

  /**
   * Поулчение знания по id
   *
   * @param id     Entity которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  def findById(id: UUID, dbName: String = defaultDBName): Option[KnowledgeEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val knowledgePlug: Option[KnowledgePlug] =
        withSQL {
          select.from(KnowledgePlug as kp)
            .where.eq(kp.id, id)
        }.map(KnowledgePlug(kp.resultName)).single.apply()

      knowledgePlug.map(KnowledgePlugMapper.plug2Entity)
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
   * @return последовательность всех Entity из таблицы
   */
  def findAll(limit: Int = 100,
              offset: Int = 0,
              orderBy: SQLSyntax = Id.value,
              sort: SQLSyntax = ASC.value,
              dbName: String = defaultDBName): Seq[KnowledgeEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val knowledgePlugs: Seq[KnowledgePlug] =
        withSQL {
          select.all(k).from(KnowledgePlug as kp)
            .orderBy(orderBy).append(sort)
            .limit(limit)
            .offset(offset)
        }.map(KnowledgePlug(kp.resultName)).collection.apply()

      knowledgePlugs.map(KnowledgePlugMapper.plug2Entity)
    }
  }

  /**
   * Обновление записи о знании
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  def update(entity: KnowledgeEntity, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        QueryDSL.update(KnowledgeEntity)
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
  def deleteById(id: UUID, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        deleteFrom(KnowledgeEntity)
          .where.eq(kC.id, id)
      }.update.apply()
    }
}
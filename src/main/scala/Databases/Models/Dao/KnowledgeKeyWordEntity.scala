package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы knowledge_keyword - ключевые слова знаний
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 * @see IKASEntity
 */
case class KnowledgeKeyWordEntity(id: UUID, name: String) extends IKeyWordEntity

/**
 * Объект компаньон, выполняющий роль DAO.
 * Реализует стандартные CRUD операции.
 * Позвоялет работать с данным отображением при помощи type safe DSL
 *
 * @see IKnowledgeKeyWordDao
 * @see UUIDFactory
 */
object KnowledgeKeyWordEntity extends SQLSyntaxSupport[KnowledgeKeyWordEntity] with IKnowledgeKeyWordDao {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "knowledge_keyword"
  var defaultDBName = "default"

  val kkw: QuerySQLSyntaxProvider[SQLSyntaxSupport[KnowledgeKeyWordEntity], KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.syntax("kkw")
  val kkwc: ColumnName[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.column

  def apply(r: ResultName[KnowledgeKeyWordEntity])(rs: WrappedResultSet): KnowledgeKeyWordEntity =
    new KnowledgeKeyWordEntity(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )

  /**
   * Вставка новой Entity в таблицу
   *
   * @param entity Entity которую необходимо вставить в таблицу
   * @param dbName - имя БД с которой мы хотим работать
   */
  override def insert(entity: KnowledgeKeyWordEntity, dbName: String): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(KnowledgeKeyWordEntity)
          .namedValues(
            kkwc.id -> entity.id,
            kkwc.name -> entity.name
          )
      }.update.apply()
    }

  /**
   * Вставка сразу нескольких KAS в БД
   *
   * @param keyWords список KAS которые мы хотим вставить
   * @param dbName   имя БД с которой мы хотим работать
   */
  override def insertMultiRows(keyWords: Seq[KnowledgeKeyWordEntity], dbName: String): Unit = {
    val batchParams: Seq[Seq[Any]] = keyWords.map(word => Seq(word.id, word.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(KnowledgeKeyWordEntity)
          .namedValues(
            kkwc.id -> sqls.?,
            kkwc.name -> sqls.?
          )
      }.batch(batchParams: _*).apply()
    }
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
  override def findAll(limit: Int,
                       offset: Int,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value,
                       dbName: String): Seq[KnowledgeKeyWordEntity] =
    NamedDB(s"$dbName") readOnly { implicit session =>
      withSQL {
        select.all(kkw).from(KnowledgeKeyWordEntity as kkw)
          .orderBy(orderBy).append(sort)
          .limit(limit)
          .offset(offset)
      }.map(KnowledgeKeyWordEntity(kkw.resultName)).list.apply()
    }

  /**
   * Получение Entity из таблицы по id
   *
   * @param id     Entity которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID, dbName: String): Option[KnowledgeKeyWordEntity] =
    NamedDB(s"$dbName") readOnly { implicit session =>
      withSQL {
        select.from(KnowledgeKeyWordEntity as kkw)
          .where.eq(kkw.id, id)
      }.map(KnowledgeKeyWordEntity(kkw.resultName)).single.apply()
    }

  /**
   * Обновление Entity в таблице
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  override def update(entity: KnowledgeKeyWordEntity, dbName: String): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        QueryDSL.update(KnowledgeKeyWordEntity)
          .set(
            kkwc.name -> entity.name
          ).where.eq(kkwc.id, entity.id)
      }.update.apply()
    }

  /**
   * Удаление Entity из таблицы по id
   *
   * @param id     Entity которую необходимо удалить
   * @param dbName имя БД с которой мы хотим работать
   */
  override def deleteById(id: UUID, dbName: String): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        deleteFrom(KnowledgeKeyWordEntity)
          .where.eq(kkwc.id, id)
      }.update.apply()
    }
}
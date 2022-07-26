package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import scalikejdbc._

import java.util.UUID

/**
 * Сущность Ключевое слово знания (knowledge_keyword)
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

  val kkw: QuerySQLSyntaxProvider[SQLSyntaxSupport[KnowledgeKeyWordEntity], KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.syntax("kkw")
  val kkwc: ColumnName[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.column

  def apply(r: ResultName[KnowledgeKeyWordEntity])(rs: WrappedResultSet): KnowledgeKeyWordEntity =
    new KnowledgeKeyWordEntity(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )

  /**
   * Вставка нового Ключевого слова в таблицу
   *
   * @param entity Entity которую необходимо вставить в таблицу
   */
  override def insert(entity: KnowledgeKeyWordEntity)
                     (implicit session: DBSession): Unit =
    withSQL {
      insertInto(KnowledgeKeyWordEntity)
        .namedValues(
          kkwc.id -> entity.id,
          kkwc.name -> entity.name
        )
    }.update.apply()

  /**
   * Вставка сразу нескольких Ключевых слов в БД
   *
   * @param keyWords список KAS которые мы хотим вставить
   */
  override def insertMultiRows(keyWords: Seq[KnowledgeKeyWordEntity])
                              (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = keyWords.map(word => Seq(word.id, word.name))

    withSQL {
      insertInto(KnowledgeKeyWordEntity)
        .namedValues(
          kkwc.id -> sqls.?,
          kkwc.name -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Получение всех Ключевых слов из таблицы
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @return последовательность всех Entity из таблицы
   */
  override def findAll(limit: Int,
                       offset: Int,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value)
                      (implicit session: DBSession): Seq[KnowledgeKeyWordEntity] =
    withSQL {
      select.all(kkw).from(KnowledgeKeyWordEntity as kkw)
        .orderBy(orderBy).append(sort)
        .limit(limit)
        .offset(offset)
    }.map(KnowledgeKeyWordEntity(kkw.resultName)).list.apply()


  /**
   * Получение Ключевого слова из таблицы по id
   *
   * @param id Entity которую необходимо получить
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID)
                       (implicit session: DBSession): Option[KnowledgeKeyWordEntity] =
    withSQL {
      select.from(KnowledgeKeyWordEntity as kkw)
        .where.eq(kkw.id, id)
    }.map(KnowledgeKeyWordEntity(kkw.resultName)).single.apply()


  /**
   * Обновление Ключевого слова в таблице
   *
   * @param entity Entity которое будет обновлено
   */
  override def update(entity: KnowledgeKeyWordEntity)
                     (implicit session: DBSession): Unit =
    withSQL {
      QueryDSL.update(KnowledgeKeyWordEntity)
        .set(
          kkwc.name -> entity.name
        ).where.eq(kkwc.id, entity.id)
    }.update.apply()


  /**
   * Удаление Ключевого слова из таблицы по id
   *
   * @param id Entity которую необходимо удалить
   */
  override def deleteById(id: UUID)
                         (implicit session: DBSession): Unit =
    withSQL {
      deleteFrom(KnowledgeKeyWordEntity)
        .where.eq(kkwc.id, id)
    }.update.apply()
}
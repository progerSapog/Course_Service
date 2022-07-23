package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы ability_keyword - ключевые слова умений
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 * @see IKASEntity
 */
case class AbilityKeyWordEntity(id: UUID, name: String) extends IKeyWordEntity

/**
 * Объект компаньон, выполняющий роль DAO.
 * Реализует стандартные CRUD операции.
 * Позвоялет работать с данным отображением при помощи type safe DSL
 *
 * @see IAbilityKeyWordDao
 * @see UUIDFactory
 */
object AbilityKeyWordEntity extends SQLSyntaxSupport[AbilityKeyWordEntity] with IAbilityKeyWordDao {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "ability_keyword"
  var defaultDBName = "default"

  val akw: QuerySQLSyntaxProvider[SQLSyntaxSupport[AbilityKeyWordEntity], AbilityKeyWordEntity] = AbilityKeyWordEntity.syntax("akw")
  val akwC: ColumnName[AbilityKeyWordEntity] = AbilityKeyWordEntity.column

  def apply(r: ResultName[AbilityKeyWordEntity])(rs: WrappedResultSet): AbilityKeyWordEntity =
    new AbilityKeyWordEntity(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )

  /**
   * Вставка новой Entity в таблицу
   *
   * @param entity Entity которую необходимо вставить в таблицу
   * @param dbName - имя БД с которой мы хотим работать
   */
  override def insert(entity: AbilityKeyWordEntity, dbName: String): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(AbilityKeyWordEntity)
          .namedValues(
            akwC.id -> entity.id,
            akwC.name -> entity.name
          )
      }.update.apply()
    }

  /**
   * Вставка сразу нескольких KAS в БД
   *
   * @param keyWords список KAS которые мы хотим вставить
   * @param dbName   имя БД с которой мы хотим работать
   */
  override def insertMultiRows(keyWords: Seq[AbilityKeyWordEntity], dbName: String): Unit = {
    val batchParams: Seq[Seq[Any]] = keyWords.map(word => Seq(word.id, word.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(AbilityKeyWordEntity)
          .namedValues(
            akwC.id -> sqls.?,
            akwC.name -> sqls.?
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
                       dbName: String): Seq[AbilityKeyWordEntity] =
    NamedDB(s"$dbName") readOnly { implicit session =>
      withSQL {
        select.all(akw).from(AbilityKeyWordEntity as akw)
          .orderBy(orderBy).append(sort)
          .limit(limit)
          .offset(offset)
      }.map(AbilityKeyWordEntity(akw.resultName)).list.apply()
    }

  /**
   * Получение Entity из таблицы по id
   *
   * @param id     Entity которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID, dbName: String): Option[AbilityKeyWordEntity] =
    NamedDB(s"$dbName") readOnly { implicit session =>
      withSQL {
        select.from(AbilityKeyWordEntity as akw)
          .where.eq(akw.id, id)
      }.map(AbilityKeyWordEntity(akw.resultName)).single.apply()
    }

  /**
   * Обновление Entity в таблице
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  override def update(entity: AbilityKeyWordEntity, dbName: String): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        QueryDSL.update(AbilityKeyWordEntity)
          .set(
            akwC.name -> entity.name
          ).where.eq(akwC.id, entity.id)
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
        deleteFrom(AbilityKeyWordEntity)
          .where.eq(akwC.id, id)
      }.update.apply()
    }
}
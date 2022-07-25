package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import scalikejdbc._

import java.util.UUID

/**
 * Сущность Ключевое слово умения (ability_keyword)
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

  val akw: QuerySQLSyntaxProvider[SQLSyntaxSupport[AbilityKeyWordEntity], AbilityKeyWordEntity] = AbilityKeyWordEntity.syntax("akw")
  val akwC: ColumnName[AbilityKeyWordEntity] = AbilityKeyWordEntity.column

  def apply(r: ResultName[AbilityKeyWordEntity])(rs: WrappedResultSet): AbilityKeyWordEntity =
    new AbilityKeyWordEntity(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )

  /**
   * Вставка нового Ключевого слова в таблицу
   *
   * @param entity ключевое слово которое необходимо вставить в таблицу
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
   * Вставка сразу нескольких Ключевых слов в БД
   *
   * @param keyWords список Ключевых слов которые необходимо вставить
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
   * Получение всех Ключевых слова из таблицы
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Ключевых слов из таблицы
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
   * Получение Ключевого слова из таблицы по id
   *
   * @param id     Ключевого слова которое необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Ключевым словом если такая есть в БД.
   */
  override def findById(id: UUID, dbName: String): Option[AbilityKeyWordEntity] =
    NamedDB(s"$dbName") readOnly { implicit session =>
      withSQL {
        select.from(AbilityKeyWordEntity as akw)
          .where.eq(akw.id, id)
      }.map(AbilityKeyWordEntity(akw.resultName)).single.apply()
    }

  /**
   * Обновление Ключевого слова в таблице
   *
   * @param entity Ключевое слово которое будет обновлено
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
   * Удаление Ключевого слова из таблицы по id
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
package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}

import java.util.UUID
import scalikejdbc._

/**
 * Отображение таблицы skill_keyword - ключевые слова навыков
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 * @see IKASEntity
 */
case class SkillKeyWordEntity(id: UUID, name: String) extends IKeyWordEntity

/**
 * Объект компаньон, выполняющий роль DAO.
 * Реализует стандартные CRUD операции.
 * Позвоялет работать с данным отображением при помощи type safe DSL
 *
 * @see ISkillKeyWordDao
 * @see UUIDFactory
 */
object SkillKeyWordEntity extends SQLSyntaxSupport[SkillKeyWordEntity] with ISkillKeyWordDao {
  override val schemaName: Some[String] = Some("courses")
  override val tableName = "skill_keyword"
  var defaultDBName = "default"

  val skw: QuerySQLSyntaxProvider[SQLSyntaxSupport[SkillKeyWordEntity], SkillKeyWordEntity] = SkillKeyWordEntity.syntax("skw")
  val skwc: ColumnName[SkillKeyWordEntity] = SkillKeyWordEntity.column

  def apply(r: ResultName[SkillKeyWordEntity])(rs: WrappedResultSet): SkillKeyWordEntity =
    new SkillKeyWordEntity(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )

  /**
   * Вставка новой Entity в таблицу
   *
   * @param entity Entity которую необходимо вставить в таблицу
   * @param dbName - имя БД с которой мы хотим работать
   */
  override def insert(entity: SkillKeyWordEntity, dbName: String): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(SkillKeyWordEntity)
          .namedValues(
            skwc.id -> entity.id,
            skwc.name -> entity.name
          )
      }.update.apply()
    }

  /**
   * Вставка сразу нескольких KAS в БД
   *
   * @param keyWords список KAS которые мы хотим вставить
   * @param dbName     имя БД с которой мы хотим работать
   */
  override def insertMultiRows(keyWords: Seq[SkillKeyWordEntity], dbName: String): Unit = {
    val batchParams: Seq[Seq[Any]] = keyWords.map(word => Seq(word.id, word.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(SkillKeyWordEntity)
          .namedValues(
            skwc.id -> sqls.?,
            skwc.name -> sqls.?
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
                       dbName: String): Seq[SkillKeyWordEntity] =
    NamedDB(s"$dbName") readOnly { implicit session =>
      withSQL {
        select.all(skw).from(SkillKeyWordEntity as skw)
          .orderBy(orderBy).append(sort)
          .limit(limit)
          .offset(offset)
      }.map(SkillKeyWordEntity(skw.resultName)).list.apply()
    }

  /**
   * Получение Entity из таблицы по id
   *
   * @param id     Entity которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID, dbName: String): Option[SkillKeyWordEntity] =
    NamedDB(s"$dbName") readOnly { implicit session =>
      withSQL {
        select.from(SkillKeyWordEntity as skw)
          .where.eq(skw.id, id)
      }.map(SkillKeyWordEntity(skw.resultName)).single.apply()
    }

  /**
   * Обновление Entity в таблице
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  override def update(entity: SkillKeyWordEntity, dbName: String): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        QueryDSL.update(SkillKeyWordEntity)
          .set(
            skwc.name -> entity.name
          ).where.eq(skwc.id, entity.id)
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
        deleteFrom(SkillKeyWordEntity)
          .where.eq(skwc.id, id)
      }.update.apply()
    }
}
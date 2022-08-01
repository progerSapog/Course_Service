package Databases.Models.Dao.Keywords

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.{IKeyWordEntity, ISkillKeyWordDao}
import scalikejdbc._

import java.util.UUID

/**
 * Сущность Ключевое слово навыка (skill_keyword)
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

  val skw: QuerySQLSyntaxProvider[SQLSyntaxSupport[SkillKeyWordEntity], SkillKeyWordEntity] = SkillKeyWordEntity.syntax("skw")
  val skwc: ColumnName[SkillKeyWordEntity] = SkillKeyWordEntity.column

  def apply(r: ResultName[SkillKeyWordEntity])(rs: WrappedResultSet): SkillKeyWordEntity =
    new SkillKeyWordEntity(
      id = UUID.fromString(rs.get(r.id)),
      name = rs.string(r.name)
    )

  /**
   * Вставка новой Ключевого слова в таблицу
   *
   * @param entity Entity которую необходимо вставить в таблицу
   */
  override def insert(entity: SkillKeyWordEntity)
                     (implicit session: DBSession): Unit =
    withSQL {
      insertInto(SkillKeyWordEntity)
        .namedValues(
          skwc.id -> entity.id,
          skwc.name -> entity.name
        )
    }.update.apply()

  /**
   * Вставка сразу нескольких Ключевых слов в БД
   *
   * @param keyWords список Ключевых слов которые мы хотим вставить
   */
  override def insertMultiRows(keyWords: Seq[SkillKeyWordEntity])
                              (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = keyWords.map(word => Seq(word.id, word.name))

    withSQL {
      insertInto(SkillKeyWordEntity)
        .namedValues(
          skwc.id -> sqls.?,
          skwc.name -> sqls.?
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
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Entity из таблицы
   */
  override def findAll(limit: Int,
                       offset: Int,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value)
                      (implicit session: DBSession): Seq[SkillKeyWordEntity] =
    withSQL {
      select.all(skw).from(SkillKeyWordEntity as skw)
        .orderBy(orderBy).append(sort)
        .limit(limit)
        .offset(offset)
    }.map(SkillKeyWordEntity(skw.resultName)).list.apply()

  /**
   * Получение Ключевого слова из таблицы по id
   *
   * @param id Entity которую необходимо получить
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID)
                       (implicit session: DBSession): Option[SkillKeyWordEntity] =
    withSQL {
      select.from(SkillKeyWordEntity as skw)
        .where.eq(skw.id, id)
    }.map(SkillKeyWordEntity(skw.resultName)).single.apply()

  /**
   * Обновление Ключевого слова в таблице
   *
   * @param entity Entity которое будет обновлено
   */
  override def update(entity: SkillKeyWordEntity)
                     (implicit session: DBSession): Unit =
    withSQL {
      QueryDSL.update(SkillKeyWordEntity)
        .set(
          skwc.name -> entity.name
        ).where.eq(skwc.id, entity.id)
    }.update.apply()

  /**
   * Удаление Ключевого слова из таблицы по id
   *
   * @param id Entity которую необходимо удалить
   */
  override def deleteById(id: UUID)
                         (implicit session: DBSession): Unit =
    withSQL {
      deleteFrom(SkillKeyWordEntity)
        .where.eq(skwc.id, id)
    }.update.apply()
}
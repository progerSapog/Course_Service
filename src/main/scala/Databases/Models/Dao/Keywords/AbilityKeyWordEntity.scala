package Databases.Models.Dao.Keywords

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.{IAbilityKeyWordDao, IKeyWordEntity}
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
   */
  override def insert(entity: AbilityKeyWordEntity)
                     (implicit session: DBSession): Unit =
    withSQL {
      insertInto(AbilityKeyWordEntity)
        .namedValues(
          akwC.id -> entity.id,
          akwC.name -> entity.name
        )
    }.update.apply()


  /**
   * Вставка сразу нескольких Ключевых слов в БД
   *
   * @param keyWords список Ключевых слов которые необходимо вставить
   */
  override def insertMultiRows(keyWords: Seq[AbilityKeyWordEntity])
                              (implicit session: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = keyWords.map(word => Seq(word.id, word.name))

    withSQL {
      insertInto(AbilityKeyWordEntity)
        .namedValues(
          akwC.id -> sqls.?,
          akwC.name -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Получение всех Ключевых слова из таблицы
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @return последовательность всех Ключевых слов из таблицы
   */
  override def findAll(limit: Int,
                       offset: Int,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value)
                      (implicit session: DBSession): Seq[AbilityKeyWordEntity] =
    withSQL {
      select.all(akw).from(AbilityKeyWordEntity as akw)
        .orderBy(orderBy).append(sort)
        .limit(limit)
        .offset(offset)
    }.map(AbilityKeyWordEntity(akw.resultName)).list.apply()

  /**
   * Получение Ключевого слова из таблицы по id
   *
   * @param id Ключевого слова которое необходимо получить
   * @return Optional с Ключевым словом если такая есть в БД.
   */
  override def findById(id: UUID)
                       (implicit session: DBSession): Option[AbilityKeyWordEntity] =
    withSQL {
      select.from(AbilityKeyWordEntity as akw)
        .where.eq(akw.id, id)
    }.map(AbilityKeyWordEntity(akw.resultName)).single.apply()


  /**
   * Обновление Ключевого слова в таблице
   *
   * @param entity Ключевое слово которое будет обновлено
   */
  override def update(entity: AbilityKeyWordEntity)
                     (implicit session: DBSession): Unit =
    withSQL {
      QueryDSL.update(AbilityKeyWordEntity)
        .set(
          akwC.name -> entity.name
        ).where.eq(akwC.id, entity.id)
    }.update.apply()


  /**
   * Удаление Ключевого слова из таблицы по id
   *
   * @param id Entity которую необходимо удалить
   */
  override def deleteById(id: UUID)
                         (implicit session: DBSession): Unit =
    withSQL {
      deleteFrom(AbilityKeyWordEntity)
        .where.eq(akwC.id, id)
    }.update.apply()
}
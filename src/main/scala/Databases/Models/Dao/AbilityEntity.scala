package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.AbilityKeyWordEntity.akw
import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы ability - Умение
 *
 * @param id   столбец id (UUID)
 * @param name столбец name (VARCHAR(255))
 * @see IKASEntity
 */
case class AbilityEntity(id: UUID,
                         name: String,
                         keyWords: Seq[AbilityKeyWordEntity]) extends IKASEntity

/**
 * Объект компаньон, выполняющий роль DAO.
 * Реализует стандартные CRUD операции.
 * Позвоялет работать с данным отображением при помощи type safe DSL
 *
 * @see IAbilityDao
 * @see UUIDFactory
 */
object AbilityEntity extends SQLSyntaxSupport[AbilityEntity] with IAbilityDao {

  import Databases.Models.Dao.AbilityEntity.AbilityKeyWordLink.{akwl, akwlC}
  import Databases.Models.Dao.AbilityEntity.AbilityPlug.ap

  /**
   * Класс промежуточного представления Умения, содержит только id и name
   *
   * @param id   столбец id (UUID)
   * @param name столбец name (VARCHAR(255))
   */
  private case class AbilityPlug(id: UUID, name: String) extends IPlug

  private object AbilityPlug extends SQLSyntaxSupport[AbilityPlug] {
    val ap: QuerySQLSyntaxProvider[SQLSyntaxSupport[AbilityPlug], AbilityPlug] = AbilityPlug.syntax("ap")
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "ability"

    def apply(r: ResultName[AbilityPlug])(rs: WrappedResultSet): AbilityPlug =
      new AbilityPlug(
        id = UUID.fromString(rs.get(r.id)),
        name = rs.string(r.name)
      )
  }

  /**
   * Маппер из AbilityPlug в AbilityEntity
   */
  private object AbilityPlugMapper {
    /**
     * Перевод из AbilityPlug в AbilityEntity.
     * Дополняет данные из AbilityPlug связанными с умением ключевыми словами
     *
     * @param plug      который будет переведен
     * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
     * @return полученный CourseEntity
     */
    def plug2Entity(plug: AbilityPlug)(implicit dbSession: DBSession): AbilityEntity = {
      AbilityEntity(
        id = plug.id,
        name = plug.name,
        keyWords = selectKeyWords(plug)
      )
    }
  }

  /**
   * Представление таблицы связи ability_keyword_link
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param abilityId id умения
   * @param keywordId id ключевого слова
   */
  private case class AbilityKeyWordLink(abilityId: UUID, keywordId: UUID)

  private object AbilityKeyWordLink extends SQLSyntaxSupport[AbilityKeyWordLink] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "ability_keyword_link"
    val akwl: QuerySQLSyntaxProvider[SQLSyntaxSupport[AbilityKeyWordLink], AbilityKeyWordLink] = AbilityKeyWordLink.syntax("a_kw_l")
    val akwlC: ColumnName[AbilityKeyWordLink] = AbilityKeyWordLink.column
  }

  override val schemaName: Some[String] = Some("courses")
  override val tableName = "ability"
  var defaultDBName = "default"

  val a: QuerySQLSyntaxProvider[SQLSyntaxSupport[AbilityEntity], AbilityEntity] = AbilityEntity.syntax("a")
  val aC: ColumnName[AbilityEntity] = AbilityEntity.column

  /**
   * Вставка связей умения и ключевых слов в таблицу ability_keyword_link
   *
   * @param ability   умение для которого ищутся ключевые слова
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertKeyWord(ability: AbilityEntity)
                           (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = ability.keyWords.map(word => Seq(ability.id, word.id))

    withSQL {
      insertInto(AbilityKeyWordLink)
        .namedValues(
          akwlC.abilityId -> sqls.?,
          akwlC.keywordId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Выборка ключевых слов умения через ability_keyword_link
   *
   * @param abilityPlug умение для которого ищутся ключевые слова
   * @param dbSession   имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные навыки
   */
  private def selectKeyWords(abilityPlug: AbilityPlug)
                            (implicit dbSession: DBSession): Seq[AbilityKeyWordEntity] = {
    withSQL {
      selectFrom(AbilityKeyWordEntity as akw)
        .leftJoin(AbilityKeyWordLink as akwl)
        .on(akw.id, akwl.abilityId)
        .where.eq(akwl.abilityId, abilityPlug.id)
    }.map(AbilityKeyWordEntity(akw.resultName)).collection.apply()
  }

  /**
   * Удаление связей умения и ключевых слов из таблицы ability_keyword_link
   *
   * @param ability   умение, связи с котором будут удалены
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteKeyWords(ability: AbilityEntity)
                            (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(AbilityKeyWordLink)
        .where.eq(akwlC.courseId, ability.id)
    }.update.apply()
  }

  /**
   * Вставка умения в БД
   *
   * @param entity Entity которую необходимо вставить в таблицу
   * @param dbName имя БД с которой мы хотим работать
   */
  def insert(entity: AbilityEntity, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(AbilityEntity)
          .namedValues(
            aC.id -> entity.id,
            aC.name -> entity.name
          )
      }.update.apply()

      insertKeyWord(entity)
    }

  /**
   * Вставка сразу нескольких ability в БД
   *
   * @param abilities список abilities которые мы хотим вставить
   * @param dbName    имя БД с которой мы хотим работать
   */
  override def insertMultiRows(abilities: Seq[AbilityEntity], dbName: String): Unit = {
    val batchParams: Seq[Seq[Any]] = abilities.map(ability => Seq(ability.id, ability.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(AbilityEntity)
          .namedValues(
            aC.id -> sqls.?,
            aC.name -> sqls.?
          )
      }.batch(batchParams: _*).apply()

      abilities.foreach(insertKeyWord)
    }
  }

  /**
   * Получение умения по id
   *
   * @param id     Entity которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  def findById(id: UUID, dbName: String = defaultDBName): Option[AbilityEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val abilityPlug: Option[AbilityPlug] =
        withSQL {
          select.from(AbilityPlug as ap)
            .where.eq(ap.id, id)
        }.map(AbilityPlug(ap.resultName)).single.apply()

      abilityPlug.map(AbilityPlugMapper.plug2Entity)
    }
  }

  /**
   * Получение указанного кол-ва умений
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
              dbName: String = defaultDBName): Seq[AbilityEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val abilitiesPlugs: Seq[AbilityPlug] =
        withSQL {
          select.all(ap).from(AbilityPlug as ap)
            .orderBy(orderBy).append(sort)
            .limit(limit)
            .offset(offset)
        }.map(AbilityPlug(ap.resultName)).collection.apply()

      abilitiesPlugs.map(AbilityPlugMapper.plug2Entity)
    }
  }

  /**
   * Обновление записи о умении
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  def update(entity: AbilityEntity, dbName: String = defaultDBName): Unit = {
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        QueryDSL.update(AbilityEntity)
          .set(
            aC.name -> entity.name
          ).where.eq(aC.id, entity.id)
      }.update.apply()

      deleteKeyWords(entity)
      insertKeyWord(entity)
    }
  }

  /**
   * Удаление умения по его ID
   *
   * @param id     Entity которую необходимо удалить
   * @param dbName имя БД с которой мы хотим работать
   */
  def deleteById(id: UUID, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        deleteFrom(AbilityEntity)
          .where.eq(aC.id, id)
      }.update.apply()
    }
}
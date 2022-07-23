package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.SkillKeyWordEntity.skw
import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы skill - Навык
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
object SkillEntity extends SQLSyntaxSupport[SkillEntity] with ISkillDao {

  import Databases.Models.Dao.SkillEntity.SkillKeyWordLink.{skwl, skwlC}
  import Databases.Models.Dao.SkillEntity.SkillPlug.sp

  /**
   * Класс промежуточного представления Навыка, содержит только id и name
   *
   * @param id   столбец id (UUID)
   * @param name столбец name (VARCHAR(255))
   */
  private case class SkillPlug(id: UUID, name: String) extends IPlug

  private object SkillPlug extends SQLSyntaxSupport[SkillPlug] {
    val sp: QuerySQLSyntaxProvider[SQLSyntaxSupport[SkillPlug], SkillPlug] = SkillPlug.syntax("sp")
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "skill"

    def apply(r: ResultName[SkillPlug])(rs: WrappedResultSet): SkillPlug =
      new SkillPlug(
        id = UUID.fromString(rs.get(r.id)),
        name = rs.string(r.name)
      )
  }

  /**
   * Маппер из SkillPlug в SkillEntity
   */
  private object SkillPlugMapper {
    /**
     * Перевод из SkillPlug в SkillEntity.
     * Дополняет данные из SkillPlug связанными с навыком ключевыми словами
     *
     * @param plug      навык который будет переведен
     * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
     * @return полученный CourseEntity
     */
    def plug2Entity(plug: SkillPlug)
                   (implicit dbSession: DBSession): SkillEntity = {
      SkillEntity(
        id = plug.id,
        name = plug.name,
        keyWords = selectKeyWords(plug)
      )
    }
  }

  /**
   * Представление таблицы связи skill_keyword_link
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param skillId   id навыка
   * @param keywordId id ключевого слова
   */
  private case class SkillKeyWordLink(skillId: UUID, keywordId: UUID)

  private object SkillKeyWordLink extends SQLSyntaxSupport[SkillKeyWordLink] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "skill_keyword_link"
    val skwl: QuerySQLSyntaxProvider[SQLSyntaxSupport[SkillKeyWordLink], SkillKeyWordLink] = SkillKeyWordLink.syntax("s_kw_l")
    val skwlC: ColumnName[SkillKeyWordLink] = SkillKeyWordLink.column
  }

  override val schemaName: Some[String] = Some("courses")
  override val tableName = "skill"
  var defaultDBName = "default"

  val s: QuerySQLSyntaxProvider[SQLSyntaxSupport[SkillEntity], SkillEntity] = SkillEntity.syntax("s")
  val sC: ColumnName[SkillEntity] = SkillEntity.column

  /**
   * Вставка связей навыка и ключевых слов в таблицу skill_keyword_link
   *
   * @param skill     навык для которого ищутся ключевые слова
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertKeyWord(skill: SkillEntity)
                           (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = skill.keyWords.map(word => Seq(skill.id, word.id))

    withSQL {
      insertInto(SkillKeyWordLink)
        .namedValues(
          skwlC.abilityId -> sqls.?,
          skwlC.keywordId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Выборка ключевых слов навыка через skill_keyword_link
   *
   * @param skillPlug навык для которого ищутся ключевые слова
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные навыки
   */
  private def selectKeyWords(skillPlug: SkillPlug)
                            (implicit dbSession: DBSession): Seq[SkillKeyWordEntity] = {
    withSQL {
      selectFrom(SkillKeyWordEntity as skw)
        .leftJoin(SkillKeyWordLink as skwl)
        .on(skw.id, skwl.abilityId)
        .where.eq(skwl.abilityId, skillPlug.id)
    }.map(SkillKeyWordEntity(skw.resultName)).collection.apply()
  }

  /**
   * Удаление связей навыка и ключевых слов из таблицы skill_keyword_link
   *
   * @param skill     умение, связи с котором будут удалены
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteKeyWords(skill: SkillEntity)
                            (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(SkillKeyWordLink)
        .where.eq(skwlC.courseId, skill.id)
    }.update.apply()
  }

  /**
   * Вставка навыка в БД
   *
   * @param entity Entity которую необходимо вставить в таблицу
   * @param dbName - имя БД с которой мы хотим работать
   */
  def insert(entity: SkillEntity, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(SkillEntity)
          .namedValues(
            sC.id -> entity.id,
            sC.name -> entity.name
          )
      }.update.apply()

      insertKeyWord(entity)
    }

  /**
   * Вставка сразу нескольких skills в БД
   *
   * @param skills список skills которые мы хотим вставить
   * @param dbName имя БД с которой мы хотим работать
   */
  override def insertMultiRows(skills: Seq[SkillEntity], dbName: String = defaultDBName): Unit = {
    val batchParams: Seq[Seq[Any]] = skills.map(skill => Seq(skill.id, skill.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(SkillEntity)
          .namedValues(
            sC.id -> sqls.?,
            sC.name -> sqls.?
          )
      }.batch(batchParams: _*).apply()

      skills.foreach(insertKeyWord)
    }
  }

  /**
   * Получение навыка по id
   *
   * @param id     Entity которую необходимо получить
   * @param dbName имя БД с которой мы хотим работать
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  def findById(id: UUID, dbName: String = defaultDBName): Option[SkillEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val skillPlug: Option[SkillPlug] =
        withSQL {
          select.from(SkillPlug as sp)
            .where.eq(sp.id, id)
        }.map(SkillPlug(sp.resultName)).single.apply()

      skillPlug.map(SkillPlugMapper.plug2Entity)
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
  def findAll(limit: Int = 100,
              offset: Int = 0,
              orderBy: SQLSyntax = Id.value,
              sort: SQLSyntax = ASC.value,
              dbName: String = defaultDBName): Seq[SkillEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val skillPlugs: Seq[SkillPlug] =
        withSQL {
          select.all(sp).from(SkillPlug as sp)
            .orderBy(orderBy).append(sort)
            .limit(limit)
            .offset(offset)
        }.map(SkillPlug(sp.resultName)).collection.apply()

      skillPlugs.map(SkillPlugMapper.plug2Entity)
    }
  }

  /**
   * Обновление записи о навыке
   *
   * @param entity Entity которое будет обновлено
   * @param dbName имя БД с которой мы хотим работать
   */
  def update(entity: SkillEntity, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        QueryDSL.update(SkillEntity)
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
  def deleteById(id: UUID, dbName: String = defaultDBName): Unit =
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        deleteFrom(SkillEntity)
          .where.eq(sC.id, id)
      }.update.apply()
    }
}
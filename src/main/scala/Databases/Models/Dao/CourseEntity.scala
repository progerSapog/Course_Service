package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.AbilityEntity.a
import Databases.Models.Dao.KnowledgeEntity.k
import scalikejdbc._

import java.util.UUID

/**
 * Отображение таблицы course
 *
 * @param id              столбец id (UUID)
 * @param name            столбец name (VARCHAR(255))
 * @param inputSkills     входные навыки (join table: course_input_skill)
 * @param outputSkills    выходыне навыки  (join table: course_output_skill)
 * @param inputAbilities  входные умения (join table: course_input_ability)
 * @param outputAbilities выходные умения (join table: course_output_ability)
 * @param inputKnowledge  входыне знания (join table: course_input_knowledge)
 * @param outputKnowledge выходные знания (join table: course_output_knowledge)
 * @see IEntity
 */
case class CourseEntity(id: UUID,
                        name: String,
                        inputSkills: Seq[SkillEntity],
                        outputSkills: Seq[SkillEntity],
                        inputAbilities: Seq[AbilityEntity],
                        outputAbilities: Seq[AbilityEntity],
                        inputKnowledge: Seq[KnowledgeEntity],
                        outputKnowledge: Seq[KnowledgeEntity]
                       ) extends IEntity

/**
 * Объект компаньон, выполняющий роль DAO.
 * Реализует стандартные CRUD операции
 *
 * @see ICourseDao
 * @see UUIDFactory
 */
object CourseEntity extends SQLSyntaxSupport[CourseEntity] with ICourseDao {

  import Databases.Models.Dao.CourseEntity.CourseInputAbility.{cia, ciaC}
  import Databases.Models.Dao.CourseEntity.CourseInputKnowledge.{cik, cikC}
  import Databases.Models.Dao.CourseEntity.CourseInputSkill.{cis, cisC}
  import Databases.Models.Dao.CourseEntity.CourseOutputAbility.{coa, coaC}
  import Databases.Models.Dao.CourseEntity.CourseOutputKnowledge.{cok, cokC}
  import Databases.Models.Dao.CourseEntity.CourseOutputSkill.{cos, cosC}
  import Databases.Models.Dao.CourseEntity.CoursePlug.cp

  /**
   * Класс промежуточного представления курса, содержит только id и name
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param id   столбец id (UUID)
   * @param name столбец name (VARCHAR(255))
   */
  private case class CoursePlug(id: UUID, name: String) extends IPlug

  private object CoursePlug extends SQLSyntaxSupport[CoursePlug] {
    val cp: QuerySQLSyntaxProvider[SQLSyntaxSupport[CoursePlug], CoursePlug] = CoursePlug.syntax("cp")
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "course"

    def apply(r: ResultName[CoursePlug])(rs: WrappedResultSet): CoursePlug =
      new CoursePlug(
        id = UUID.fromString(rs.get(r.id)),
        name = rs.string(r.name)
      )
  }

  /**
   * Маппер из CoursePlug в CourseEntity
   */
  private object CoursePlugMapper {
    /**
     * Перевод из CoursePlug в CourseEntity.
     * Дополняет данные из CoursePlug связанными с данными курсом KAS
     *
     * @param plug      который будет переведен
     * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
     * @return полученный CourseEntity
     */
    def plug2Entity(plug: CoursePlug)(implicit dbSession: DBSession): CourseEntity = {
      CourseEntity(
        id = plug.id,
        name = plug.name,
        inputSkills = selectInputSkills(plug),
        outputSkills = selectOutputSkills(plug),
        inputAbilities = selectInputAbilities(plug),
        outputAbilities = selectOutputAbilities(plug),
        inputKnowledge = selectInputKnowledge(plug),
        outputKnowledge = selectOutputKnowledge(plug)
      )
    }
  }

  /**
   * Представление таблицы связи course_input_skill
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param courseId id курса
   * @param skillId  id входного курса
   */
  private case class CourseInputSkill(courseId: UUID, skillId: UUID)

  private object CourseInputSkill extends SQLSyntaxSupport[CourseInputSkill] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "course_input_skill"
    val cis: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseInputSkill], CourseInputSkill] = CourseInputSkill.syntax("c_i_s")
    val cisC: ColumnName[CourseInputSkill] = CourseInputSkill.column
  }

  /**
   * Представление таблицы связи course_output_skill
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param courseId id курса
   * @param skillId  id выходного курса
   */
  private case class CourseOutputSkill(courseId: UUID, skillId: UUID)

  private object CourseOutputSkill extends SQLSyntaxSupport[CourseOutputSkill] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "course_output_skill"
    val cos: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseOutputSkill], CourseOutputSkill] = CourseOutputSkill.syntax("c_o_s")
    val cosC: ColumnName[CourseOutputSkill] = CourseOutputSkill.column
  }

  /**
   * Представление таблицы связи course_input_ability
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param courseId  id курса
   * @param abilityId id входного умения
   */
  private case class CourseInputAbility(courseId: UUID, abilityId: UUID)

  private object CourseInputAbility extends SQLSyntaxSupport[CourseInputAbility] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "course_input_ability"
    val cia: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseInputAbility], CourseInputAbility] = CourseInputAbility.syntax("c_i_a")
    val ciaC: ColumnName[CourseInputAbility] = CourseInputAbility.column
  }

  /**
   * Представление таблицы связи course_output_ability
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param courseId  id курса
   * @param abilityId id выходного умения
   */
  private case class CourseOutputAbility(courseId: UUID, abilityId: UUID)

  private object CourseOutputAbility extends SQLSyntaxSupport[CourseOutputAbility] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "course_output_ability"
    val coa: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseOutputAbility], CourseOutputAbility] = CourseOutputAbility.syntax("c_o_a")
    val coaC: ColumnName[CourseOutputAbility] = CourseOutputAbility.column
  }

  /**
   * Представление таблицы связи course_input_knowledge
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param courseId    id курса
   * @param knowledgeId id входного знания
   */
  private case class CourseInputKnowledge(courseId: UUID, knowledgeId: UUID)

  private object CourseInputKnowledge extends SQLSyntaxSupport[CourseInputKnowledge] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "course_input_knowledge"
    val cik: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseInputKnowledge], CourseInputKnowledge] = CourseInputKnowledge.syntax("c_i_k")
    val cikC: ColumnName[CourseInputKnowledge] = CourseInputKnowledge.column
  }

  /**
   * Представление таблицы связи course_output_knowledge
   *
   * Объект компаньон, позволяющий работать с данным отображением при помощи
   * type safe DSL
   *
   * @param courseId    id курса
   * @param knowledgeId id выходного знания
   */
  private case class CourseOutputKnowledge(courseId: UUID, knowledgeId: UUID)

  private object CourseOutputKnowledge extends SQLSyntaxSupport[CourseOutputKnowledge] {
    override val schemaName: Some[String] = Some("courses")
    override val tableName = "course_output_knowledge"
    val cok: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseOutputKnowledge], CourseOutputKnowledge] = CourseOutputKnowledge.syntax("c_o_k")
    val cokC: ColumnName[CourseOutputKnowledge] = CourseOutputKnowledge.column
  }

  override val schemaName: Some[String] = Some("courses")
  override val tableName = "course"
  var defaultDBName = "default"

  val c: QuerySQLSyntaxProvider[SQLSyntaxSupport[CourseEntity], CourseEntity] = CourseEntity.syntax("c")
  val cc: ColumnName[CourseEntity] = CourseEntity.column

  /**
   * Вставка связей курса и входных навыков в таблицу course_input_skill
   *
   * @param course    курс с которым связаны навыки
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertInputSkills(course: CourseEntity)
                               (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = course.inputSkills.map(skill => Seq(course.id, skill.id))

    withSQL {
      insertInto(CourseInputSkill)
        .namedValues(
          cisC.courseId -> sqls.?,
          cisC.skillId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Вставка связей курса и выходных навыков в таблицу course_output_skill
   *
   * @param course    курс с которым связаны навыки
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertOutputSkills(course: CourseEntity)
                                (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = course.outputSkills.map(skill => Seq(course.id, skill.id))

    withSQL {
      insertInto(CourseOutputSkill)
        .namedValues(
          cosC.courseId -> sqls.?,
          cosC.skillId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Вставка связей курса и входноых умений в таблицу course_input_ability
   *
   * @param course    курс с которым связаны умения
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertInputAbilities(course: CourseEntity)
                                  (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = course.inputAbilities.map(ability => Seq(course.id, ability.id))

    withSQL {
      insertInto(CourseInputAbility)
        .namedValues(
          ciaC.courseId -> sqls.?,
          ciaC.abilityId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Вставка связей курса и выходных умений в таблицу course_output_ability
   *
   * @param course    курс с которым связаны умения
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertOutputAbilities(course: CourseEntity)
                                   (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = course.outputAbilities.map(ability => Seq(course.id, ability.id))

    withSQL {
      insertInto(CourseOutputAbility)
        .namedValues(
          coaC.courseId -> sqls.?,
          coaC.abilityId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Вставка связей курса и входных знаний в таблицу course_input_knowledge
   *
   * @param course    курс, с которым связаные знания
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertInputKnowledge(course: CourseEntity)
                                  (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = course.inputKnowledge.map(knowledge => Seq(course.id, knowledge.id))

    withSQL {
      insertInto(CourseInputKnowledge)
        .namedValues(
          cikC.courseId -> sqls.?,
          cikC.knowledgeId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Вставка связей курса и выходных знаний в таблицу course_output_knowledge
   *
   * @param course    курс, с которым связано знание
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertOutputKnowledge(course: CourseEntity)
                                   (implicit dbSession: DBSession): Unit = {
    val batchParams: Seq[Seq[Any]] = course.outputKnowledge.map(knowledge => Seq(course.id, knowledge.id))

    withSQL {
      insertInto(CourseOutputKnowledge)
        .namedValues(
          cokC.courseId -> sqls.?,
          cokC.knowledgeId -> sqls.?
        )
    }.batch(batchParams: _*).apply()
  }

  /**
   * Вставка связей курса и всех KAS
   *
   * @param course    курс с которым связаны KAS
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertKAS(course: CourseEntity)
                       (implicit dbSession: DBSession): Unit = {
    insertInputSkills(course)
    insertOutputSkills(course)
    insertInputAbilities(course)
    insertOutputAbilities(course)
    insertInputKnowledge(course)
    insertOutputKnowledge(course)
  }

  /**
   * Выборка входных навыков курса через таблицу course_input_skill
   *
   * @param coursePlug курс входные знания которого мы ищем
   * @param dbSession  имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные навыки
   */
  private def selectInputSkills(coursePlug: CoursePlug)
                               (implicit dbSession: DBSession): Seq[SkillEntity] = {
    withSQL {
      selectFrom(SkillEntity as sp)
        .leftJoin(CourseInputSkill as cis)
        .on(sp.id, cis.skillId)
        .where.eq(cis.courseId, coursePlug.id)
    }.map(SkillEntity(sp.resultName)).collection.apply()
  }

  /**
   * Выборка выходных навыков курса через таблицу course_output_skill
   *
   * @param coursePlug курс выходные знания которого мы ищем
   * @param dbSession  - имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные выходные навыки
   */
  private def selectOutputSkills(coursePlug: CoursePlug)
                                (implicit dbSession: DBSession): Seq[SkillEntity] = {
    withSQL {
      selectFrom(SkillEntity as sp)
        .leftJoin(CourseOutputSkill as cos)
        .on(sp.id, cos.skillId)
        .where.eq(cos.courseId, coursePlug.id)
    }.map(SkillEntity(sp.resultName)).collection.apply()
  }

  /**
   * Выборка входных умений курса через таблицу course_input_ability
   *
   * @param coursePlug курс входыне умения которого мы ищем
   * @param dbSession  имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные умения
   */
  private def selectInputAbilities(coursePlug: CoursePlug)
                                  (implicit dbSession: DBSession): Seq[AbilityEntity] = {
    withSQL {
      selectFrom(AbilityEntity as a)
        .leftJoin(CourseInputAbility as cia)
        .on(a.id, cia.abilityId)
        .where.eq(cia.courseId, coursePlug.id)
    }.map(AbilityEntity(a.resultName)).collection.apply()
  }

  /**
   * Выборка выходных умений курса через таблицу course_output_ability
   *
   * @param coursePlug курс выходные умения которого мы ищем
   * @param dbSession  имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные выходные умения
   */
  private def selectOutputAbilities(coursePlug: CoursePlug)
                                   (implicit dbSession: DBSession): Seq[AbilityEntity] = {
    withSQL {
      selectFrom(AbilityEntity as a)
        .leftJoin(CourseOutputAbility as coa)
        .on(a.id, coa.abilityId)
        .where.eq(coa.courseId, coursePlug.id)
    }.map(AbilityEntity(a.resultName)).collection.apply()
  }

  /**
   * Выборка входных знаний курса через таблицу course_input_knowledge
   *
   * @param coursePlug курс входыне знания которого мы ищем
   * @param dbSession  имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные знания
   */
  private def selectInputKnowledge(coursePlug: CoursePlug)
                                  (implicit dbSession: DBSession): Seq[KnowledgeEntity] = {
    withSQL {
      selectFrom(KnowledgeEntity as k)
        .leftJoin(CourseInputKnowledge as cik)
        .on(k.id, cik.knowledgeId)
        .where.eq(cik.courseId, coursePlug.id)
    }.map(KnowledgeEntity(k.resultName)).collection.apply()
  }

  /**
   * Выборка выходных знаний курса через таблицу course_input_knowledge
   *
   * @param coursePlug курс входыне знания которого мы ищем
   * @param dbSession  имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные знания
   */
  private def selectOutputKnowledge(coursePlug: CoursePlug)
                                   (implicit dbSession: DBSession): Seq[KnowledgeEntity] = {

    withSQL {
      selectFrom(KnowledgeEntity as k)
        .leftJoin(CourseOutputKnowledge as cok)
        .on(k.id, cok.knowledgeId)
        .where.eq(cok.courseId, coursePlug.id)
    }.map(KnowledgeEntity(k.resultName)).collection.apply()
  }

  /**
   * Удаление связей курса и его входных навыков из таблицы course_input_skill
   *
   * @param course    курс, связи с которым мы хотим удалить
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteInputSkills(course: CourseEntity)
                               (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseInputSkill)
        .where.eq(cisC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его выходных навыков из таблицы course_output_skill
   *
   * @param course    курс, связи с которым мы хотим удалить
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteOutputSkills(course: CourseEntity)
                                (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseOutputSkill)
        .where.eq(cosC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его входных умений из таблицы course_input_ability
   *
   * @param course    курс, связи с которым мы хотим удалить
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteInputAbilities(course: CourseEntity)
                                  (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseInputAbility)
        .where.eq(ciaC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его выходных умений из таблицы course_output_ability
   *
   * @param course    курс, связи с которым мы хотим удалить
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteOutputAbilities(course: CourseEntity)
                                   (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseOutputAbility)
        .where.eq(coaC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его входных знаний из таблицы course_input_knowledge
   *
   * @param course    курс, связи с которым мы хотим удалить
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteInputKnowledge(course: CourseEntity)
                                  (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseInputKnowledge)
        .where.eq(cikC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его выходных знаний из таблицы course_output_knowledge
   *
   * @param course    курс, связи с которым мы хотим удалить
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteOutputKnowledge(course: CourseEntity)
                                   (implicit dbSession: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseOutputKnowledge)
        .where.eq(cokC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса со всеми KAS
   *
   * @param course    курс, связи с которым мы хотим удалить
   * @param dbSession имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteKAS(course: CourseEntity)
                       (implicit dbSession: DBSession): Unit = {
    deleteInputSkills(course)
    deleteOutputSkills(course)
    deleteInputAbilities(course)
    deleteOutputAbilities(course)
    deleteInputKnowledge(course)
    deleteOutputKnowledge(course)
  }

  /**
   * Вставка курса в базу данных (вместе с его KAS).
   *
   * @param entity курс для вставки
   * @param dbName имя БД с которой мы хотим работать
   */
  override def insert(entity: CourseEntity, dbName: String = defaultDBName): Unit = {
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(CourseEntity)
          .namedValues(
            cc.id -> entity.id,
            cc.name -> entity.name
          )
      }.update.apply()

      insertKAS(entity)
    }
  }


  /**
   * Вставка сразу нескольких KAS в БД
   *
   * @param courses список KAS которые мы хотим вставить
   * @param dbName  имя БД с которой мы хотим работать
   */
  override def insertMultiRows(courses: Seq[CourseEntity], dbName: String): Unit = {
    val batchCourses: Seq[Seq[Any]] = courses.map(course => Seq(course.id, course.name))

    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        insertInto(CourseEntity)
          .namedValues(
            cc.id -> sqls.?,
            cc.name -> sqls.?
          )
      }.batch(batchCourses: _*).apply()

      courses.foreach(insertKAS)
    }
  }

  /**
   * Получение курса по id
   *
   * @param id     Entity которую необходимо получить
   * @param dbName - имя БД с которой мы хотим работать
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID, dbName: String = defaultDBName): Option[CourseEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val coursePlugOpt: Option[CoursePlug] =
        withSQL {
          selectFrom(CoursePlug as cp)
            .where.eq(cp.id, id)
        }.map(CoursePlug(cp.resultName)).single.apply()

      coursePlugOpt.map(CoursePlugMapper.plug2Entity)
    }
  }

  /**
   * Получение указанного кол-ва курсов
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @param dbName  имя БД с которой мы хотим работать
   * @return последовательность всех Entity из таблицы
   */
  override def findAll(limit: Int = 100,
                       offset: Int = 0,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value,
                       dbName: String = defaultDBName): Seq[CourseEntity] = {
    NamedDB(s"$dbName") readOnly { implicit session =>
      val coursePlugs: Seq[CoursePlug] =
        withSQL {
          select.all(cp).from(CoursePlug as cp)
            .orderBy(orderBy).append(sort)
            .limit(limit)
            .offset(offset)
        }.map(CoursePlug(cp.resultName)).collection.apply()

      coursePlugs.map(CoursePlugMapper.plug2Entity)
    }
  }

  /**
   * Обновление записи о курси и его KAS
   *
   * @param entity курс для обновления
   * @param dbName имя БД с которой мы хотим работать
   */
  override def update(entity: CourseEntity, dbName: String = defaultDBName): Unit = {
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        QueryDSL.update(CourseEntity)
          .set(
            cc.name -> entity.name
          ).where.eq(cc.id, entity.id)
      }.update.apply()

      deleteKAS(entity)
      insertKAS(entity)
    }
  }

  /**
   * Удаление курса и его связей по ID
   *
   * @param id     Entity которую необходимо удалить
   * @param dbName имя БД с которой мы хотим работать
   */
  override def deleteById(id: UUID, dbName: String = defaultDBName): Unit = {
    NamedDB(s"$dbName") localTx { implicit session =>
      withSQL {
        deleteFrom(CoursePlug)
          .where.eq(cc.id, id)
      }.update.apply()
    }
  }

  /**
   * Получение множества курсов, образующих образовательную траекторию
   *
   * @param inputKnowledge  входные знания траектории
   * @param outputKnowledge выходные знания траектории
   * @param inputAbilities  входыне умения траектории
   * @param outputAbilities выходыне умения траектории
   * @param inputSkills     входыне навыки траектории
   * @param outputSkill     выходные навыки траектории
   * @return мноежство курсов участвующих в траектории
   */
  def makeTrajectory(inputKnowledge: Seq[KnowledgeEntity] = Seq.empty,
                     outputKnowledge: Seq[KnowledgeEntity] = Seq.empty,
                     inputAbilities: Seq[AbilityEntity] = Seq.empty,
                     outputAbilities: Seq[AbilityEntity] = Seq.empty,
                     inputSkills: Seq[SkillEntity] = Seq.empty,
                     outputSkill: Seq[SkillEntity] = Seq.empty,
                    ): Seq[CourseEntity] = {
    DB readOnly { implicit session =>

      val coursePlugs: Seq[CoursePlug] = {
        sql"""
            SELECT * FROM makeTrajectory(
                inputsknowledge := array[${inputKnowledge.map(_.id)}]::UUID[],
                outputsknowledge := array[${outputKnowledge.map(_.id)}]::UUID[],
                inputsabilities := array[${inputAbilities.map(_.id)}]::UUID[],
                outputsabilities := array[${outputAbilities.map(_.id)}]::UUID[],
                inputsskills := array[${inputSkills.map(_.id)}]::UUID[],
                outputsskills := array[${outputSkill.map(_.id)}]::UUID[]);
        """.map(course => CoursePlug(
          id = UUID.fromString(course.string("id")),
          name = course.string("name"))).collection.apply()
      }

      coursePlugs.map(CoursePlugMapper.plug2Entity)
    }
  }
}
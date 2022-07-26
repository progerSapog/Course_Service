package Databases.Models.Dao

import Databases.Configurations.{ASC, Id}
import Databases.Models.Dao.LinkTables.CourseInputAbility.{cia, ciaC}
import Databases.Models.Dao.LinkTables.CourseInputKnowledge.{cik, cikC}
import Databases.Models.Dao.LinkTables.{CourseInputAbility, CourseInputKnowledge, CourseInputSkill, CourseOutputAbility, CourseOutputKnowledge, CourseOutputSkill}
import Databases.Models.Dao.LinkTables.CourseInputSkill.{cis, cisC}
import Databases.Models.Dao.LinkTables.CourseOutputAbility.{coa, coaC}
import Databases.Models.Dao.LinkTables.CourseOutputKnowledge.{cok, cokC}
import Databases.Models.Dao.LinkTables.CourseOutputSkill.{cos, cosC}
import Databases.Models.Dao.Plugs.AbilityPlug.a
import Databases.Models.Dao.Plugs.CoursePlug.{c, cC}
import Databases.Models.Dao.Plugs.KnowledgePlug.k
import Databases.Models.Dao.Plugs.SkillPlug.s
import Databases.Models.Dao.Plugs.{AbilityPlug, CoursePlug, KnowledgePlug, SkillPlug}
import scalikejdbc._

import java.util.UUID

/**
 * Сущность Курс (course).
 * Содержит связанные с данным курсом Kass
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
 * Позвоялет работать с данным отображением при помощи type safe DSL
 *
 * @see ICourseDao
 * @see UUIDFactory
 */
object CourseEntity extends ICourseDao {

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
   * @param course  курс с которым связаны навыки
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertOutputSkills(course: CourseEntity)
                                (implicit session: DBSession): Unit = {
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
   * @param course  курс с которым связаны умения
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertInputAbilities(course: CourseEntity)
                                  (implicit session: DBSession): Unit = {
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
   * @param course  курс с которым связаны умения
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertOutputAbilities(course: CourseEntity)
                                   (implicit session: DBSession): Unit = {
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
   * @param course  курс, с которым связаные знания
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertInputKnowledge(course: CourseEntity)
                                  (implicit session: DBSession): Unit = {
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
   * @param course  курс, с которым связано знание
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertOutputKnowledge(course: CourseEntity)
                                   (implicit session: DBSession): Unit = {
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
   * @param course  курс с которым связаны KAS
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def insertKAS(course: CourseEntity)
                       (implicit session: DBSession): Unit = {
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
   * @param session    имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные навыки
   */
  private def selectInputSkills(coursePlug: CoursePlug)
                               (implicit session: DBSession): Seq[SkillEntity] = {
    val skillPlugs: Seq[SkillPlug] =
      withSQL {
        selectFrom(SkillPlug as s)
          .leftJoin(CourseInputSkill as cis)
          .on(s.id, cis.skillId)
          .where.eq(cis.courseId, coursePlug.id)
      }.map(SkillPlug(s.resultName)).collection.apply()

    skillPlugs.map(plug => SkillEntity(
      id = plug.id,
      name = plug.name,
      keyWords = SkillEntity.selectKeyWords(plug)
    ))
  }

  /**
   * Выборка выходных навыков курса через таблицу course_output_skill
   *
   * @param coursePlug курс выходные знания которого мы ищем
   * @param session    - имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные выходные навыки
   */
  private def selectOutputSkills(coursePlug: CoursePlug)
                                (implicit session: DBSession): Seq[SkillEntity] = {
    val skillPlugs: Seq[SkillPlug] =
      withSQL {
        selectFrom(SkillPlug as s)
          .leftJoin(CourseOutputSkill as cos)
          .on(s.id, cos.skillId)
          .where.eq(cos.courseId, coursePlug.id)
      }.map(SkillPlug(s.resultName)).collection.apply()

    skillPlugs.map(plug => SkillEntity(
      id = plug.id,
      name = plug.name,
      keyWords = SkillEntity.selectKeyWords(plug)
    ))
  }

  /**
   * Выборка входных умений курса через таблицу course_input_ability
   *
   * @param coursePlug курс входыне умения которого мы ищем
   * @param session    имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные умения
   */
  private def selectInputAbilities(coursePlug: CoursePlug)
                                  (implicit session: DBSession): Seq[AbilityEntity] = {
    val abilityPlugs: Seq[AbilityPlug] =
      withSQL {
        selectFrom(AbilityPlug as a)
          .leftJoin(CourseInputAbility as cia)
          .on(a.id, cia.abilityId)
          .where.eq(cia.courseId, coursePlug.id)
      }.map(AbilityPlug(a.resultName)).collection.apply()

    abilityPlugs.map(plug => AbilityEntity(
      id = plug.id,
      name = plug.name,
      keyWords = AbilityEntity.selectKeyWords(plug)
    ))
  }

  /**
   * Выборка выходных умений курса через таблицу course_output_ability
   *
   * @param coursePlug курс выходные умения которого мы ищем
   * @param session    имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные выходные умения
   */
  private def selectOutputAbilities(coursePlug: CoursePlug)
                                   (implicit session: DBSession): Seq[AbilityEntity] = {
    val abilityPlugs: Seq[AbilityPlug] =
      withSQL {
        selectFrom(AbilityPlug as a)
          .leftJoin(CourseOutputAbility as coa)
          .on(a.id, coa.abilityId)
          .where.eq(coa.courseId, coursePlug.id)
      }.map(AbilityPlug(a.resultName)).collection.apply()

    abilityPlugs.map(plug => AbilityEntity(
      id = plug.id,
      name = plug.name,
      keyWords = AbilityEntity.selectKeyWords(plug)
    ))
  }

  /**
   * Выборка входных знаний курса через таблицу course_input_knowledge
   *
   * @param coursePlug курс входыне знания которого мы ищем
   * @param session    имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные знания
   */
  private def selectInputKnowledge(coursePlug: CoursePlug)
                                  (implicit session: DBSession): Seq[KnowledgeEntity] = {
    val knowledgePlugs: Seq[KnowledgePlug] =
      withSQL {
        selectFrom(KnowledgePlug as k)
          .leftJoin(CourseInputKnowledge as cik)
          .on(k.id, cik.knowledgeId)
          .where.eq(cik.courseId, coursePlug.id)
      }.map(KnowledgePlug(k.resultName)).collection.apply()

    knowledgePlugs.map(plug => KnowledgeEntity(
      id = plug.id,
      name = plug.name,
      keyWords = KnowledgeEntity.selectKeyWords(plug)
    ))
  }

  /**
   * Выборка выходных знаний курса через таблицу course_input_knowledge
   *
   * @param coursePlug курс входыне знания которого мы ищем
   * @param session    имплисит, позволяющий вызывать метод внутри сессии
   * @return найденные входные знания
   */
  private def selectOutputKnowledge(coursePlug: CoursePlug)
                                   (implicit session: DBSession): Seq[KnowledgeEntity] = {
    val knowledgePlugs: Seq[KnowledgePlug] =
      withSQL {
        selectFrom(KnowledgePlug as k)
          .leftJoin(CourseOutputKnowledge as cok)
          .on(k.id, cok.knowledgeId)
          .where.eq(cok.courseId, coursePlug.id)
      }.map(KnowledgePlug(k.resultName)).collection.apply()

    knowledgePlugs.map(plug => KnowledgeEntity(
      id = plug.id,
      name = plug.name,
      keyWords = KnowledgeEntity.selectKeyWords(plug)
    ))
  }

  /**
   * Удаление связей курса и его входных навыков из таблицы course_input_skill
   *
   * @param course  курс, связи с которым мы хотим удалить
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteInputSkills(course: CourseEntity)
                               (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseInputSkill)
        .where.eq(cisC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его выходных навыков из таблицы course_output_skill
   *
   * @param course  курс, связи с которым мы хотим удалить
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteOutputSkills(course: CourseEntity)
                                (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseOutputSkill)
        .where.eq(cosC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его входных умений из таблицы course_input_ability
   *
   * @param course  курс, связи с которым мы хотим удалить
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteInputAbilities(course: CourseEntity)
                                  (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseInputAbility)
        .where.eq(ciaC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его выходных умений из таблицы course_output_ability
   *
   * @param course  курс, связи с которым мы хотим удалить
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteOutputAbilities(course: CourseEntity)
                                   (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseOutputAbility)
        .where.eq(coaC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его входных знаний из таблицы course_input_knowledge
   *
   * @param course  курс, связи с которым мы хотим удалить
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteInputKnowledge(course: CourseEntity)
                                  (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseInputKnowledge)
        .where.eq(cikC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса и его выходных знаний из таблицы course_output_knowledge
   *
   * @param course  курс, связи с которым мы хотим удалить
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteOutputKnowledge(course: CourseEntity)
                                   (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(CourseOutputKnowledge)
        .where.eq(cokC.courseId, course.id)
    }.update.apply()
  }

  /**
   * Удаление связей курса со всеми KAS
   *
   * @param course  курс, связи с которым мы хотим удалить
   * @param session имплисит, позволяющий вызывать метод внутри сессии
   */
  private def deleteKAS(course: CourseEntity)
                       (implicit session: DBSession): Unit = {
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
   */
  override def insert(entity: CourseEntity)
                     (implicit session: DBSession): Unit = {
    withSQL {
      insertInto(CoursePlug)
        .namedValues(
          cC.id -> entity.id,
          cC.name -> entity.name
        )
    }.update.apply()

    insertKAS(entity)
  }


  /**
   * Вставка сразу нескольких KAS в БД
   *
   * @param courses список KAS которые мы хотим вставить
   */
  override def insertMultiRows(courses: Seq[CourseEntity])
                              (implicit session: DBSession): Unit = {
    val batchCourses: Seq[Seq[Any]] = courses.map(course => Seq(course.id, course.name))

    withSQL {
      insertInto(CoursePlug)
        .namedValues(
          cC.id -> sqls.?,
          cC.name -> sqls.?
        )
    }.batch(batchCourses: _*).apply()

    courses.foreach(insertKAS)
  }

  /**
   * Получение курса по id
   *
   * @param id Entity которую необходимо получить
   * @return Optional с Entity если такая есть в БД, иначе Option.empty
   */
  override def findById(id: UUID)
                       (implicit session: DBSession): Option[CourseEntity] = {
    val coursePlugOpt: Option[CoursePlug] =
      withSQL {
        selectFrom(CoursePlug as c)
          .where.eq(c.id, id)
      }.map(CoursePlug(c.resultName)).single.apply()

    coursePlugOpt.map(plug => CourseEntity(
      id = plug.id,
      name = plug.name,
      inputSkills = CourseEntity.selectInputSkills(plug),
      outputSkills = CourseEntity.selectOutputSkills(plug),
      inputAbilities = CourseEntity.selectInputAbilities(plug),
      outputAbilities = CourseEntity.selectOutputAbilities(plug),
      inputKnowledge = CourseEntity.selectInputKnowledge(plug),
      outputKnowledge = CourseEntity.selectOutputKnowledge(plug)
    ))
  }

  /**
   * Получение указанного кол-ва курсов
   *
   * @param limit   кол-во записей которые необходимо получить
   * @param offset  отсутуп от начала полученных записей
   * @param orderBy поле по которому необходимо отсортировать записи
   * @param sort    порядок сортировки
   * @return последовательность всех Entity из таблицы
   */
  override def findAll(limit: Int = 100,
                       offset: Int = 0,
                       orderBy: SQLSyntax = Id.value,
                       sort: SQLSyntax = ASC.value)
                      (implicit session: DBSession): Seq[CourseEntity] = {
    val coursePlugs: Seq[CoursePlug] =
      withSQL {
        select.all(c).from(CoursePlug as c)
          .orderBy(orderBy).append(sort)
          .limit(limit)
          .offset(offset)
      }.map(CoursePlug(c.resultName)).collection.apply()

    coursePlugs.map(plug => CourseEntity(
      id = plug.id,
      name = plug.name,
      inputSkills = CourseEntity.selectInputSkills(plug),
      outputSkills = CourseEntity.selectOutputSkills(plug),
      inputAbilities = CourseEntity.selectInputAbilities(plug),
      outputAbilities = CourseEntity.selectOutputAbilities(plug),
      inputKnowledge = CourseEntity.selectInputKnowledge(plug),
      outputKnowledge = CourseEntity.selectOutputKnowledge(plug)
    ))
  }

  /**
   * Обновление записи о курси и его KAS
   *
   * @param entity курс для обновления
   */
  override def update(entity: CourseEntity)
                     (implicit session: DBSession): Unit = {
    withSQL {
      QueryDSL.update(CoursePlug)
        .set(
          cC.name -> entity.name
        ).where.eq(cC.id, entity.id)
    }.update.apply()

    deleteKAS(entity)
    insertKAS(entity)
  }

  /**
   * Удаление курса и его связей по ID
   *
   * @param id Entity которую необходимо удалить
   */
  override def deleteById(id: UUID)
                         (implicit session: DBSession): Unit = {
    withSQL {
      deleteFrom(CoursePlug)
        .where.eq(cC.id, id)
    }.update.apply()
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
                     outputSkill: Seq[SkillEntity] = Seq.empty)
                    (implicit session: DBSession): Seq[CourseEntity] = {

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

    coursePlugs.map(plug => CourseEntity(
      id = plug.id,
      name = plug.name,
      inputSkills = CourseEntity.selectInputSkills(plug),
      outputSkills = CourseEntity.selectOutputSkills(plug),
      inputAbilities = CourseEntity.selectInputAbilities(plug),
      outputAbilities = CourseEntity.selectOutputAbilities(plug),
      inputKnowledge = CourseEntity.selectInputKnowledge(plug),
      outputKnowledge = CourseEntity.selectOutputKnowledge(plug)
    ))
  }
}
package Databases.Models.Dao

import Databases.Configurations.{DESC, Name}
import Databases.Models.Dao.Kas.{AbilityEntity, KnowledgeEntity, SkillEntity}
import Databases.Models.Dao.Keywords.{AbilityKeyWordEntity, KnowledgeKeyWordEntity, SkillKeyWordEntity}
import Databases.Models.IBeforeAfterAllDBInit
import scalikejdbc.NamedDB
import scalikejdbc.specs2.mutable.AutoRollback

import java.sql.SQLException
import java.util.UUID

object CourseEntityTest extends IBeforeAfterAllDBInit {
  val inputKnowledgeKeyWords: Seq[KnowledgeKeyWordEntity] = Seq(
    KnowledgeKeyWordEntity(UUID.randomUUID(), "InKnowledgeKeyword1"),
    KnowledgeKeyWordEntity(UUID.randomUUID(), "InKnowledgeKeyword2"),
  )
  val outputKnowledgeKeyWords: Seq[KnowledgeKeyWordEntity] = Seq(
    KnowledgeKeyWordEntity(UUID.randomUUID(), "OutKnowledgeKeyword1"),
    KnowledgeKeyWordEntity(UUID.randomUUID(), "OutKnowledgeKeyword2"),
  )

  val inputKnowledge: Seq[KnowledgeEntity] = Seq(
    KnowledgeEntity(UUID.randomUUID(), "InKnowledge1", keyWords = inputKnowledgeKeyWords),
    KnowledgeEntity(UUID.randomUUID(), "InKnowledge2", keyWords = inputKnowledgeKeyWords),
  )
  val outputKnowledge: Seq[KnowledgeEntity] = Seq(
    KnowledgeEntity(UUID.randomUUID(), "OutKnowledge1", keyWords = outputKnowledgeKeyWords),
    KnowledgeEntity(UUID.randomUUID(), "OutKnowledge2", keyWords = outputKnowledgeKeyWords),
  )


  val inputAbilityKeyWords: Seq[AbilityKeyWordEntity] = Seq(
    AbilityKeyWordEntity(UUID.randomUUID(), "InAbilityKeyword1"),
    AbilityKeyWordEntity(UUID.randomUUID(), "InAbilityKeyword2"),
  )
  val outputAbilityKeyWords: Seq[AbilityKeyWordEntity] = Seq(
    AbilityKeyWordEntity(UUID.randomUUID(), "OutAbilityKeyword1"),
    AbilityKeyWordEntity(UUID.randomUUID(), "OutAbilityKeyword2"),
  )

  val inputAbilities: Seq[AbilityEntity] = Seq(
    AbilityEntity(UUID.randomUUID(), "InAbility1", keyWords = inputAbilityKeyWords),
    AbilityEntity(UUID.randomUUID(), "InAbility2", keyWords = inputAbilityKeyWords),
  )
  val outputAbilities: Seq[AbilityEntity] = Seq(
    AbilityEntity(UUID.randomUUID(), "OutAbility1", keyWords = outputAbilityKeyWords),
    AbilityEntity(UUID.randomUUID(), "OutAbility2", keyWords = outputAbilityKeyWords),
  )


  val inputSkillsKeyWords: Seq[SkillKeyWordEntity] = Seq(
    SkillKeyWordEntity(UUID.randomUUID(), "InSkillKeyword1"),
    SkillKeyWordEntity(UUID.randomUUID(), "InSkillKeyword2"),
  )
  val outputSkillsKeyWords: Seq[SkillKeyWordEntity] = Seq(
    SkillKeyWordEntity(UUID.randomUUID(), "OutSkillKeyword1"),
    SkillKeyWordEntity(UUID.randomUUID(), "OutSkillKeyword2"),
  )

  val inputSkills: Seq[SkillEntity] = Seq(
    SkillEntity(UUID.randomUUID(), "InSkill1", keyWords = inputSkillsKeyWords),
    SkillEntity(UUID.randomUUID(), "InSkill2", keyWords = inputSkillsKeyWords),
  )
  val outputSkills: Seq[SkillEntity] = Seq(
    SkillEntity(UUID.randomUUID(), "OutSkill1", keyWords = outputSkillsKeyWords),
    SkillEntity(UUID.randomUUID(), "OutSkill2", keyWords = outputSkillsKeyWords)
  )

  override def beforeAll(): Unit = {
    super.beforeAll()
    NamedDB(DBName) localTx { implicit session =>
      KnowledgeKeyWordEntity.insertMultiRows(inputKnowledgeKeyWords)
      KnowledgeKeyWordEntity.insertMultiRows(outputKnowledgeKeyWords)

      AbilityKeyWordEntity.insertMultiRows(inputAbilityKeyWords)
      AbilityKeyWordEntity.insertMultiRows(outputAbilityKeyWords)

      SkillKeyWordEntity.insertMultiRows(inputSkillsKeyWords)
      SkillKeyWordEntity.insertMultiRows(outputSkillsKeyWords)


      KnowledgeEntity.insertMultiRows(inputKnowledge)
      KnowledgeEntity.insertMultiRows(outputKnowledge)

      AbilityEntity.insertMultiRows(inputAbilities)
      AbilityEntity.insertMultiRows(outputAbilities)

      SkillEntity.insertMultiRows(inputSkills)
      SkillEntity.insertMultiRows(outputSkills)
    }
  }

  override def afterAll(): Unit = {
    NamedDB(DBName) localTx { implicit session =>
      inputKnowledge.foreach(kas => KnowledgeEntity.deleteById(kas.id))
      outputKnowledge.foreach(kas => KnowledgeEntity.deleteById(kas.id))

      inputAbilities.foreach(kas => AbilityEntity.deleteById(kas.id))
      outputAbilities.foreach(kas => AbilityEntity.deleteById(kas.id))

      inputSkills.foreach(kas => SkillEntity.deleteById(kas.id))
      outputSkills.foreach(kas => SkillEntity.deleteById(kas.id))


      inputKnowledgeKeyWords.foreach(word => KnowledgeKeyWordEntity.deleteById(word.id))
      outputKnowledgeKeyWords.foreach(word => KnowledgeKeyWordEntity.deleteById(word.id))

      inputAbilityKeyWords.foreach(word => AbilityKeyWordEntity.deleteById(word.id))
      outputAbilityKeyWords.foreach(word => AbilityKeyWordEntity.deleteById(word.id))

      inputSkillsKeyWords.foreach(word => SkillKeyWordEntity.deleteById(word.id))
      outputSkillsKeyWords.foreach(word => SkillKeyWordEntity.deleteById(word.id))
    }
    super.afterAll()
  }

  sequential

  "Course successfully created" in new AutoRollback {
    val entity: CourseEntity = CourseEntity(
      id = UUID.randomUUID(),
      name = "Course1",
      inputSkills = inputSkills,
      outputSkills = outputSkills,
      inputAbilities = inputAbilities,
      outputAbilities = outputAbilities,
      inputKnowledge = inputKnowledge,
      outputKnowledge = outputKnowledge
    )

    CourseEntity.insert(entity)
    val res: Option[CourseEntity] = CourseEntity.findById(entity.id)

    res.isDefined must beTrue
    res.get mustEqual entity
  }

  "Course not created because, such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val entity: CourseEntity = CourseEntity(
      id = id,
      name = "Course1",
      inputSkills = inputSkills,
      outputSkills = outputSkills,
      inputAbilities = inputAbilities,
      outputAbilities = outputAbilities,
      inputKnowledge = inputKnowledge,
      outputKnowledge = outputKnowledge
    )
    val entityDuplicate: CourseEntity = CourseEntity(
      id = id,
      name = "Course2",
      inputSkills = inputSkills,
      outputSkills = outputSkills,
      inputAbilities = inputAbilities,
      outputAbilities = outputAbilities,
      inputKnowledge = inputKnowledge,
      outputKnowledge = outputKnowledge
    )

    CourseEntity.insert(entity)
    CourseEntity.insert(entityDuplicate) must throwA[SQLException]
  }

  "Courses successfully created" in new AutoRollback {
    val entities: Seq[CourseEntity] = Seq(
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course1",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course2",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course3",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course4",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course5",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      )
    )

    //    for (i <- 1 to 5;
    //         entities <- CourseEntity(
    //           id = UUID.randomUUID(),
    //           name = s"Course$i",
    //           inputSkills = inputSkills,
    //           outputSkills = outputSkills,
    //           inputAbilities = inputAbilities,
    //           outputAbilities = outputAbilities,
    //           inputKnowledge = inputKnowledge,
    //           outputKnowledge = outputKnowledge
    //         )
    //         ) yield (entities)

    CourseEntity.insertMultiRows(entities)
    val res: Seq[CourseEntity] = CourseEntity.findAll()

    res.nonEmpty must beTrue
    res mustEqual entities.sortBy(_.id.toString)
  }

  "Courses not create, because such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val entities: Seq[CourseEntity] = Seq(
      CourseEntity(
        id = id,
        name = "Course1",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course2",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = id,
        name = "Course3",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course4",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = id,
        name = "Course5",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      )
    )

    CourseEntity.insertMultiRows(entities) must throwA[SQLException]
  }

  "select Course from table by id" in new AutoRollback {
    val entity: CourseEntity = CourseEntity(
      id = UUID.randomUUID(),
      name = "Course1",
      inputSkills = inputSkills,
      outputSkills = outputSkills,
      inputAbilities = inputAbilities,
      outputAbilities = outputAbilities,
      inputKnowledge = inputKnowledge,
      outputKnowledge = outputKnowledge
    )
    CourseEntity.insert(entity)

    val res: Option[CourseEntity] = CourseEntity.findById(entity.id)
    res.isDefined must beTrue
    res.get mustEqual entity
  }

  "empty select, because record missing" in new AutoRollback {
    val res: Option[CourseEntity] = CourseEntity.findById(UUID.randomUUID())

    res.isEmpty must beTrue
    res.isDefined must beFalse
  }

  "select all Courses without parameters" in new AutoRollback {
    val entities: Seq[CourseEntity] = Seq(
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course1",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course2",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course3",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course4",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course5",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      )
    )

    CourseEntity.insertMultiRows(entities)
    val res: Seq[CourseEntity] = CourseEntity.findAll()

    res.nonEmpty must beTrue
    res mustEqual entities.sortBy(_.id.toString)
  }

  "select all Courses with limit" in new AutoRollback {
    val entities: Seq[CourseEntity] = Seq(
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course1",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course2",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course3",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course4",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course5",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      )
    )

    CourseEntity.insertMultiRows(entities)
    val res: Seq[CourseEntity] = CourseEntity.findAll(limit = 2)

    res.nonEmpty must beTrue
    res.size mustEqual 2
    res mustEqual entities.sortBy(_.id.toString).take(2)
  }

  "select all Courses with all parameters" in new AutoRollback {
    val entities: Seq[CourseEntity] = Seq(
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course1",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course2",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course3",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course4",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      ),
      CourseEntity(
        id = UUID.randomUUID(),
        name = "Course5",
        inputSkills = inputSkills,
        outputSkills = outputSkills,
        inputAbilities = inputAbilities,
        outputAbilities = outputAbilities,
        inputKnowledge = inputKnowledge,
        outputKnowledge = outputKnowledge
      )
    )

    CourseEntity.insertMultiRows(entities)

    val res: Seq[CourseEntity] = CourseEntity.findAll(limit = 3,
      offset = 1,
      orderBy = Name.value,
      sort = DESC.value)

    res.nonEmpty must beTrue
    res.size mustEqual 3
    res mustEqual entities.sortBy(_.name).reverse.slice(1, entities.size - 1)
  }

  "Course update successfully" in new AutoRollback {
    val entity: CourseEntity = CourseEntity(
      id = UUID.randomUUID(),
      name = "Course1",
      inputSkills = inputSkills,
      outputSkills = outputSkills,
      inputAbilities = inputAbilities,
      outputAbilities = outputAbilities,
      inputKnowledge = inputKnowledge,
      outputKnowledge = outputKnowledge
    )
    CourseEntity.insert(entity)

    val entityToUpdate: CourseEntity = CourseEntity(
      id = entity.id,
      name = "Course2",
      inputSkills = inputSkills,
      outputSkills = outputSkills,
      inputAbilities = inputAbilities,
      outputAbilities = outputAbilities,
      inputKnowledge = inputKnowledge,
      outputKnowledge = outputKnowledge
    )

    CourseEntity.update(entityToUpdate)
    val res: Option[CourseEntity] = CourseEntity.findById(entity.id)

    res.isDefined must beTrue
    res.get mustEqual entityToUpdate
  }

  "Course delete successfully" in new AutoRollback {
    val entity: CourseEntity = CourseEntity(
      id = UUID.randomUUID(),
      name = "Course1",
      inputSkills = inputSkills,
      outputSkills = outputSkills,
      inputAbilities = inputAbilities,
      outputAbilities = outputAbilities,
      inputKnowledge = inputKnowledge,
      outputKnowledge = outputKnowledge
    )
    CourseEntity.insert(entity)
    CourseEntity.deleteById(entity.id)

    val res: Option[CourseEntity] = CourseEntity.findById(entity.id)
    res.isEmpty must beTrue
    res.isDefined must beFalse
  }
}

package Databases.Models.Dao.Kas

import Databases.Configurations.{DESC, Name}
import Databases.Models.Dao.Keywords.SkillKeyWordEntity
import Databases.Models.IBeforeAfterAllDBInit
import scalikejdbc.NamedDB
import scalikejdbc.specs2.mutable.AutoRollback

import java.sql.SQLException
import java.util.UUID

object SkillEntityTest extends IBeforeAfterAllDBInit {
  val skillKeyWords: Seq[SkillKeyWordEntity] = Seq(
    SkillKeyWordEntity(UUID.randomUUID(), "keyWord1"),
    SkillKeyWordEntity(UUID.randomUUID(), "keyWord2"),
    SkillKeyWordEntity(UUID.randomUUID(), "keyWord3"),
    SkillKeyWordEntity(UUID.randomUUID(), "keyWord4"),
  )

  override def beforeAll(): Unit = {
    super.beforeAll()

    NamedDB(DBName) localTx { implicit session =>
      SkillKeyWordEntity.insertMultiRows(skillKeyWords)
    }
  }

  override def afterAll(): Unit = {
    NamedDB(DBName) localTx { implicit session =>
      skillKeyWords.foreach(word => SkillKeyWordEntity.deleteById(word.id))
    }

    super.afterAll()
  }

  sequential

  "Skill successfully created" in new AutoRollback {
    val entity: SkillEntity = SkillEntity(
      id = UUID.randomUUID(),
      name = "Skill1",
      keyWords = skillKeyWords
    )

    SkillEntity.insert(entity)
    val res: Option[SkillEntity] = SkillEntity.findById(entity.id)

    res.isDefined must beTrue
    res.get mustEqual entity
  }

  "Skill not created because, such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val entity: SkillEntity = SkillEntity(
      id = id,
      name = "Skill1",
      keyWords = skillKeyWords
    )
    val entityDuplicate: SkillEntity = SkillEntity(
      id = id,
      name = "Skill1",
      keyWords = skillKeyWords
    )

    SkillEntity.insert(entity)
    SkillEntity.insert(entityDuplicate) must throwA[SQLException]
  }

  "Skill not created because, such name exists" in new AutoRollback {
    val entity: SkillEntity = SkillEntity(
      id = UUID.randomUUID(),
      name = "Skill1",
      keyWords = skillKeyWords
    )
    val entityDuplicate: SkillEntity = SkillEntity(
      id = UUID.randomUUID(),
      name = "Skill1",
      keyWords = skillKeyWords
    )

    SkillEntity.insert(entity)
    SkillEntity.insert(entityDuplicate) must throwA[SQLException]
  }

  "Skills successfully created" in new AutoRollback {
    val entities: Seq[SkillEntity] = Seq(
      SkillEntity(UUID.randomUUID(), "Skill1", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill2", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill3", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill4", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill5", keyWords = skillKeyWords),
    )

    SkillEntity.insertMultiRows(entities)
    val res: Seq[SkillEntity] = SkillEntity.findAll()

    res.nonEmpty must beTrue
    res mustEqual entities.sortBy(_.id.toString)
  }

  "Skills not create, because such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val entities: Seq[SkillEntity] = Seq(
      SkillEntity(UUID.randomUUID(), "Skill1", keyWords = skillKeyWords),
      SkillEntity(id, "Skill2", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill3", keyWords = skillKeyWords),
      SkillEntity(id, "Skill4", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill5", keyWords = skillKeyWords),
    )

    SkillEntity.insertMultiRows(entities) must throwA[SQLException]
  }

  "Skills not create, because such name exists" in new AutoRollback {
    val name = "Skill1"

    val entities: Seq[SkillEntity] = Seq(
      SkillEntity(UUID.randomUUID(), name, keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill2", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), name, keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill4", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), name, keyWords = skillKeyWords),
    )

    SkillEntity.insertMultiRows(entities) must throwA[SQLException]
  }

  "select Skill from table by id" in new AutoRollback {
    val entity: SkillEntity = SkillEntity(
      id = UUID.randomUUID(),
      name = "Skill1",
      keyWords = skillKeyWords
    )
    SkillEntity.insert(entity)

    val res: Option[SkillEntity] = SkillEntity.findById(entity.id)
    res.isDefined must beTrue
    res.get mustEqual entity
  }

  "empty select, because record missing" in new AutoRollback {
    val res: Option[SkillEntity] = SkillEntity.findById(UUID.randomUUID())

    res.isEmpty must beTrue
    res.isDefined must beFalse
  }

  "select all Skills without parameters" in new AutoRollback {
    val entities: Seq[SkillEntity] = Seq(
      SkillEntity(UUID.randomUUID(), "Skill1", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill2", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill3", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill4", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill5", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill6", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill7", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill8", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill9", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill10", keyWords = skillKeyWords),
    )

    SkillEntity.insertMultiRows(entities)
    val res: Seq[SkillEntity] = SkillEntity.findAll()

    res.nonEmpty must beTrue
    res mustEqual entities.sortBy(_.id.toString)
  }

  "select all Skills with limit" in new AutoRollback {
    val entities: Seq[SkillEntity] = Seq(
      SkillEntity(UUID.randomUUID(), "Skill1", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill2", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill3", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill4", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill5", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill6", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill7", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill8", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill9", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill10", keyWords = skillKeyWords),
    )

    SkillEntity.insertMultiRows(entities)
    val res: Seq[SkillEntity] = SkillEntity.findAll(limit = 5)

    res.nonEmpty must beTrue
    res.size mustEqual 5
    res mustEqual entities.sortBy(_.id.toString).take(5)
  }

  "select all Skills with all parameters" in new AutoRollback {
    val entities: Seq[SkillEntity] = Seq(
      SkillEntity(UUID.randomUUID(), "Skill1", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill2", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill3", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill4", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill5", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill6", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill7", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill8", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill9", keyWords = skillKeyWords),
      SkillEntity(UUID.randomUUID(), "Skill10", keyWords = skillKeyWords),
    )

    SkillEntity.insertMultiRows(entities)

    val res: Seq[SkillEntity] = SkillEntity.findAll(limit = 7,
      offset = 2,
      orderBy = Name.value,
      sort = DESC.value)

    res.nonEmpty must beTrue
    res.size mustEqual 7
    res mustEqual entities.sortBy(_.name).reverse.slice(2, entities.size - 1)
  }

  "Skill update successfully" in new AutoRollback {
    val anotherKeyWords: Seq[SkillKeyWordEntity] = Seq(
      SkillKeyWordEntity(UUID.randomUUID(), "keyWord6"),
      SkillKeyWordEntity(UUID.randomUUID(), "keyWord7"),
      SkillKeyWordEntity(UUID.randomUUID(), "keyWord8"),
      SkillKeyWordEntity(UUID.randomUUID(), "keyWord9"),
    )
    SkillKeyWordEntity.insertMultiRows(anotherKeyWords)

    val entity: SkillEntity = SkillEntity(
      id = UUID.randomUUID(),
      name = "Skill1",
      keyWords = skillKeyWords
    )
    SkillEntity.insert(entity)

    val entityToUpdate: SkillEntity = SkillEntity(
      id = entity.id,
      name = "Skill2",
      keyWords = anotherKeyWords
    )

    SkillEntity.update(entityToUpdate)
    val res: Option[SkillEntity] = SkillEntity.findById(entity.id)

    res.isDefined must beTrue
    res.get mustEqual entityToUpdate
  }

  "Skill delete successfully" in new AutoRollback {
    val entity: SkillEntity = SkillEntity(
      id = UUID.randomUUID(),
      name = "Skill1",
      keyWords = skillKeyWords
    )
    SkillEntity.insert(entity)
    SkillEntity.deleteById(entity.id)

    val res: Option[SkillEntity] = SkillEntity.findById(entity.id)
    res.isEmpty must beTrue
    res.isDefined must beFalse
  }
}
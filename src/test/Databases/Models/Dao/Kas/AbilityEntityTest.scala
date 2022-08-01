package Databases.Models.Dao.Kas

import Databases.Configurations.{DESC, Name}
import Databases.Models.Dao.Keywords.AbilityKeyWordEntity
import Databases.Models.IBeforeAfterAllDBInit
import scalikejdbc.NamedDB
import scalikejdbc.specs2.mutable.AutoRollback

import java.sql.SQLException
import java.util.UUID

object AbilityEntityTest extends IBeforeAfterAllDBInit {
  val abilityKeyWords: Seq[AbilityKeyWordEntity] = Seq(
    AbilityKeyWordEntity(UUID.randomUUID(), "keyWord1"),
    AbilityKeyWordEntity(UUID.randomUUID(), "keyWord2"),
    AbilityKeyWordEntity(UUID.randomUUID(), "keyWord3"),
    AbilityKeyWordEntity(UUID.randomUUID(), "keyWord4"),
  )

  override def beforeAll(): Unit = {
    super.beforeAll()

    NamedDB(DBName) localTx { implicit session =>
      AbilityKeyWordEntity.insertMultiRows(abilityKeyWords)
    }
  }

  override def afterAll(): Unit = {
    NamedDB(DBName) localTx { implicit session =>
      abilityKeyWords.foreach(word => AbilityKeyWordEntity.deleteById(word.id))
    }

    super.afterAll()
  }

  sequential

  "Ability successfully created" in new AutoRollback {
    val entity: AbilityEntity = AbilityEntity(
      id = UUID.randomUUID(),
      name = "Ability1",
      keyWords = abilityKeyWords
    )

    AbilityEntity.insert(entity)
    val res: Option[AbilityEntity] = AbilityEntity.findById(entity.id)

    res.isDefined must beTrue
    res.get mustEqual entity
  }

  "Ability not created because, such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val entity: AbilityEntity = AbilityEntity(
      id = id,
      name = "Ability1",
      keyWords = abilityKeyWords
    )
    val entityDuplicate: AbilityEntity = AbilityEntity(
      id = id,
      name = "Ability2",
      keyWords = abilityKeyWords
    )

    AbilityEntity.insert(entity)
    AbilityEntity.insert(entityDuplicate) must throwA[SQLException]
  }

  "Ability not created because, such name exists" in new AutoRollback {
    val entity: AbilityEntity = AbilityEntity(
      id = UUID.randomUUID(),
      name = "Ability1",
      keyWords = abilityKeyWords
    )
    val entityDuplicate: AbilityEntity = AbilityEntity(
      id = UUID.randomUUID(),
      name = "Ability1",
      keyWords = abilityKeyWords
    )

    AbilityEntity.insert(entity)
    AbilityEntity.insert(entityDuplicate) must throwA[SQLException]
  }

  "Abilities successfully created" in new AutoRollback {
    val entities: Seq[AbilityEntity] = Seq(
      AbilityEntity(UUID.randomUUID(), "Ability1", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability2", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability3", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability4", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability5", keyWords = abilityKeyWords),
    )

    AbilityEntity.insertMultiRows(entities)
    val res: Seq[AbilityEntity] = AbilityEntity.findAll()

    res.nonEmpty must beTrue
    res mustEqual entities.sortBy(_.id.toString)
  }

  "Abilities not create, because such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val entities: Seq[AbilityEntity] = Seq(
      AbilityEntity(UUID.randomUUID(), "Ability1", keyWords = abilityKeyWords),
      AbilityEntity(id, "Ability2", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability3", keyWords = abilityKeyWords),
      AbilityEntity(id, "Ability4", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability5", keyWords = abilityKeyWords),
    )

    AbilityEntity.insertMultiRows(entities) must throwA[SQLException]
  }

  "Abilities not create, because such name exists" in new AutoRollback {
    val name = "Ability1"

    val entities: Seq[AbilityEntity] = Seq(
      AbilityEntity(UUID.randomUUID(), name, keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability2", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), name, keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability4", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), name, keyWords = abilityKeyWords),
    )

    AbilityEntity.insertMultiRows(entities) must throwA[SQLException]
  }

  "select Ability from table by id" in new AutoRollback {
    val entity: AbilityEntity = AbilityEntity(
      id = UUID.randomUUID(),
      name = "Ability1",
      keyWords = abilityKeyWords
    )
    AbilityEntity.insert(entity)

    val res: Option[AbilityEntity] = AbilityEntity.findById(entity.id)
    res.isDefined must beTrue
    res.get mustEqual entity
  }

  "empty select, because record missing" in new AutoRollback {
    val res: Option[AbilityEntity] = AbilityEntity.findById(UUID.randomUUID())

    res.isEmpty must beTrue
    res.isDefined must beFalse
  }

  "select all Abilities without parameters" in new AutoRollback {
    val entities: Seq[AbilityEntity] = Seq(
      AbilityEntity(UUID.randomUUID(), "Ability1", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability2", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability3", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability4", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability5", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability6", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability7", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability8", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability9", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability10", keyWords = abilityKeyWords),
    )

    AbilityEntity.insertMultiRows(entities)
    val res: Seq[AbilityEntity] = AbilityEntity.findAll()

    res.nonEmpty must beTrue
    res mustEqual entities.sortBy(_.id.toString)
  }

  "select all Abilities with limit" in new AutoRollback {
    val entities: Seq[AbilityEntity] = Seq(
      AbilityEntity(UUID.randomUUID(), "Ability1", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability2", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability3", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability4", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability5", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability6", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability7", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability8", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability9", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability10", keyWords = abilityKeyWords),
    )

    AbilityEntity.insertMultiRows(entities)
    val res: Seq[AbilityEntity] = AbilityEntity.findAll(limit = 5)

    res.nonEmpty must beTrue
    res.size mustEqual 5
    res mustEqual entities.sortBy(_.id.toString).take(5)
  }

  "select all Abilities with all parameters" in new AutoRollback {
    val entities: Seq[AbilityEntity] = Seq(
      AbilityEntity(UUID.randomUUID(), "Ability1", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability2", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability3", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability4", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability5", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability6", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability7", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability8", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability9", keyWords = abilityKeyWords),
      AbilityEntity(UUID.randomUUID(), "Ability10", keyWords = abilityKeyWords),
    )

    AbilityEntity.insertMultiRows(entities)

    val res: Seq[AbilityEntity] = AbilityEntity.findAll(limit = 7,
      offset = 2,
      orderBy = Name.value,
      sort = DESC.value)

    res.nonEmpty must beTrue
    res.size mustEqual 7
    res mustEqual entities.sortBy(_.name).reverse.slice(2, entities.size - 1)
  }

  "Ability update successfully" in new AutoRollback {
    val anotherKeyWords: Seq[AbilityKeyWordEntity] = Seq(
      AbilityKeyWordEntity(UUID.randomUUID(), "keyWord6"),
      AbilityKeyWordEntity(UUID.randomUUID(), "keyWord7"),
      AbilityKeyWordEntity(UUID.randomUUID(), "keyWord8"),
      AbilityKeyWordEntity(UUID.randomUUID(), "keyWord9"),
    )
    AbilityKeyWordEntity.insertMultiRows(anotherKeyWords)

    val entity: AbilityEntity = AbilityEntity(
      id = UUID.randomUUID(),
      name = "Ability1",
      keyWords = abilityKeyWords
    )
    AbilityEntity.insert(entity)

    val entityToUpdate: AbilityEntity = AbilityEntity(
      id = entity.id,
      name = "Ability2",
      keyWords = anotherKeyWords
    )

    AbilityEntity.update(entityToUpdate)
    val res: Option[AbilityEntity] = AbilityEntity.findById(entity.id)

    res.isDefined must beTrue
    res.get mustEqual entityToUpdate
  }

  "Ability delete successfully" in new AutoRollback {
    val entity: AbilityEntity = AbilityEntity(
      id = UUID.randomUUID(),
      name = "Ability1",
      keyWords = abilityKeyWords
    )
    AbilityEntity.insert(entity)
    AbilityEntity.deleteById(entity.id)

    val res: Option[AbilityEntity] = AbilityEntity.findById(entity.id)
    res.isEmpty must beTrue
    res.isDefined must beFalse
  }
}
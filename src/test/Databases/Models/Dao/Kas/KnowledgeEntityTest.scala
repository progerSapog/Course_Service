package Databases.Models.Dao.Kas

import Databases.Configurations.{DESC, Name}
import Databases.Models.Dao.Keywords.KnowledgeKeyWordEntity
import Databases.Models.IBeforeAfterAllDBInit
import scalikejdbc.NamedDB
import scalikejdbc.specs2.mutable.AutoRollback

import java.sql.SQLException
import java.util.UUID

object KnowledgeEntityTest extends IBeforeAfterAllDBInit {
  val knowledgeKeyWords: Seq[KnowledgeKeyWordEntity] = Seq(
    KnowledgeKeyWordEntity(UUID.randomUUID(), "keyWord1"),
    KnowledgeKeyWordEntity(UUID.randomUUID(), "keyWord2"),
    KnowledgeKeyWordEntity(UUID.randomUUID(), "keyWord3"),
    KnowledgeKeyWordEntity(UUID.randomUUID(), "keyWord4"),
  )

  override def beforeAll(): Unit = {
    super.beforeAll()

    NamedDB(DBName) localTx { implicit session =>
      KnowledgeKeyWordEntity.insertMultiRows(knowledgeKeyWords)
    }
  }

  override def afterAll(): Unit = {
    NamedDB(DBName) localTx { implicit session =>
      knowledgeKeyWords.foreach(word => KnowledgeKeyWordEntity.deleteById(word.id))
    }

    super.afterAll()
  }

  sequential

  "Knowledge successfully created" in new AutoRollback {
    val entity: KnowledgeEntity = KnowledgeEntity(
      id = UUID.randomUUID(),
      name = "Knowledge1",
      keyWords = knowledgeKeyWords
    )

    KnowledgeEntity.insert(entity)
    val res: Option[KnowledgeEntity] = KnowledgeEntity.findById(entity.id)

    res.isDefined must beTrue
    res.get mustEqual entity
  }

  "Knowledge not created because, such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val entity: KnowledgeEntity = KnowledgeEntity(
      id = id,
      name = "Knowledge1",
      keyWords = knowledgeKeyWords
    )
    val entityDuplicate: KnowledgeEntity = KnowledgeEntity(
      id = id,
      name = "Knowledge2",
      keyWords = knowledgeKeyWords
    )

    KnowledgeEntity.insert(entity)
    KnowledgeEntity.insert(entityDuplicate) must throwA[SQLException]
  }

  "Knowledge not created because, such name exists" in new AutoRollback {
    val entity: KnowledgeEntity = KnowledgeEntity(
      id = UUID.randomUUID(),
      name = "Knowledge1",
      keyWords = knowledgeKeyWords
    )
    val entityDuplicate: KnowledgeEntity = KnowledgeEntity(
      id = UUID.randomUUID(),
      name = "Knowledge1",
      keyWords = knowledgeKeyWords
    )

    KnowledgeEntity.insert(entity)
    KnowledgeEntity.insert(entityDuplicate) must throwA[SQLException]
  }

  "Knowledge successfully created" in new AutoRollback {
    val entities: Seq[KnowledgeEntity] = Seq(
      KnowledgeEntity(UUID.randomUUID(), "Knowledge1", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge2", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge3", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge4", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge5", keyWords = knowledgeKeyWords),
    )

    KnowledgeEntity.insertMultiRows(entities)
    val res: Seq[KnowledgeEntity] = KnowledgeEntity.findAll()

    res.nonEmpty must beTrue
    res mustEqual entities.sortBy(_.id.toString)
  }

  "Knowledge not create, because such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val entities: Seq[KnowledgeEntity] = Seq(
      KnowledgeEntity(UUID.randomUUID(), "Knowledge1", keyWords = knowledgeKeyWords),
      KnowledgeEntity(id, "Knowledge2", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge3", keyWords = knowledgeKeyWords),
      KnowledgeEntity(id, "Knowledge4", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge5", keyWords = knowledgeKeyWords),
    )

    KnowledgeEntity.insertMultiRows(entities) must throwA[SQLException]
  }

  "Knowledge not create, because such name exists" in new AutoRollback {
    val name = "Knowledge1"

    val entities: Seq[KnowledgeEntity] = Seq(
      KnowledgeEntity(UUID.randomUUID(), name, keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge2", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), name, keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge4", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), name, keyWords = knowledgeKeyWords),
    )

    KnowledgeEntity.insertMultiRows(entities) must throwA[SQLException]
  }

  "select Knowledge from table by id" in new AutoRollback {
    val entity: KnowledgeEntity = KnowledgeEntity(
      id = UUID.randomUUID(),
      name = "Knowledge1",
      keyWords = knowledgeKeyWords
    )
    KnowledgeEntity.insert(entity)

    val res: Option[KnowledgeEntity] = KnowledgeEntity.findById(entity.id)
    res.isDefined must beTrue
    res.get mustEqual entity
  }

  "empty select, because record missing" in new AutoRollback {
    val res: Option[KnowledgeEntity] = KnowledgeEntity.findById(UUID.randomUUID())

    res.isEmpty must beTrue
    res.isDefined must beFalse
  }

  "select all Knowledge without parameters" in new AutoRollback {
    val entities: Seq[KnowledgeEntity] = Seq(
      KnowledgeEntity(UUID.randomUUID(), "Knowledge1", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge2", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge3", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge4", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge5", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge6", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge7", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge8", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge9", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge10", keyWords = knowledgeKeyWords),
    )

    KnowledgeEntity.insertMultiRows(entities)
    val res: Seq[KnowledgeEntity] = KnowledgeEntity.findAll()

    res.nonEmpty must beTrue
    res mustEqual entities.sortBy(_.id.toString)
  }

  "select all Knowledge with limit" in new AutoRollback {
    val entities: Seq[KnowledgeEntity] = Seq(
      KnowledgeEntity(UUID.randomUUID(), "Knowledge1", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge2", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge3", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge4", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge5", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge6", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge7", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge8", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge9", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge10", keyWords = knowledgeKeyWords),
    )

    KnowledgeEntity.insertMultiRows(entities)
    val res: Seq[KnowledgeEntity] = KnowledgeEntity.findAll(limit = 5)

    res.nonEmpty must beTrue
    res.size mustEqual 5
    res mustEqual entities.sortBy(_.id.toString).take(5)
  }

  "select all Knowledge with all parameters" in new AutoRollback {
    val entities: Seq[KnowledgeEntity] = Seq(
      KnowledgeEntity(UUID.randomUUID(), "Knowledge1", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge2", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge3", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge4", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge5", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge6", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge7", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge8", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge9", keyWords = knowledgeKeyWords),
      KnowledgeEntity(UUID.randomUUID(), "Knowledge10", keyWords = knowledgeKeyWords),
    )

    KnowledgeEntity.insertMultiRows(entities)

    val res: Seq[KnowledgeEntity] = KnowledgeEntity.findAll(limit = 7,
      offset = 2,
      orderBy = Name.value,
      sort = DESC.value)

    res.nonEmpty must beTrue
    res.size mustEqual 7
    res mustEqual entities.sortBy(_.name).reverse.slice(2, entities.size - 1)
  }

  "Knowledge update successfully" in new AutoRollback {
    val anotherKeyWords: Seq[KnowledgeKeyWordEntity] = Seq(
      KnowledgeKeyWordEntity(UUID.randomUUID(), "keyWord6"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "keyWord7"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "keyWord8"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "keyWord9"),
    )
    KnowledgeKeyWordEntity.insertMultiRows(anotherKeyWords)

    val entity: KnowledgeEntity = KnowledgeEntity(
      id = UUID.randomUUID(),
      name = "knowledge1",
      keyWords = knowledgeKeyWords
    )
    KnowledgeEntity.insert(entity)

    val entityToUpdate: KnowledgeEntity = KnowledgeEntity(
      id = entity.id,
      name = "knowledge2",
      keyWords = anotherKeyWords
    )

    KnowledgeEntity.update(entityToUpdate)
    val res: Option[KnowledgeEntity] = KnowledgeEntity.findById(entity.id)

    res.isDefined must beTrue
    res.get mustEqual entityToUpdate
  }

  "Knowledge delete successfully" in new AutoRollback {
    val entity: KnowledgeEntity = KnowledgeEntity(
      id = UUID.randomUUID(),
      name = "Knowledge1",
      keyWords = knowledgeKeyWords
    )
    KnowledgeEntity.insert(entity)
    KnowledgeEntity.deleteById(entity.id)

    val res: Option[KnowledgeEntity] = KnowledgeEntity.findById(entity.id)
    res.isEmpty must beTrue
    res.isDefined must beFalse
  }
}
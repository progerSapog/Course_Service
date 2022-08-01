package Databases.Models.Dao.Keywords

import Databases.Configurations.{DESC, Name}
import Databases.Models.IBeforeAfterAllDBInit
import scalikejdbc.config.DBs
import scalikejdbc.specs2.mutable.AutoRollback

import java.sql.SQLException
import java.util.UUID

object SkillKeyWordEntityTest extends IBeforeAfterAllDBInit {

  sequential
  DBs.setup("default")

  "SkillKeyWord successfully created" in new AutoRollback {
    val keyWord: SkillKeyWordEntity = SkillKeyWordEntity(
      id = UUID.randomUUID(),
      name = "SkillKeyWordTest1"
    )

    SkillKeyWordEntity.insert(keyWord)
    val res: Option[SkillKeyWordEntity] = SkillKeyWordEntity.findById(keyWord.id)

    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWord)
  }

  "SkillKeyWord not created because, such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val keyWord: SkillKeyWordEntity = SkillKeyWordEntity(
      id = id,
      name = "SkillKeyword1"
    )
    val keyWordDuplicate: SkillKeyWordEntity = SkillKeyWordEntity(
      id = id,
      name = "SkillKeyword2"
    )

    SkillKeyWordEntity.insert(keyWord)
    SkillKeyWordEntity.insert(keyWordDuplicate) must throwA[SQLException]
  }

  "SkillKeyWord not created because, such name exists" in new AutoRollback {
    val keyWord: SkillKeyWordEntity = SkillKeyWordEntity(
      id = UUID.randomUUID(),
      name = "SkillKeyword1"
    )
    val keyWordDuplicate: SkillKeyWordEntity = SkillKeyWordEntity(
      id = UUID.randomUUID(),
      name = "SkillKeyword1"
    )

    SkillKeyWordEntity.insert(keyWord)
    SkillKeyWordEntity.insert(keyWordDuplicate) must throwA[SQLException]
  }

  "SkillKeyWord successfully created" in new AutoRollback {
    val keyWords: Seq[SkillKeyWordEntity] = Seq(
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword1"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword2"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword3"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword4"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword5"),
    )

    SkillKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[SkillKeyWordEntity] = SkillKeyWordEntity.findAll()

    assert(res.nonEmpty must beTrue)
    assert(res mustEqual keyWords.sortBy(_.id.toString))
  }

  "SkillKeyWord not created, because such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val keyWords: Seq[SkillKeyWordEntity] = Seq(
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword1"),
      SkillKeyWordEntity(id, "SkillKeyword2"),
      SkillKeyWordEntity(UUID.randomUUID(), "SKillKeyword3"),
      SkillKeyWordEntity(id, "SkillKeyword4"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword5"),
    )

    SkillKeyWordEntity.insertMultiRows(keyWords) must throwA[SQLException]
  }

  "SkillKeyWord not created, because such name exists" in new AutoRollback {
    val name = "SkillKeyword1"

    val keyWords: Seq[SkillKeyWordEntity] = Seq(
      SkillKeyWordEntity(UUID.randomUUID(), name),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword2"),
      SkillKeyWordEntity(UUID.randomUUID(), name),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword4"),
      SkillKeyWordEntity(UUID.randomUUID(), name),
    )

    SkillKeyWordEntity.insertMultiRows(keyWords) must throwA[SQLException]
  }

  "select SkillKeyWord from table by id" in new AutoRollback {
    val keyWord: SkillKeyWordEntity = SkillKeyWordEntity(
      id = UUID.randomUUID(),
      name = "SkillKeyWord1"
    )
    SkillKeyWordEntity.insert(keyWord)

    val res: Option[SkillKeyWordEntity] = SkillKeyWordEntity.findById(keyWord.id)
    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWord)
  }

  "empty select, because record missing" in new AutoRollback {
    val res: Option[SkillKeyWordEntity] = SkillKeyWordEntity.findById(UUID.randomUUID())

    assert(res.isEmpty must beTrue)
    assert(res.isDefined must beFalse)
  }

  "select all SkillKeyWord without parameters" in new AutoRollback {
    val keyWords: Seq[SkillKeyWordEntity] = Seq(
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword1"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword2"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword3"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword4"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword5"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword6"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword7"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword8"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword9"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword10"),
    )

    SkillKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[SkillKeyWordEntity] = SkillKeyWordEntity.findAll()

    assert(res.nonEmpty must beTrue)
    assert(res mustEqual keyWords.sortBy(_.id.toString))
  }

  "select all SkillKeyWord with limit" in new AutoRollback {
    val keyWords: Seq[SkillKeyWordEntity] = Seq(
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword1"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword2"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword3"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword4"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword5"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword6"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword7"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword8"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword9"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword10"),
    )

    SkillKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[SkillKeyWordEntity] = SkillKeyWordEntity.findAll(limit = 5)

    assert(res.nonEmpty must beTrue)
    assert(res.size mustEqual 5)
    assert(res mustEqual keyWords.sortBy(_.id.toString).take(5))
  }

  "select all SkillKeyWord with all parameters" in new AutoRollback {
    val keyWords: Seq[SkillKeyWordEntity] = Seq(
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword1"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword2"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword3"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword4"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword5"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword6"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword7"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword8"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword9"),
      SkillKeyWordEntity(UUID.randomUUID(), "SkillKeyword10"),
    )

    SkillKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[SkillKeyWordEntity] = SkillKeyWordEntity.findAll(limit = 7,
      offset = 2,
      orderBy = Name.value,
      sort = DESC.value)

    assert(res.nonEmpty must beTrue)
    assert(res.size mustEqual 7)
    assert(res mustEqual keyWords.sortBy(_.name).reverse.slice(2, keyWords.size - 1))
  }

  "SkillKeyWord update successfully" in new AutoRollback {
    val keyWord: SkillKeyWordEntity = SkillKeyWordEntity(
      id = UUID.randomUUID(),
      name = "SkillKeyWord1"
    )
    SkillKeyWordEntity.insert(keyWord)

    val keyWordToUpdate: SkillKeyWordEntity = SkillKeyWordEntity(
      id = keyWord.id,
      name = "NOTSkillKeyWord1"
    )
    SkillKeyWordEntity.update(keyWordToUpdate)

    val res: Option[SkillKeyWordEntity] = SkillKeyWordEntity.findById(keyWord.id)

    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWordToUpdate)
  }

  "SkillKeyWord delete successfully" in new AutoRollback {
    val keyWord: SkillKeyWordEntity = SkillKeyWordEntity(
      id = UUID.randomUUID(),
      name = "SKillKeyWord1"
    )

    SkillKeyWordEntity.insert(keyWord)
    SkillKeyWordEntity.deleteById(keyWord.id)

    val res: Option[SkillKeyWordEntity] = SkillKeyWordEntity.findById(keyWord.id)

    assert(res.isEmpty must beTrue)
    assert(res.isDefined must beFalse)
  }
}

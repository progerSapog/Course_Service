package Databases.Models.Dao

import Databases.Configurations.{DESC, Name}
import org.specs2.mutable.Specification
import scalikejdbc.config.DBs
import scalikejdbc.specs2.mutable.AutoRollback

import java.sql.SQLException
import java.util.UUID

object AbilityKeyWordEntityTest extends Specification {

  sequential
  DBs.setup("default")

  "AbilityKeyWord successfully created" in new AutoRollback {
    val keyWord: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = UUID.randomUUID(),
      name = "AbilityKeyWordTest1"
    )

    AbilityKeyWordEntity.insert(keyWord)
    val res: Option[AbilityKeyWordEntity] = AbilityKeyWordEntity.findById(keyWord.id)

    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWord)
  }

  "AbilityKeyWord not created because, such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val keyWord: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = id,
      name = "AbilityKeyword1"
    )
    val keyWordDuplicate: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = id,
      name = "AbilityKeyword2"
    )

    AbilityKeyWordEntity.insert(keyWord)
    AbilityKeyWordEntity.insert(keyWordDuplicate) must throwA[SQLException]
  }

  "AbilityKeyWord not created because, such name exists" in new AutoRollback {
    val keyWord: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = UUID.randomUUID(),
      name = "AbilityKeyword1"
    )
    val keyWordDuplicate: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = UUID.randomUUID(),
      name = "AbilityKeyword1"
    )

    AbilityKeyWordEntity.insert(keyWord)
    AbilityKeyWordEntity.insert(keyWordDuplicate) must throwA[SQLException]
  }

  "AbilityKeyWords successfully created" in new AutoRollback {
    val keyWords: Seq[AbilityKeyWordEntity] = Seq(
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword2"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword3"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword4"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword5"),
    )

    AbilityKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[AbilityKeyWordEntity] = AbilityKeyWordEntity.findAll()

    assert(res.nonEmpty must beTrue)
    assert(res mustEqual keyWords.sortBy(_.id.toString))
  }

  "AbilityKeyWords not created, because such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val keyWords: Seq[AbilityKeyWordEntity] = Seq(
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
      AbilityKeyWordEntity(id, "AbilityKeyword2"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword3"),
      AbilityKeyWordEntity(id, "AbilityKeyword4"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword5"),
    )

    AbilityKeyWordEntity.insertMultiRows(keyWords) must throwA[SQLException]
  }

  "AbilityKeyWords not created, because such name exists" in new AutoRollback {
    val keyWords: Seq[AbilityKeyWordEntity] = Seq(
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword2"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword4"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
    )

    AbilityKeyWordEntity.insertMultiRows(keyWords) must throwA[SQLException]
  }

  "select AbilityKeyWords from table by id" in new AutoRollback {
    val keyWord: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = UUID.randomUUID(),
      name = "AbilityKeyWord1"
    )
    AbilityKeyWordEntity.insert(keyWord)

    val res: Option[AbilityKeyWordEntity] = AbilityKeyWordEntity.findById(keyWord.id)
    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWord)
  }

  "empty select, because record missing" in new AutoRollback {
    val res: Option[AbilityKeyWordEntity] = AbilityKeyWordEntity.findById(UUID.randomUUID())

    assert(res.isEmpty must beTrue)
    assert(res.isDefined must beFalse)
  }

  "select all AbilityKeyWords without parameters" in new AutoRollback {
    val keyWords: Seq[AbilityKeyWordEntity] = Seq(
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword2"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword3"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword4"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword5"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword6"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword7"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword8"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword9"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword10"),
    )

    AbilityKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[AbilityKeyWordEntity] = AbilityKeyWordEntity.findAll()

    assert(res.nonEmpty must beTrue)
    assert(res mustEqual keyWords.sortBy(_.id.toString))
  }

  "select all AbilityKeyWords with limit" in new AutoRollback {
    val keyWords: Seq[AbilityKeyWordEntity] = Seq(
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword2"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword3"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword4"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword5"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword6"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword7"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword8"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword9"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword10"),
    )

    AbilityKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[AbilityKeyWordEntity] = AbilityKeyWordEntity.findAll(limit = 5)

    assert(res.nonEmpty must beTrue)
    assert(res.size mustEqual 5)
    assert(res mustEqual keyWords.sortBy(_.id.toString).take(5))
  }

  "select all AbilityKeyWords with all parameters" in new AutoRollback {
    val keyWords: Seq[AbilityKeyWordEntity] = Seq(
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword2"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword3"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword4"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword5"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword6"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword7"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword8"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword9"),
      AbilityKeyWordEntity(UUID.randomUUID(), "AbilityKeyword10"),
    )

    AbilityKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[AbilityKeyWordEntity] = AbilityKeyWordEntity.findAll(limit = 7,
      offset = 2,
      orderBy = Name.value,
      sort = DESC.value)

    assert(res.nonEmpty must beTrue)
    assert(res.size mustEqual 7)
    assert(res mustEqual keyWords.sortBy(_.name).reverse.slice(2, keyWords.size - 1))
  }

  "AbilityKeyWords update successfully" in new AutoRollback {
    val keyWord: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = UUID.randomUUID(),
      name = "AbilityKeyWord1"
    )
    AbilityKeyWordEntity.insert(keyWord)

    val keyWordToUpdate: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = keyWord.id,
      name = "NOTAbilityKeyWord1"
    )
    AbilityKeyWordEntity.update(keyWordToUpdate)

    val res: Option[AbilityKeyWordEntity] = AbilityKeyWordEntity.findById(keyWord.id)

    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWordToUpdate)
  }

  "AbilityKeyWords delete successfully" in new AutoRollback {
    val keyWord: AbilityKeyWordEntity = AbilityKeyWordEntity(
      id = UUID.randomUUID(),
      name = "AbilityKeyWord1"
    )

    AbilityKeyWordEntity.insert(keyWord)
    AbilityKeyWordEntity.deleteById(keyWord.id)

    val res: Option[AbilityKeyWordEntity] = AbilityKeyWordEntity.findById(keyWord.id)

    assert(res.isEmpty must beTrue)
    assert(res.isDefined must beFalse)
  }
}

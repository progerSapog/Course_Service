package Databases.Models.Dao

import Databases.Configurations.{DESC, Name}
import org.specs2.mutable.Specification
import scalikejdbc.config.DBs
import scalikejdbc.specs2.mutable.AutoRollback

import java.sql.SQLException
import java.util.UUID

object KnowledgeKeyWordEntityTest extends Specification {

  sequential
  DBs.setup("default")

  "KnowledgeKeyWord successfully created" in new AutoRollback {
    val keyWord: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = UUID.randomUUID(),
      name = "KnowledgeKeyWord1"
    )

    KnowledgeKeyWordEntity.insert(keyWord)
    val res: Option[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findById(keyWord.id)

    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWord)
  }

  "KnowledgeKeyWord not created because, such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val keyWord: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = id,
      name = "KnowledgeKeyword1"
    )
    val keyWordDuplicate: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = id,
      name = "KnowledgeKeyword2"
    )

    KnowledgeKeyWordEntity.insert(keyWord)
    KnowledgeKeyWordEntity.insert(keyWordDuplicate) must throwA[SQLException]
  }

  "KnowledgeKeyWord not created because, such name exists" in new AutoRollback {
    val keyWord: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = UUID.randomUUID(),
      name = "KnowledgeKeyword1"
    )
    val keyWordDuplicate: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = UUID.randomUUID(),
      name = "KnowledgeKeyword1"
    )

    KnowledgeKeyWordEntity.insert(keyWord)
    KnowledgeKeyWordEntity.insert(keyWordDuplicate) must throwA[SQLException]
  }

  "KnowledgeKeyWord successfully created" in new AutoRollback {
    val keyWords: Seq[KnowledgeKeyWordEntity] = Seq(
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword1"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword2"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword3"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword4"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword5"),
    )

    KnowledgeKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findAll()

    assert(res.nonEmpty must beTrue)
    assert(res mustEqual keyWords.sortBy(_.id.toString))
  }

  "KnowledgeKeyWord not created, because such UUID exists" in new AutoRollback {
    val id: UUID = UUID.randomUUID()

    val keyWords: Seq[KnowledgeKeyWordEntity] = Seq(
      KnowledgeKeyWordEntity(UUID.randomUUID(), "AbilityKeyword1"),
      KnowledgeKeyWordEntity(id, "AbilityKeyword2"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "AbilityKeyword3"),
      KnowledgeKeyWordEntity(id, "AbilityKeyword4"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "AbilityKeyword5"),
    )

    KnowledgeKeyWordEntity.insertMultiRows(keyWords) must throwA[SQLException]
  }

  "KnowledgeKeyWord not created, because such name exists" in new AutoRollback {
    val keyWords: Seq[KnowledgeKeyWordEntity] = Seq(
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword1"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword2"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword1"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword4"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword1"),
    )

    KnowledgeKeyWordEntity.insertMultiRows(keyWords) must throwA[SQLException]
  }

  "select KnowledgeKeyWord from table by id" in new AutoRollback {
    val keyWord: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = UUID.randomUUID(),
      name = "KnowledgeKeyWord1"
    )
    KnowledgeKeyWordEntity.insert(keyWord)

    val res: Option[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findById(keyWord.id)
    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWord)
  }

  "empty select, because record missing" in new AutoRollback {
    val res: Option[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findById(UUID.randomUUID())

    assert(res.isEmpty must beTrue)
    assert(res.isDefined must beFalse)
  }

  "select all KnowledgeKeyWord without parameters" in new AutoRollback {
    val keyWords: Seq[KnowledgeKeyWordEntity] = Seq(
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword1"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword2"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword3"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword4"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword5"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword6"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword7"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword8"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword9"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword10"),
    )

    KnowledgeKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findAll()

    assert(res.nonEmpty must beTrue)
    assert(res mustEqual keyWords.sortBy(_.id.toString))
  }

  "select all KnowledgeKeyWord with limit" in new AutoRollback {
    val keyWords: Seq[KnowledgeKeyWordEntity] = Seq(
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword1"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword2"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword3"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword4"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword5"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword6"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword7"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword8"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword9"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword10"),
    )

    KnowledgeKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findAll(limit = 5)

    assert(res.nonEmpty must beTrue)
    assert(res.size mustEqual 5)
    assert(res mustEqual keyWords.sortBy(_.id.toString).take(5))
  }

  "select all KnowledgeKeyWord with all parameters" in new AutoRollback {
    val keyWords: Seq[KnowledgeKeyWordEntity] = Seq(
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword1"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword2"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword3"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword4"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword5"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword6"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword7"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword8"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword9"),
      KnowledgeKeyWordEntity(UUID.randomUUID(), "KnowledgeKeyword10"),
    )

    KnowledgeKeyWordEntity.insertMultiRows(keyWords)

    val res: Seq[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findAll(limit = 7,
      offset = 2,
      orderBy = Name.value,
      sort = DESC.value)

    assert(res.nonEmpty must beTrue)
    assert(res.size mustEqual 7)
    assert(res mustEqual keyWords.sortBy(_.name).reverse.slice(2, keyWords.size - 1))
  }

  "KnowledgeKeyWord update successfully" in new AutoRollback {
    val keyWord: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = UUID.randomUUID(),
      name = "KnowledgeKeyWord1"
    )
    KnowledgeKeyWordEntity.insert(keyWord)

    val keyWordToUpdate: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = keyWord.id,
      name = "NOTKnowledgeKeyWord1"
    )
    KnowledgeKeyWordEntity.update(keyWordToUpdate)

    val res: Option[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findById(keyWord.id)

    assert(res.isDefined must beTrue)
    assert(res.get mustEqual keyWordToUpdate)
  }

  "KnowledgeKeyWord delete successfully" in new AutoRollback {
    val keyWord: KnowledgeKeyWordEntity = KnowledgeKeyWordEntity(
      id = UUID.randomUUID(),
      name = "KnowledgeKeyWord1"
    )

    KnowledgeKeyWordEntity.insert(keyWord)
    KnowledgeKeyWordEntity.deleteById(keyWord.id)

    val res: Option[KnowledgeKeyWordEntity] = KnowledgeKeyWordEntity.findById(keyWord.id)

    assert(res.isEmpty must beTrue)
    assert(res.isDefined must beFalse)
  }
}

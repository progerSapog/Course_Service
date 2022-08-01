package Databases.Models

import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll
import scalikejdbc.config.DBs

/**
 * Инициализация базы перед тестами и закрытие соединения после
 */
abstract class IBeforeAfterAllDBInit extends Specification with BeforeAfterAll {
  val DBName = "default"

  override def beforeAll(): Unit = {
    DBs.setup(DBName)
  }

  override def afterAll(): Unit = {
    DBs.close(DBName)
  }
}
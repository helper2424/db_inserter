import java.sql.Connection

import akka.actor.Actor
import models.{Base, User}

import scala.collection.mutable.ArrayBuffer

class InsertAgent(val scoptConfig: ScoptConfig) extends Actor {
  protected var connection: Connection = null

  def receive = {
    case b: QueryParams => {
      var i = 0
      val singleton: Option[Base] = b.model.toLowerCase.trim() match {
        case "user" => Some(User)
        case _ => Some(null)
      }

      if (!singleton.isDefined) {
        println(s"Undefined model ${b.model}")
      } else {
        var valuesBuffer = new ArrayBuffer[String]

        Benchmark.time("Agent work", {
          for (i <- b.minId until b.maxId) {
            valuesBuffer += singleton.get.generate(i)

            if (valuesBuffer.size >= singleton.get.maxValuesPerRequest)
              executeQuery(singleton.get, valuesBuffer)
          }

          if (!valuesBuffer.isEmpty)
            executeQuery(singleton.get, valuesBuffer)
        })

        getConnection.close()
        b.terminator.tell(new TerminationParams(), self)
      }
    }
  }

  def executeQuery(singleton: Base, values: ArrayBuffer[String]): Unit = {
    Benchmark.time("Execute query", {
      val query = s"INSERT INTO ${singleton.table}${singleton.keys} VALUES${values.mkString(",")};"
      //println(query)
      getConnection.createStatement().execute(query)
      values.clear()
    })
  }

  def getConnection: Connection = {
    if (this.connection != null)
      return connection

    this.connection = DB.getConnection(this.scoptConfig.name, this.scoptConfig.user, this.scoptConfig.pass)
    return this.connection
  }
}

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Actor, Props, ActorSystem}
import models.{User, Base}

object Main {
  private var config:ScoptConfig = null
  def main(args: Array[String]) {
    println("Start db inserter")

    initConsoleParams(args)

    println("Remove everything from db")

    if (this.config.clear) {
      val conn = DB.getConnection(this.config.name, this.config.user, this.config.pass)

      val singleton: Option[Base] = this.config.model.toLowerCase.trim() match {
        case "user" => Some(User)
        case _ => Some(null)
      }

      conn.createStatement.execute(s"Delete from ${singleton.get.table};")
      conn.close()
    }

    val count = this.config.records
    var actorsCount = this.config.actors
    if (actorsCount <= 0)
       actorsCount = 32

    val actorSystem = ActorSystem("my_system")
    val terminator = actorSystem.actorOf(Props(new TerminatorAgent(actorsCount)))
    val actors:Array[ActorRef] = new Array[ActorRef](actorsCount)

    Benchmark.init

    var i = 0
    val diff:Int = count / actorsCount;
    for (i <- 0 until actorsCount) {
      actors.update(i, actorSystem.actorOf(Props(new InsertAgent(this.config))))
      actors(i).tell(new QueryParams(i * diff, math.min((i + 1) * diff, count), this.config.model, terminator), null)
    }

    Benchmark.time("Full time", { while(!actorSystem.whenTerminated.isCompleted) Thread.sleep(1000) } )
    println("Stop db inserter")
    println(Benchmark.report)
    Benchmark.stop
  }

  def initConsoleParams(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[ScoptConfig]("scopt") {
      head("DB inserter")

      opt[Boolean]('c', "clear").optional().action((x, c) =>
        c.copy(clear = x)).text("Clear database before inserting")

      opt[String]('u', "user").required().action((x, c) =>
        c.copy(user = x)).text("Database user")

      opt[String]('p', "pass").required().action((x, c) =>
        c.copy(pass = x)).text("Database password")

      opt[String]('n', "name").required().action((x, c) =>
        c.copy(name = x)).text("Database name")

      opt[Int]('a', "actors").required().action((x, c) =>
        c.copy(actors = x)).text("Actors(threads) count")

      opt[Int]('r', "records").required().action((x, c) =>
        c.copy(records = x)).text("Records count(How much records will inserted)")

      opt[String]('m', "model").required().action((x, c) =>
        c.copy(model = x)).text("Model name")
    }

    parser.parse(args, ScoptConfig()) match {
      case Some(config) =>
        this.config = config
      case None =>
        println("Some problem with command line parsing")
        System.exit(1)
    }
  }
}

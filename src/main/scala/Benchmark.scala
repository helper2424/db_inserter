import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import akka.util.Timeout

import scala.collection.mutable.{HashMap, MultiMap, Set}
import scala.concurrent.Await
import akka.pattern.ask
import scala.concurrent.duration._

class Benchmark extends Actor {
  private var measures = new HashMap[String, Set[Long]] with MultiMap[String, Long]

  def receive = {
    case b: BenchmarkParams => {
      measures.addBinding(b.key, b.value)
    }
    case _ => {
      var result = new StringBuilder
      for ((k, v) <- measures) {
        if (!v.isEmpty) {
          var max: Long = v.head
          var min: Long = v.head
          var sum: Long = 0
          var med: Float = 0
          v.foreach(i => {
            if (min > i)
              min = i

            if (i > max)
              max = i

            sum += i
          })

          result ++= s"\n\n${k} has results:\n"
          result ++= "\tmin\tmax\tavg\tmed\n"
          result ++= s"${Benchmark.readableTime(min)}\t" +
            s"${Benchmark.readableTime(max)}\t" +
            s"${Benchmark.readableTime(sum / v.size)}\t" +
            s"${Benchmark.readableTime(med)}"
        }
      }
      sender() ! result.toString()
    }
  }
}

object Benchmark {
  private var actorSystem: ActorSystem = null
  private var actor:ActorRef = null

  def init(): Unit = {
    actorSystem = ActorSystem("bench_system")
    actor = actorSystem.actorOf(Props[Benchmark])
  }

  def time[R](key: String, block: => R): R = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    actor.tell(new BenchmarkParams(key, t1 - t0), null)
    result
  }

  def readableTime(value: Float): String = {
    readableTime(value.toLong)
  }

  def readableTime(value: Long): String = {
    value match {
      case i if i < 1000 => s"${value} ns"
      case i if i < 1000000 => s"${value / 1000} mcs"
      case i if i < 1000000000 => s"${value / 1000000} ms"
      case i if i < 1000000000000l => s"${value / 1000000000} s"
      case _ => s"${value / 1000000000000l} h"
    }
  }

  def report: String = {
    implicit val timeout = Timeout(10 seconds)
    return Await.result(actor ? 1, timeout.duration).asInstanceOf[String]
  }

  def stop: Unit = {
    actorSystem.terminate()
  }
}

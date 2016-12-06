import java.sql.Connection

import akka.actor.Actor
import models.{Base, User}

import scala.collection.mutable.ArrayBuffer

class TerminatorAgent(actorsCount:Int) extends Actor {
  protected var terminatedActors = 0

  def receive = {
    case input: TerminationParams => {
      terminatedActors += 1

      if (terminatedActors >= actorsCount)
        context.system.terminate
    }
  }
}

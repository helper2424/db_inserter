import akka.actor.ActorRef

class QueryParams(val minId: Int, val maxId: Int, val model: String, val terminator:ActorRef)

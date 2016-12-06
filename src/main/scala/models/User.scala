package models

object User extends Base {
  val fields:Array[String] = Array("id", "level", "api_type, api_uid")
  def keys: String = {
    s"(${fields.mkString(",")})"
  }

  def table: String = "users"
  def generate(id: Int): String = {
    val values:Array[String] = Array(id.toString,
      scala.util.Random.nextInt(1000).toString,
      scala.util.Random.nextInt(10).toString,
      s"'${id.toString}'")
    s"(${values.mkString(",") })"
  }
  def maxValuesPerRequest:Int = 1000
}

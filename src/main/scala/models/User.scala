package models

object User extends Base {
  val fields:Array[String] = Array("id", "level", "api_type, api_uid", "cohort",
    "csrf_token", "game_level", "game_token", "healths", "hard_cur", "last_sign_in_at", "last_sign_in_ip",
    "session", "soft_cur", "photo")
  def keys: String = {
    s"(${fields.mkString(",")})"
  }

  def table: String = "users"
  def generate(id: Int): String = {
    val values:Array[String] = Array(id.toString, //id
      scala.util.Random.nextInt(1000).toString, //level
      scala.util.Random.nextInt(10).toString, //api_type
      s"'${id.toString}'", //api_uid
      scala.util.Random.nextInt(10).toString, // cohort
      scala.util.Random.nextInt(10).toString,// csrf_token
      scala.util.Random.nextInt(10).toString, // game level
      scala.util.Random.nextInt(10).toString, // game_token
      scala.util.Random.nextInt(10).toString,  // healths
      scala.util.Random.nextInt(10).toString,  // hard_cur
      "'2001-09-28 01:00:00'", "'255.255.255.255'",
      randomString(40), // session
      "0", randomString(40))
    s"(${values.mkString(",") })"
  }
  def maxValuesPerRequest:Int = 1000
  def randomString(symbols:Int) = s"'${randomAlpha(symbols)}'"
  def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      val randomNum = util.Random.nextInt(chars.length)
      sb.append(chars(randomNum))
    }
    sb.toString
  }
  def randomAlpha(length: Int): String = {
    val chars = ('a' to 'z') ++ ('A' to 'Z')
    randomStringFromCharList(length, chars)
  }
}

import java.sql.{DriverManager, Connection}
import java.util.Properties

object DB {
  def getConnection(name:String, user:String, pass:String):Connection = {
    val url = s"jdbc:postgresql:${name}"
    var props: Properties = new Properties()
    props.setProperty("user", user)
    props.setProperty("password", pass)
    DriverManager.getConnection(url, props)
  }
}

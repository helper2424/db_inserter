package models

/**
  * Created by helper2424 on 23.11.16.
  */
trait Base {
  def keys:String
  def table:String
  def generate(id:Int):String
  def maxValuesPerRequest: Int
}

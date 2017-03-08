package services

import model.{Login, User}

import scala.collection.mutable.ListBuffer


trait ReadWrite {

  val userList: ListBuffer[String]

  def read(user: Login): String

  def write(user: User): String

  def accessUser(username: String): User

}

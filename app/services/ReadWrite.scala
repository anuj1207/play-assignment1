package services

import model.{Login, User}

import scala.collection.mutable.ListBuffer


trait ReadWrite {

  def read(user: Login): String

  def write(user: User): String

  def accessUser(username: String): User

  def getUserList:List[model.User]

  def resetUser(username: String, value: Boolean): Boolean

}

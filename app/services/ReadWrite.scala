package services

import model.{Login, User}


trait ReadWrite {

  def read(user: Login): String

  def write(user: User): String

  def accessUser(username: String): User

}

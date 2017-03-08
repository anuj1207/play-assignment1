package services

import javax.inject.Inject

import model.{Login, User}
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.CacheApi

import scala.collection.mutable.ListBuffer


class ReadWriteCache @Inject() (cache: CacheApi) extends ReadWrite{

  override val userList: ListBuffer[String] = ListBuffer[String]()

  override def write(newUser: User): String = {
    val user: Option[User] = cache.get[User](newUser.username)
    user match {
      case Some(_) => throw new Exception("User Already Exists")
      case None =>
        cache.set(newUser.username,newUser)
        newUser.username
    }
  }

  override def read(loginUser: Login): String = {
    val user: Option[User] = cache.get[User](loginUser.username)
    user match {
      case Some(x) =>
        println(s"verifying  ${loginUser.password}==${x.password}")
        if(BCrypt.checkpw(loginUser.password, x.password)) {
          if (!x.isRevoked) x.username else throw new Exception("Authorisation revoked")
        }else throw new Exception("Wrong Password")
      case None => throw new Exception("User Not Found")
    }
  }

  override def accessUser(username: String) : User = {
    val user: Option[User] = cache.get[User](username)
    user match {
      case Some(x) => x
      case None => throw new Exception("User Not Found")
    }
  }

}

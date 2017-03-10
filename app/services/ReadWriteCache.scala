package services

import javax.inject.Inject

import model.{Login, User}
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.CacheApi



class ReadWriteCache @Inject() (cache: CacheApi) extends ReadWrite{

  override def write(newUser: User): String = {
    val user: Option[User] = cache.get[User](newUser.username)
    user match {
      case Some(_) => throw new Exception("User Already Exists")
      case None =>
        cache.set(newUser.username,newUser)
        val oldList = cache.get[List[String]]("userList")
        oldList match {
          case Some(list) =>
            val newList = list:+newUser.username
            cache.remove("userList")
            cache.set("userList",newList)
          case None =>
            cache.set("userList", List[String](newUser.username))
        }
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
    val user = cache.get[User](username)
    user match {
      case Some(user) => user
      case None => throw new Exception("User Not Found")
    }
  }

  override def getUserList: List[User] = {
    cache.get[List[String]]("userList") match {
      case Some(usernameList) =>
        usernameList.map(username =>
          cache.get[User](username) match {
            case Some(user) => user
            case None => throw new Exception("User Not Found")
          })
      case None => throw new Exception("No user Right Now")
    }
  }

  override def resetUser(username: String, value: Boolean): Boolean = {
    val user = accessUser(username)
    user match {
      case x: User =>
        cache.set(x.username,x.copy(isRevoked = value))
        true
      case _ => false
    }
  }

}

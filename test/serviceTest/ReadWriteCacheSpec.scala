package serviceTest

import model.{Login, User}
import org.jmock.Mockery
import org.mindrot.jbcrypt.BCrypt
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.cache.CacheApi
import services.ReadWriteCache
import org.mockito.Mockito._


class ReadWriteCacheSpec extends PlaySpec with MockitoSugar {

  "ReadWriteCache should " should {

    val cache = mock[CacheApi]
    val readWriteCache = new ReadWriteCache(cache)
    /*val context = new Mockery
    val mockBCrypt = context.mock(classOf[BCrypt])*/

    "write one user object in empty cache " in {
      val newUser = User("Anuj","","Saxena","anuj1207","1234","9871463958","male",24,List(),true,false)
      when(cache.get[User]("anuj1207")) thenReturn None
      //cache.set("userList",List[String]())
      when(cache.get[List[String]]("userList")) thenReturn None
      readWriteCache.write(newUser) mustBe "anuj1207"
    }

    "write another user object in nonempty cache " in {
      val newUser = User("Anuj","","Saxena","anuj633","1234","9871463958","male",24,List(),true,false)
      when(cache.get[User]("anuj633")) thenReturn None
      //cache.set("userList",List[String]())
      when(cache.get[List[String]]("userList")) thenReturn Some(List("anuj1207"))
      readWriteCache.write(newUser) mustBe "anuj633"
    }

    /*Exception handling not done*/
    /*"return user already exists on entering duplicate username " in {
      val cache = mock[CacheApi]
      val readWriteCache = new ReadWriteCache(cache)
      val newUser = User("Anuj","","Saxena","anuj1207","1234","9871463958","male",24,List(),true,false)
      readWriteCache.write(newUser) must typeCheck Exception("User Already Exists")
      val xx = readWriteCache.write(newUser)
      xx must equal("")
    }*/

    /*Jmocking For BCrypt???*/

    "read user object from username " in {
      val newUser = User("Anuj","","Saxena","anuj633","1234","9871463958","male",24,List(),true,false)
      when(cache.get[User]("anuj633")) thenReturn Some(newUser)
      readWriteCache.accessUser("anuj633") mustBe newUser
    }

    "get list of users from cache " in {
      val newUser = User("Anuj","","Saxena","anuj633","1234","9871463958","male",24,List(),true,false)
      when(cache.get[List[String]]("userList")) thenReturn Some(List[String]("anuj633"))
      when(cache.get[User]("anuj633")) thenReturn Some(newUser)
      readWriteCache.getUserList mustBe List(newUser)
    }

    "change user authorisation " in {
      readWriteCache.resetUser("anuj633",true) mustBe true
    }

  }

}

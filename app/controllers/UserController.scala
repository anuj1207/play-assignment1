package controllers

import javax.inject.Inject

import model.{Login, User}
import play.api.cache.CacheApi
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import sun.security.util.Password


class UserController @Inject() (cache: CacheApi) extends Controller{

  val userForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "midName" -> text,
    "lastName" -> nonEmptyText,
    "username" -> nonEmptyText,
    "password" -> nonEmptyText,
    "mobile" -> nonEmptyText(minLength = 10,maxLength = 10),
    "gender" -> nonEmptyText,
    "age" -> number(min=18,max=75),
    "hobbies" -> list(text)
  )(User.apply)(User.unapply))

  val loginForm = Form(mapping(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText
  )(Login.apply)(Login.unapply))

  val users = scala.collection.mutable.Map[String,User]("anuj1207" ->
    User("Anuj","","Saxena","anuj1207","1234","9871463958","male",24,List()))

  def login = Action{
    Ok(views.html.login("hello"))
  }

  def signUp = Action{
    Ok(views.html.signUp("sign it up"))
  }

  def submitData = Action {
    implicit request =>
      val userData = request.body
      userForm.bindFromRequest.fold(
        formWithErrors => {
          println("forms with errors >>>>>>>>>>>>>>>>>>>>>>>>>."+userData.toString)
          BadRequest(views.html.signUp("Error"))
        },
        userData => {
          println("Form successfully submitted >>>>>>>>>>>>>>>>>>>>>>>>>."+userData.toString)
          val newUser = model.User(userData.firstName,userData.midName,userData.lastName,
            userData.username,userData.password,userData.mobile,userData.gender,userData.age,userData.hobbies)
          if(users.contains(newUser.username)){
            Ok(views.html.signUp("User already exists"))
          }
          else{
            users += newUser.username -> newUser
            println(users)
            cache.set(newUser.username,newUser)
            Ok(views.html.profile(userData)).withSession("connected" -> newUser.username)
          }
        }
      )
  }

  def validate = Action{
    implicit request =>
      val userData = request.body
      loginForm.bindFromRequest.fold(
        formWithErrors => {
          println("forms with errors >>>>>>>>>>>>>>>>>>>>>>>>>."+userData.toString)
          BadRequest(views.html.signUp("Error"))
        },
        userData => {
          println("Form successfully submitted >>>>>>>>>>>>>>>>>>>>>>>>>."+userData.toString)
          val newUser = model.Login(userData.username,userData.password)
          if(validateUser(newUser)){
            cache.set(newUser.username,newUser)
            Ok(views.html.profile(getUser(newUser.username)))
              .withSession("connected" -> newUser.username)
          }
          else{
            Ok(views.html.login("User doesn't exists"))
          }
        }
      )
  }

  private def validateUser(userLogin :Login):Boolean = {
    users.toList.exists(x=>x._1==userLogin.username && x._2.password==userLogin.password)
  }

  private def getUser(username: String):User = {
    users.get(username) match {
      case Some(x) => x
    }
  }

  def logout = Action{
    Ok(views.html.index("")).withNewSession
  }

}

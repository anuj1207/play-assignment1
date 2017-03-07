package controllers

import com.google.inject.Inject
import model.{Login, User}
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import services.ReadWrite



class UserController @Inject() (readWrite: ReadWrite) extends Controller{

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
          try {
            val username = readWrite.write(newUser)
            Redirect(routes.UserController.profile)
              .withSession("connected" -> username)
          }
          catch {
            case ex:Exception => Ok(views.html.signUp(s"${ex.getMessage}"))
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
          val loginUser = model.Login(userData.username,userData.password)
          try {
            val username = readWrite.read(loginUser)
            Redirect(routes.UserController.profile)
              .withSession("connected" -> username)
          }
          catch {
            case ex: Exception => Ok(views.html.login(ex.getMessage))
          }
        }
      )
  }

  def logout = Action { implicit request =>
    Ok(views.html.index("")).withNewSession
  }

  def profile = Action { request =>
    println("we came here")
    request.session.get("connected").map { username =>
      println("we came here too")
      try {
        val user = readWrite.accessUser(username)
        Ok(views.html.profile(user))
      }
      catch {
        case ex: Exception => Unauthorized(ex.getMessage)
      }
    }.getOrElse {
      Unauthorized("Oops, you are not connected")
    }
  }

}

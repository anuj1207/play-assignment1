package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import services.ReadWrite

class AuthorityController @Inject() (readWrite: ReadWrite) extends Controller{

  def userList = Action {
    val listOfUser = readWrite.getUserList
    println(listOfUser)
    Ok(views.html.userList(listOfUser))
  }

  def changePermission(username: String, value: Boolean) = Action {
    println("we are doomed")
    readWrite.resetUser(username, value)
    Redirect(routes.AuthorityController.userList())
  }

}

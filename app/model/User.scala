package model


case class User(firstName: String, midName: String, lastName: String, username: String, password: String, mobile: String, gender: String, age: Int, hobbies: List[String])

case class Login(username: String, password:String)
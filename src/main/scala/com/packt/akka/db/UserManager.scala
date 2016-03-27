package com.packt.akka.db 

import com.packt.akka.models._
import reactivemongo.api.QueryOpts
import reactivemongo.core.commands.Count
import scala.concurrent.ExecutionContext
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.api.collections.bson.BSONCollection

object UserManager {
  import MongoDB._
  import UserEntity._

  val collection = db[BSONCollection]("users")

  def login(loginEntity: UserEntity)(implicit ec: ExecutionContext) =
    collection.find(queryLogin(loginEntity.name, loginEntity.password)).one[UserEntity]
    
  def save(userEntity: UserEntity)(implicit ec: ExecutionContext) ={
    println("save"+userEntity.id+userEntity.name+userEntity.password)
    collection.insert(userEntity).map{_ => println("inser map");Created(userEntity.id.stringify)}
    Created(userEntity.id.stringify)
  }
  
  def findById(id: String)(implicit ec: ExecutionContext) =
    collection.find(queryById(id)).one[UserEntity]

  def deleteById(id: String)(implicit ec: ExecutionContext) =
    collection.remove(queryById(id)).map(_ => Deleted)

  def find(implicit ec: ExecutionContext) =
    collection.find(emptyQuery).cursor[BSONDocument].collect[List]()

  private def queryById(id: String) = BSONDocument("_id" -> BSONObjectID(id))
  private def queryLogin(name: String, password: String) = BSONDocument("name" -> name, "password" -> password)

  private def emptyQuery = BSONDocument()
}
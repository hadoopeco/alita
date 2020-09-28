package com.common

import java.util
import java.util.concurrent.{ArrayBlockingQueue, ConcurrentHashMap}

import akka.japi.Predicate
import com.dto.{Entity, Staff, User}
import com.sun.jmx.remote.internal.ArrayQueue

import scala.collection.immutable
import scala.collection.immutable.HashMap

/**
 * @autor mark.wei
 * @date 2018/9/6
 *
 **/

// used to store room entity in runtime
object RoomFactory {

  val rooms:util.Map[String,Entity] = new ConcurrentHashMap[String,Entity]()
  var users:Map[String, User] = new HashMap[String,User]
  var staffs:Map[String,Staff] = new HashMap[String,Staff]()
  var pairs:util.Map[String,String] = new ConcurrentHashMap[String, String]()

  def getRoom(key:String): Entity = {
    var entity:Entity = rooms.get(key)
    if(entity == null) {
      entity = new Entity()
      rooms.put(key, entity)
    }
    entity
  }

  def addUser(userId:String,user:User):Unit = {
    if (!exsitUser(userId)) {
      users += (userId -> user)
    }
  }

  def exsitUser(key:String): Boolean ={
    users.contains(key) || pairs.containsKey(key)
  }

  def removeUser(key:String) ={
    users -= key
  }

  def popUser():User = {
    if (users.nonEmpty) {
      users.head._2
    }else{
      null
    }
  }

  def addStaff(staffId:String, staff: Staff): Unit ={
    this.staffs += (staffId -> staff)
  }

  def getAvaliateStaff():Staff= {
    for (i <- 0 until  4) {
      staffs.foreach(x => {
        if (x._2.workLoads == i) {
           return x._2
        }
      })
    }
    null
  }

  def pair(customerId:String, staffId:String)={
    this.pairs.put(customerId, staffId)
    removeUser(customerId)
    incremeWorkLoad(staffId)
  }

  def isPaired(user:User):Boolean ={
    this.pairs.containsKey(user.userId)
  }

  def incremeWorkLoad(staffId:String) ={
    staffs(staffId).+
  }

  def unpair(customerId:String):Staff={
    val staffId = this.pairs.remove(customerId)
    staffs(staffId).-
    staffs(staffId)
  }

}

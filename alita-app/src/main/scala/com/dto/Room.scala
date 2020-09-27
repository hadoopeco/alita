package com.dto

import java.util
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import scala.beans.BeanProperty

/**
 * @autor mark.wei
 * @date 2018/9/6
 *
 **/
class Room {
//  @BeanProperty var id:String = UUID.randomUUID.toString
  @BeanProperty var id:String = _
  @BeanProperty var domain:String = "/customer"
  @BeanProperty var owner:User = _


  def this(user:User){
    this()
    this.owner = user
    this.id = user.userId
  }
  def path():String = {
      domain+"/"+id;
  }
}





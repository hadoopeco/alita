package com.dto

import java.util

import scala.beans.BeanProperty


/**
 * @autor mark.wei
 * @date 2018/9/4
 *
 **/
class Message extends Serializable{
  @BeanProperty var user:User = _
  @BeanProperty var id:String = _
  //msg from
  @BeanProperty var from:User = _
  //auto convert message should use util.HashMap,not scala mult.HashMap
  @BeanProperty var payload:util.HashMap[String, Object] = new util.HashMap[String,Object]

  @BeanProperty var room:Room = new Room

  def this(user:User){
    this()
    this.user = user
  }

  def this(from:User, message: String){
    this(from)
    put("message", message)
  }

  def put(key:String, message: String): Message ={
    this.payload.put(key,message)
    this
  }


}

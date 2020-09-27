package com.dto

import java.util.UUID

import scala.beans.BeanProperty


/**
 * @autor mark.wei
 * @date 2018/9/5
 *
 **/
class Conversation extends Serializable{
  @BeanProperty var id:String = UUID.randomUUID().toString
}

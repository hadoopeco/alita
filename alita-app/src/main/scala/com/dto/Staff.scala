package com.dto

import scala.beans.BeanProperty

/**
 * @autor mark.wei
 * @date 2018/9/21
 *
 **/
class Staff extends User {

  @BeanProperty var workLoads:Int = 0


  def this(user:User) = {
    this()
    this.userId = user.userId
    this.sessionId = user.sessionId
    this.displayName = user.displayName
    this.role = user.role
  }

  def + ={
    this.workLoads += 1
  }

  def - ={
    this.workLoads -= 1
  }

}

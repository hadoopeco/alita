package com.dto

import scala.beans.BeanProperty


class User{

  @BeanProperty var userId:String = _
  @BeanProperty var role:String = _
  @BeanProperty var displayName:String = _
  @BeanProperty var sessionId:String = _

}

object User{
}




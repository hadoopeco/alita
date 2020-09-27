package com.common

import java.util

import com.dto.User

/**
 * @autor mark.wei
 * @date 2018/9/8
 *
 **/
class OutputMessage {

  var payload:util.HashMap[String, String] = _
  var form:User =_

  def this(user:User){
    this()
    this.form = user
  }
  def this(user:User,payload:util.HashMap[String, String]){
    this(user)
    this.payload = payload
  }

}

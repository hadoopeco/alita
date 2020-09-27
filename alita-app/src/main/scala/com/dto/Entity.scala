package com.dto

import java.util
import java.util.concurrent.ConcurrentHashMap

/**
 * @autor mark.wei
 * @date 2018/9/6
 *
 **/
class Entity {
  val entity:util.Map[String,Entity] = new ConcurrentHashMap[String,Entity]()

  def set(key:String, value: Entity):Unit = {
    entity.put(key,value)
  }

  def get(key:String):Entity = {
    entity.get(key)
  }
}

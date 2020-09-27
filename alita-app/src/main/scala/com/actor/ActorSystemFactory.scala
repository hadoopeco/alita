package com.actor

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.springframework.context.annotation.{Bean, Configuration}
/**
 * @autor mark.wei
 * @date 2018/9/7
 *
 **/

@Configuration
class ActorSystemFactory {

  @Bean
  def createAs():ActorSystem = {
    val actorSystem = ActorSystem.create ("alita", appconfig())
    actorSystem
  }

  private def appconfig():Config ={
    val config = ConfigFactory.load()
    config
  }
}

package com.actor

import akka.actor.Props

/**
 * @autor mark.wei
 * @date 2018/9/8
 *
 **/
class ConversationActor extends AlitaAbstractActor {
  override def receive: Receive = {
    case _ =>
  }
}

object ConversationActor{
  def props():Props = Props(new ConversationActor)
}

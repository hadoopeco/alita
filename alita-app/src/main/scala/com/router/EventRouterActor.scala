package com.router

import akka.cluster.pubsub.DistributedPubSubMediator.Send
import com.actor.AlitaAbstractActor
import com.common.Messages.{Output, RoomJoin, ServiceAssign, ServiceEnd, ServiceRequest, UserInput}
import com.common.RoomFactory
import com.common.Constants._
import com.dto.{Conversation, Entity, Message, User}

/**
 * @autor mark.wei
 * @date 2018/9/5
 *
 **/
class EventRouterActor extends AlitaAbstractActor {

  override def receive: Receive = {
    case msg@UserInput(CHAT_IN,data,user) =>
      mediator.forward(Send(dispatchActorPath,ServiceAssign(user.role,data,user),true))
    case msg@UserInput(CHAT_MESSAGE,data,user) =>
      mediator.forward(Send(robotChatActorPath,msg,true))
    case msg@UserInput(CHAT_REQUEST,data,user) =>
      mediator.forward(Send(dispatchActorPath,ServiceRequest(user),true))
    case msg@UserInput(CHAT_OUT,data,user) =>
      mediator.forward(Send(dispatchActorPath,ServiceEnd(user.role,data.room.path(),user.userId),true))

//    case msg@UserInput("",data,user) =>
    case _ => println("login ...")
  }
}

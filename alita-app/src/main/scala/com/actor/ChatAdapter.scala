package com.actor

import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSubMediator.Send
import com.common.Messages.{Output, PingRoom, RoomBroadcast, UserInput, UserRole}
import com.common.Util.MatchCommand
import com.common.Constants._
import com.common.RoomFactory
import com.dto.{Message, Room, User}

/**
 * @autor mark.wei
 * @date 2018/9/7
 *
 **/
class ChatActor extends AlitaAbstractActor  {
  override def receive: Receive = {
    case UserInput(MatchCommand(domain, "message"), data, user) =>
      val roomPath = data.getRoom.path()
      setOut(RoomBroadcast(roomPath,"chat:message",data))
      self!UserRole(RoomFactory.isPaired(user),user.role,data)
    case UserRole(true,"customer",data) =>
      log.info("send pingroom to {}",roomAggregatePath+"/"+data.room.id)
      mediator!Send(roomAggregatePath,PingRoom(data.room),true)
    case UserRole(false,"customer",data) =>
      //todo: robot reply logic, will implement in future
      val user = new User
      val rdata = new Message()
      rdata.setRoom(data.room)
      user.setUserId("robot")
      rdata.setFrom(user)
      rdata.put("message","i'm is a robot")
      val roomPath = rdata.getRoom.path()
      setOut(RoomBroadcast(roomPath,"chat:message",rdata))

    case _ =>
      log.info("botchat chat")
  }
}

object ChatActor{
  def props():Props = Props(new ChatActor)
}


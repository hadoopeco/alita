package com.actor

import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSubMediator.Send
import com.common.Messages.{Output, PingRoom, RoomBroadcast, UserInput}
import com.common.Util.MatchCommand
import com.common.Constants._
import com.dto.{Message, Room}

/**
 * @autor mark.wei
 * @date 2018/9/7
 *
 **/
class RebotChatActor extends AlitaAbstractActor  {
  override def receive: Receive = {
    case UserInput(MatchCommand(domain, "message"), data, user) =>
      val roomPath = data.getRoom.path()
      log.info("send pingroom to {}",roomAggregatePath+"/"+data.room.id)
      mediator!Send(roomAggregatePath,PingRoom(data.room),true)
      setOut(RoomBroadcast(roomPath,"chat:message",data))
    case _ =>
      log.info("botchat chat")
  }
}

object RebotChatActor{
  def props():Props = Props(new RebotChatActor)
}

class ArtificialChat extends AlitaAbstractActor{
  override def receive: Receive = {
    case _ => log.info("persionChat")
  }
}

object ArtificialChat{
  def props:Props = Props(new ArtificialChat)
}

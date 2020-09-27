package com.actor

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Props}
import com.common.Messages._
import com.corundumstudio.socketio.SocketIOServer
import com.dto.{Message, User}

import scala.concurrent.Future

/**
 * @autor mark.wei
 * @date 2018/9/5
 *
 **/

class OutboundActor(server:SocketIOServer) extends Actor with ActorLogging{
  import context.dispatcher
  override def receive: Receive = {
    case RoomJoin(roomPath:String,data:Message) =>
      Future{
        log.info("{} ,sessionid = {} join the room {}", data.getFrom.userId,data.getFrom.getSessionId,roomPath)
        val client = server.getClient(UUID.fromString(data.getFrom.getSessionId))
        if(client != null){
          client.joinRoom(roomPath)
        }
        server.getRoomOperations(roomPath).sendEvent("room:join",data.room.id)
      }

    case RoomBroadcast(roomPath:String,event:String,data:Message,p)=>
      Future{
        log.info(s"broadcasting event @{} ... {} ",roomPath,event)
        server.getRoomOperations(roomPath).sendEvent(event,data)
      }

    case RoomLeave(roomPath:String,user:User) =>
      Future {
        val client = server.getClient(UUID.fromString(user.getSessionId))
        if(client != null){
            client.leaveRoom(roomPath)
          }
        log.info("user {} leave room {}", user.userId, roomPath)
      }

    case Output(event:String,data:Message,from:User) =>
      Future {
        val client = server.getClient(UUID.fromString(from.getSessionId))
        client.sendEvent(event, data)
        log.info("OutboundActor send out message")
      }

    case _ => log.info("OutboundActor test")
  }
}

object OutboundActor{
  def props(sever:SocketIOServer):Props = Props(new OutboundActor(sever))
}

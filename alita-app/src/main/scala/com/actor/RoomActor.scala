package com.actor

import akka.actor.{Actor, Props, ReceiveTimeout}
import akka.cluster.pubsub.DistributedPubSubMediator.Send
import akka.util.Timeout
import com.common.Messages.{CreateRoom, PingRoom, ResponseClose, ServiceEnd, StartRoom}
import com.common.Settings
import com.dto.Room

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

/**
 * @autor mark.wei
 * @date 2018/9/24
 *
 **/

class RoomAggregate extends Actor{
  override def receive: Receive = {
    case msg@CreateRoom(room) => {
      try {
        context.actorOf(RoomActor.props(room), room.getId).forward(msg)
      } catch {
        case _:Throwable =>
          implicit val timeout = Timeout(Duration.create(1, "seconds"))
          val future = context.actorSelection(room.getId).resolveOne()
          Await.result(future, timeout.duration)
      }
    }

    case msg@StartRoom(room) =>
      context.actorSelection(room.getId).forward(msg)
    case msg@PingRoom(room) =>
      context.actorSelection(room.getId).forward(msg)
  }
}


object RoomAggregate{
  def props():Props = Props(new RoomAggregate)
}

class RoomActor(room: Room) extends AlitaAbstractActor{
  implicit val ec:ExecutionContext = context.dispatcher
  context.setReceiveTimeout(Settings.roomTimeOut)
  var discardRoom = context.system.scheduler.scheduleOnce(Settings.roomTimeOut){}

  override def postStop(): Unit = {
    discardRoom.cancel()
  }
  override def receive: Receive = {
    case CreateRoom(room) =>
      log.info("create room {}", room.path())
    case StartRoom(room)=>
      //:todo handle the start room logic
    case PingRoom(room)=>
      log.info("keep the room alive")
      discardRoom.cancel()
    case ReceiveTimeout => {
      log.info("notify user to keep alive")
      //delay x times later to close the room and release resources
      discardRoom = context.system.scheduler.scheduleOnce(Settings.roomTimeOut) {
        self ! ResponseClose
      }
    }
    case ResponseClose =>
      log.info("close the room {} id= {}...",room.path(),room.id)
      //stop this actor
      mediator!Send(dispatchActorPath,ServiceEnd("customer",room.path(),room.id),true)
      context.stop(self)

  }
}


object RoomActor {
  def props(room:Room):Props = Props(new RoomActor(room))
}


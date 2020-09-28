package com.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.pubsub.DistributedPubSubMediator.{Send, SendToAll}
import com.common.Messages.{CreateRoom, ResumePaired, RoomJoin, RoomLeave, ServiceAssign, ServiceEnd, ServiceRequest}
import com.common.RoomFactory
import com.dto.{Staff, User}


class DispatcherActor extends AlitaAbstractActor with ActorLogging{
  override def receive: Receive = {
    case msg@ServiceAssign("customer",data,user) =>
      //user join the room
      data.room.setId(user.userId)
      val roomPath:String =  data.room.path()
        //add customer into
      log.info("custoemr {} join queue",user.userId)
      mediator.forward(Send(broadcastActorPath,RoomJoin(roomPath,data),true))

    case msg@ServiceRequest(user) =>
      RoomFactory.addUser(user.userId,user)

    case msg@ServiceAssign("staff",data,user) => {
      val staff = new Staff(user)
      var roomPath: String = ""

      log.info("add staff {} to work queue", staff.userId)
      data.room.setId(user.userId)
      roomPath = data.room.path()

      RoomFactory.addStaff(staff.userId, staff)
      mediator.forward(Send(roomAggregatePath, RoomJoin(roomPath, data), true))


      //continue the suspend conversation
      self ! ResumePaired(staff, data)
    }
    case msg@ResumePaired(staff,data) =>
      RoomFactory.pairs.entrySet().forEach( e => {
        if(staff.userId.equals( e.getValue)) {
          data.room.setId(e.getKey)
          val roomPath=  data.room.path()
          RoomFactory.incremeWorkLoad(staff.userId)
          // after assigned success ,to listen the RoomActor running
          mediator!SendToAll(roomAggregatePath,CreateRoom(data.room))

          mediator.forward(Send(broadcastActorPath,RoomJoin(roomPath,data),false))
        }
      })

    case msg@ServiceEnd("customer",roomPath,userId) =>
      //release the paired record
      val staff = RoomFactory.unpair(userId)
      if (staff != null) {
        mediator.forward(Send(broadcastActorPath, RoomLeave(roomPath, staff),false))
      }

    //
    case _ => log.info("DispatcherActor event")

  }
}

object DispatcherActor{
  def props():Props = Props(new DispatcherActor)
}

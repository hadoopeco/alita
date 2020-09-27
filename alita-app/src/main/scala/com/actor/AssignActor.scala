package com.actor

import java.util.concurrent.TimeUnit

import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSubMediator.{Send, SendToAll}
import com.common.Messages.{CreateRoom, RoomJoin, Start}
import com.common.RoomFactory
import com.dto.{Message, User}

import scala.concurrent.duration.Duration

/**
 * @autor mark.wei
 * @date 2018/9/21
 *
 **/
class AssignActor  extends AlitaAbstractActor {
  import context.dispatcher

  val timeoutMessenger = context.system.scheduler.scheduleWithFixedDelay(
    Duration(30, TimeUnit.SECONDS), Duration(10, TimeUnit.SECONDS),self, Start());

  override def postStop(): Unit = timeoutMessenger.cancel()

  def receive:Receive = {
    case Start() =>
      RoomFactory.popUser() match {
        case customer:User => {
          //从排队中删除已配对用户
          val staff = RoomFactory.getAvaliateStaff
          if(staff != null) {
            RoomFactory.pair(customer.userId,staff.userId)
            val roomPath = "/customer/"+customer.userId
            val data = new Message
            data.room.setId(customer.userId)
            data.setFrom(staff)
            mediator.forward(Send(broadcastActorPath,RoomJoin(roomPath,data),true))
            mediator!SendToAll(roomAggregatePath,CreateRoom(data.room))
          }

        }
        case null =>
          RoomFactory.staffs.foreach(s => {
            log.info("staff {} 's worklod is {}", s._2.userId, s._2.workLoads )
          })
      }

  }


}

object AssignActor{
  def props():Props = Props(new AssignActor)
}

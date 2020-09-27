package com.actor

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Put, SendToAll}
import akka.routing.RoundRobinPool
import com.common.Messages.Start

/**
 * @autor mark.wei
 * @date 2018/9/7
 *
 **/
abstract class AlitaAbstractActor extends Actor with ActorLogging {
  val mediator = DistributedPubSub(context.system).mediator

  val conversationActorPath="/user/backend/conversation"

  val broadcastActorPath="/user/outbound"

  val robotChatActorPath="/user/backend/robot"

  val roomAggregatePath="/user/backend/room"

  val dispatchActorPath ="/user/backend/dispatch"

  def setOut(any:AnyRef):Unit = {
    mediator!SendToAll(broadcastActorPath,any)
  }
}


class BackendAggregate extends Actor with ActorLogging{
  val mediator = DistributedPubSub(context.system).mediator

  override def receive: Receive = {
    case Start() =>
      log.info("loading conversation")
      mediator!Put(context.actorOf((RoundRobinPool(4).props(ConversationActor.props())),"conversation"))
      log.info("loading rebotActor")
      mediator!Put(context.actorOf((RoundRobinPool(4).props(RebotChatActor.props())),"robot"))

      log.info("loading rebotActor")
      mediator!Put(context.actorOf((RoundRobinPool(4).props(DispatcherActor.props())),"dispatch"))

      mediator!Put(context.actorOf(AssignActor.props(), "assign"))
      log.info("loading the room")
      mediator!Put(context.actorOf(RoomAggregate.props(), "room"))

    case _ =>
      log.info("loading conversation")
  }
}

object BackendAggregate{
  def props():Props = Props(new BackendAggregate)
}

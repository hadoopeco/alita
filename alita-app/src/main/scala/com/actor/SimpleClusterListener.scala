package com.actor

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent.{ClusterDomainEvent, CurrentClusterState, MemberRemoved, MemberUp, UnreachableMember}

/**
 * @autor mark.wei
 * @date 2018/9/4
 *
 **/
class SimpleClusterListener extends Actor with ActorLogging {
  def receive = {
    case state: CurrentClusterState ⇒
      log.info("Current members: {}", state.members.mkString(", "))
    case MemberUp(member) ⇒
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) ⇒
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) ⇒
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: ClusterDomainEvent ⇒ // ignore
  }
}

package com

import akka.cluster.Cluster
import akka.actor.{ActorSystem, Props}
import akka.cluster.ClusterEvent.ClusterDomainEvent
import com.actor.SimpleClusterListener
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StandaloneApp extends InitializingBean{

  @Autowired
  implicit var actorSystem: ActorSystem = _

  override def afterPropertiesSet(): Unit = {
    val clusterListener = actorSystem.actorOf(Props[SimpleClusterListener],
      name = "clusterListener")

    Cluster(actorSystem).subscribe(clusterListener, classOf[ClusterDomainEvent])
  }
}

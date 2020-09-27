package com.server

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Put, Send}
import com.common.Messages.{RoomJoin, Start, UserInput}
import com.actor.{BackendAggregate, ConversationActor, OutboundActor}
import com.corundumstudio.socketio.{AckRequest, SocketIOClient, SocketIOServer}
import com.corundumstudio.socketio.annotation.{OnConnect, OnDisconnect, OnEvent}
import com.corundumstudio.socketio.listener.{ConnectListener, DataListener}
import javax.annotation.{PostConstruct, PreDestroy}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.dto.{Message, User}
import com.router.EventRouterActor
import com.common.Constants._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Component
class AlitaSocketIOServer{
  val log:Logger = LoggerFactory.getLogger(this.getClass)
  @Autowired
  implicit var socketIOServer : SocketIOServer = _
  @Autowired
  implicit var actorSystem: ActorSystem = _

  @PostConstruct
  def autoStart():Unit ={
    log.info("socket io server start")

    implicit val mediator = DistributedPubSub(actorSystem).mediator

    val outbound = actorSystem.actorOf(OutboundActor.props(socketIOServer),"outbound")
    mediator!Put(outbound)

    val eventRouter= actorSystem.actorOf(Props[EventRouterActor],"event")
    mediator!Put(eventRouter)

    initAkkaActors();
    initEnventListener(mediator,outbound)

    socketIOServer.addConnectListener(new ConnectListener {
      override def onConnect(client: SocketIOClient): Unit = {
        onConnectSocket(client)
      }
    })

    socketIOServer.start()
  }

  @PreDestroy
  def autoStop:Unit ={
    if(socketIOServer != null) {
      log.info("socket io server shutdown")
      socketIOServer.stop()
      socketIOServer = null
    }
  }

  //add Enventlistener
  def initEnventListener(mediator:ActorRef,outbound:ActorRef): Unit ={
    implicit val ec = ExecutionContext.global
    //init the user's events
    val events = List(LOGIN,CHAT_IN,CHAT_MESSAGE,CHAT_OUT,CHAT_REQUEST,"chat:history","chat:typing","user:present","bot:start","bot:end","bot:message","service:request","service:cancel","service:end","staff:checkin","staff:checkout","staff:alive")
    events foreach {
      eventName=>
        socketIOServer.addEventListener(eventName, classOf[Message], new DataListener[Message]{
          override def onData(client: SocketIOClient, data: Message, ackSender: AckRequest): Unit = {
            //forward the input message to event actor
            Future {
              client.get[User]("user")
            } onComplete {
              user: Try[User] =>
                if (null == user) {
                  client.disconnect()
                }

                data.setFrom(user.get)

                //get user information
                val msg = UserInput(eventName, data, user.get)

                mediator.tell(Send("/user/event", msg, localAffinity = true), outbound)
                ackSender.sendAckData(data)
            }
          }
        })
    }
  }

  def onConnectSocket(client:SocketIOClient):Unit = {
    //        logger.info("client connected! " + client.getSessionId + " remoteAddr =" + client.getRemoteAddress)
    val userid = client.getHandshakeData.getSingleUrlParam("id")
    if (null == userid) client.disconnect()
    var username = client.getHandshakeData.getSingleUrlParam("name")
    if (null == username) username = userid
    val role = client.getHandshakeData.getSingleUrlParam("role")
//          if (null == role || "".equals(role)) role = User.Role.customer

    val user = new User
    user.setUserId(userid)
    user.setDisplayName(username)
    user.role = role
    user.setSessionId(client.getSessionId.toString)

    client.set("user", user)
    log.info("client {} connected ",client.getSessionId)
  }



  @OnDisconnect
  def onDisconnect(client: SocketIOClient): Unit ={
    log.info("client {} disconnect", client.getSessionId)
  }




  def initAkkaActors():Unit = {

    actorSystem.actorOf(BackendAggregate.props(), "backend")!Start()

  }



}

package com.common

import com.dto.{Conversation, Message, Room, Staff, User}

/**
 * @autor mark.wei
 * @date 2018/9/4
 *
 **/
object Messages {
  @SerialVersionUID(1L) case class Start()

  //socket io event from user input
  @SerialVersionUID(1L) case class UserInput(event:String,data:Message,user:User)
  @SerialVersionUID(1L) case class UserRole(event:Boolean,role:String,data:Message)



  //Conversation messages
  @SerialVersionUID(1L) case class ConversationStart(roomId:String,creator:User)
  @SerialVersionUID(1L) case class ConversationRequest(roomId:String,creator:User)
  @SerialVersionUID(1L) case class ConversationReady(roomId:String,staffId:User)
  @SerialVersionUID(1L) case class ConversationEnd(roomId:String)
  @SerialVersionUID(1L) case class ConversationCreate(conversation:Conversation)

  //outBound case class
  @SerialVersionUID(1L) case class RoomBroadcast(roomPath:String,event:String,data:Message,p:AnyRef="")
  @SerialVersionUID(1L) case class RoomJoin(roomPath:String,data:Message)
  @SerialVersionUID(1L) case class RoomLeave(roomPath:String,user:User)
  @SerialVersionUID(1L) case class Output(event:String,data:AnyRef,from:User)

  @SerialVersionUID(1L) case class ServiceAssign(role:String,data:Message,user:User)
  @SerialVersionUID(1L) case class ResumePaired(staff:Staff,data:Message)
  @SerialVersionUID(1L) case class ServiceRequest(user:User)
  @SerialVersionUID(1L) case class ServiceEnd(role:String,roomPath:String,userId:String)

  @SerialVersionUID(1L) case class CreateRoom(room:Room)
  @SerialVersionUID(1L) case class StartRoom(room:Room)
  @SerialVersionUID(1L) case class PingRoom(room:Room)





  @SerialVersionUID(1L) case class ResponseClose()



}

package com.server

import com.corundumstudio.socketio.{SocketConfig, SocketIOServer}
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.{Bean, Configuration}

import scala.beans.BeanProperty

@Configuration
class SocketIoConfig{
  @Value("${socketio.host}")
  @BeanProperty var host:String = _

  @Value("${socketio.port}")
  @BeanProperty var port:Int = _

  @Value("${socketio.bossCount}")
  @BeanProperty var bossCount:Int = _

  @Value("${socketio.workCount}")
  @BeanProperty var workCount:Int = _

  @Value("${socketio.allowCustomRequests}")
  @BeanProperty var allowCustomRequests:Boolean = _

  @Value("${socketio.upgradeTimeout}")
  @BeanProperty var upgradeTimeout:Int = _

  @Value("${socketio.pingTimeout}")
  @BeanProperty var pingTimeout:Int = _

  @Value("${socketio.pingInterval}")
  @BeanProperty var pingInterval:Int = _

  @Bean
  def  socketIOServer() :SocketIOServer = {
    val socketConfig = new SocketConfig();
    socketConfig.setTcpNoDelay(true);
    socketConfig.setSoLinger(0);
    val config = new com.corundumstudio.socketio.Configuration();
    config.setSocketConfig(socketConfig);
    config.setHostname(host);
    config.setPort(port);
    config.setBossThreads(bossCount);
    config.setWorkerThreads(workCount);
    config.setAllowCustomRequests(allowCustomRequests);
    config.setUpgradeTimeout(upgradeTimeout);
    config.setPingTimeout(pingTimeout);
    config.setPingInterval(pingInterval);
    new SocketIOServer(config);
  }
}

package com

import akka.cluster.pubsub.DistributedPubSubMediator
import akka.pattern.CircuitBreaker
import org.springframework.boot.{CommandLineRunner, SpringApplication}
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
//@ComponentScan({"com"})
class Application extends CommandLineRunner {
  override def run(args: String*): Unit = {

  }
}




object Application  extends App {
  SpringApplication.run(classOf[Application],args:_*)

}

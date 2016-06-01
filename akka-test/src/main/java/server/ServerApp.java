package server;
/**
 * Created by Administrator on 2015/8/13.
 */

import akka.actor.*;
import akka.kernel.Bootable;
import com.typesafe.config.ConfigFactory;

/** 启动ActorSystem  */
class ServerApp implements Bootable {
    private ActorSystem system;
    private ActorRef serverActor;
    private String actorName = "serverActorName";

    ServerApp() {
        system = ActorSystem.create(ServerActor.AKKA_SYSTEM_NAME, ConfigFactory.load().getConfig("server"));
        serverActor = system.actorOf(Props.create(ServerActor.class), actorName);
        System.out.println("serverActor.path"+serverActor.path());

        //发布一个远程对象到远程客户端的机器上
        //Address addr = new Address("akka", ServerActor.AKKA_SYSTEM_NAME, "10.68.14.49", 8888);
        //serverActor = system.actorOf(new Props(ServerActor.class).withDeploy(new Deploy(new RemoteScope(addr))), "simple");
        //System.out.println("serverActor.path = "+serverActor.path());
    }

    public void startup() {

    }

    public void shutdown() {
        system.shutdown();
    }

}

package client; /**
 * Created by Administrator on 2015/8/13.
 */

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;
import com.typesafe.config.ConfigFactory;

class ClientApp implements Bootable {

    private ActorSystem system;
    private ActorRef serverActor;
    private String actorName = "clientActorName";

    ClientApp(String systemName,String ip,String port,String actorServerName,String params){
        system = ActorSystem.create(ClientActor.AKKA_SYSTEM_NAME, ConfigFactory.load().getConfig("client"));
        ActorRef client = system.actorOf(Props.create(ClientActor.class), actorName);
        serverActor = system.actorFor("akka.tcp://"+systemName+"@"+ip+":"+port+"/user/"+actorServerName+"");
        for(int i =0 ;i<100;i++){
            serverActor.tell(params + i,client);
        }
        //system.actorSelection("").tell(params,client);
        //system.actorFor("akka.tcp://"+systemName+"@"+ip+":"+port+"/user/"+actorServerName+"").tell(params,client);

    }

    public void startup() {

    }

    public void shutdown() {
        system.shutdown();
    }

}

package cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SimpleClientClusterApp {

    private static final String AKKA_CLUSTER_SYSTEM_NAME = "clientClusterSystem";
    private static final String configKey = "client_cluster";

    public static void main(String[] args) {
        if (args.length == 0)
            startup(new String[]{"2561", "2562", "2563"});
        else
            startup(args);
    }

    public static void startup(String[] ports) {
        for (String port : ports) {
            // Override the configuration of the port
            Config config = ConfigFactory.parseString(
                    "akka.remote.netty.tcp.port=" + port).withFallback(
                    ConfigFactory.load().getConfig(configKey));

            // Create an Akka system
            ActorSystem system = ActorSystem.create(AKKA_CLUSTER_SYSTEM_NAME, config);

            // Create an actor that handles cluster domain events
            final ActorRef clientActor = system.actorOf(Props.create(SimpleClusterListener.class),
                    "clusterListener");
            final ActorRef serverActor = system.actorFor("akka.tcp://" + "serverClusterSystem" + "@" + "10.69.16.7" + ":" + "2551" + "/user/" + "clusterListener" + "");
            Cluster.get(system).registerOnMemberUp(new Runnable() {
                @Override
                public void run() {
                    serverActor.tell("这是一个测试", clientActor);
                }
            });
        }
    }
}

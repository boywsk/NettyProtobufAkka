package cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SimpleServerClusterApp {

    private static final String AKKA_CLUSTER_SYSTEM_NAME = "serverClusterSystem";
    private static final String configKey = "server_cluster";

    public static void main(String[] args) {
        if (args.length == 0)
            startup(new String[]{"2551", "2552", "2553"});
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
            system.actorOf(Props.create(SimpleClusterListener.class),
                    "clusterListener");
        }
    }
}

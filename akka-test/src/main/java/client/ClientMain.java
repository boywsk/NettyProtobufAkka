package client;

/**
 * Created by Administrator on 2015/8/13.
 */
public class ClientMain {
    // 1.可以在intellJ环境下，edit configurations --> Appliction 下配置main()函数类，配置 program arguments 的值(注意最前面加空格)：   "serName" "10.68.14.139" "8888" "serverActorName"  "{'a':'123'}"
    // 2.可以在cmd环境下，运行类，输入参数:    "serName" "10.68.14.139" "8888" "serverActorName"  "{'a':'123'}"
    public static void main(String[] args) {
        String systemName =  "serverAkkaSystem";//args[0];
        String ip = "10.69.16.7";//args[1];
        String port = "8888";//args[2];
        String actorServerName="serverActorName";//args[3];
        String params = "{'a':'123'}";//args[4];
        for(int i =0 ;i<args.length;i++){
            System.out.println("第"+i+"个参数" + args[i]);
        }
        new ClientApp(systemName,ip,port,actorServerName,params);
        System.out.println("--------------发消息到服务端---------------");
    }
}

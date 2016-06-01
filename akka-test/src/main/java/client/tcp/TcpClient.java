package client.tcp;

import callback.TcpCallback;
import coder.ProtobufDecoder;
import coder.ProtobufEncoder;
import handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtobufMsg;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangshikai on 2016/5/19.
 */
public class TcpClient {
    private static Logger LOG = LoggerFactory.getLogger(TcpClient.class);
    private static Channel channel;

    public void init(String ip,int port, final TcpCallback callback) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)  //长连接
                .option(ChannelOption.TCP_NODELAY, true)   //无延迟)
//                .option(ChannelOption.SO_TIMEOUT, 3000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(100, 100, 120, TimeUnit.SECONDS));
                        //pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));// 解码器
                        //pipeline.addLast(new StringEncoder(Charset.forName("UTF-8")));// 编码器
                        pipeline.addLast(new ProtobufDecoder());// 解码器
                        pipeline.addLast(new ProtobufEncoder());// 编码器
                        pipeline.addLast(new ClientHandler(callback));
                    }
                });
        ChannelFuture f = null;
        try {
            f = bootstrap.connect(ip, port).await();
            if(f.isSuccess()){
                LOG.info("客户端连接服务器成功!");
                channel = f.channel();
                ProtobufMsg.CommonMsg msg = CreateCommonMsg(123456,"tom-flower","文本","这是一条消息!");
                channel.writeAndFlush(msg);
                for(int i=0;i<100;i++){
                    Thread.sleep(1000);
                    channel.writeAndFlush(msg);
                    LOG.info("客户端发送数字 i="+i);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            f.cause();
        }
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("连接到服务器 port:"+port);
    }



    public static ProtobufMsg.CommonMsg CreateCommonMsg(int userId,String userName,String type,String msgBody){
        ProtobufMsg.CommonMsg.Builder builder = ProtobufMsg.CommonMsg.newBuilder();
        builder.setUserId(userId);
        builder.setUserName(userName);
        builder.setType(type);
        builder.setBody(msgBody);
        return builder.build();
    }

    public void connect(TcpClient client){
        String ip = "127.0.0.1";
        int port = 9999;
        ProtobufMsg.CommonMsg msg = CreateCommonMsg(123456,"tom-flower","文本","登录!");
        client.init(ip, port,new TcpCallback() {
            @Override
            public void callback(ProtobufMsg.CommonMsg msg) {
                System.out.println("客户端收到服务端消息:" + msg.getBody());
            }
        });
        client.channel.writeAndFlush(msg);

    }

    public static void main(String[] args) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                TcpClient client2 = new TcpClient();
                client2.connect(client2);
            }
        });
        t.start();

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                TcpClient client3 = new TcpClient();
                client3.connect(client3);
            }
        });
        t2.start();
        TcpClient client1 = new TcpClient();
        client1.connect(client1);
    }
}

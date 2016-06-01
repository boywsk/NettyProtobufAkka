package server.tcp;

import coder.ProtobufEncoder;
import coder.ProtobufDecoder;
import handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangshikai on 2016/5/19.
 */
public class TcpServer {
    private static Logger LOG = LoggerFactory.getLogger(TcpServer.class);

    public static void initServer(int port) {
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 - 1);
        try {
            server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    //.option(ChannelOption.SO_BACKLOG, 128)  //连接数
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  //长连接
                    .childOption(ChannelOption.TCP_NODELAY, true)   //无延迟
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //TODO   定义编码器   定义解码器  定义消息处理器
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("heartbeat",new IdleStateHandler(120,120,100, TimeUnit.SECONDS));
                            //pipeline.addLast("decoder", new StringDecoder(Charset.forName("UTF-8")));
                            //pipeline.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));
                            pipeline.addLast("decoder", new ProtobufDecoder());
                            pipeline.addLast("encoder", new ProtobufEncoder());
                            pipeline.addLast("handler",new ServerHandler());
                        }
                    });
            ChannelFuture f = server.bind(port).sync();
            LOG.info("服务端口为:" + port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("Netty启动异常：", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
//        PropertyConfigurator.configure("log4j.properties");
        initServer(9999);
    }
}

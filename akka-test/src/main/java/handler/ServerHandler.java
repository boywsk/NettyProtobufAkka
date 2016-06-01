package handler;

import client.tcp.TcpClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import protobuf.ProtobufMsg;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wangshikai on 2016/5/23.
 */
public class ServerHandler extends ChannelHandlerAdapter {

    private static ConcurrentHashMap<ChannelId,String> CHANNEL_ID_MAP = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,Channel> CHANNEL_USER_MAP = new ConcurrentHashMap<>();
    private static AtomicLong connectCount = new AtomicLong(0);

    private void setChannel(Channel channel,String userId){
        CHANNEL_ID_MAP.put(channel.id(),userId);
        CHANNEL_USER_MAP.put(userId,channel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("服务端收到新连接!");
        CHANNEL_ID_MAP.put(ctx.channel().id(),connectCount.getAndIncrement()+"");
        System.out.println(CHANNEL_ID_MAP.toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProtobufMsg.CommonMsg commonMsg = (ProtobufMsg.CommonMsg)msg;
        System.out.println("服务端收到客户端发送的消息: userId:"+commonMsg.getUserId() +"\tuserName:"+commonMsg.getUserName());
        ProtobufMsg.CommonMsg ack = TcpClient.CreateCommonMsg(123456,"tom-flower","文本","我收到你的登录消息，菜鸡!");
        ctx.channel().writeAndFlush(ack);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE)
                System.out.println("read idle");
            else if (event.state() == IdleState.WRITER_IDLE)
                System.out.println("write idle");
            else if (event.state() == IdleState.ALL_IDLE)
                System.out.println("all idle");
        }
    }
}

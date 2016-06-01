package handler;

import callback.TcpCallback;
import client.tcp.TcpClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import protobuf.ProtobufMsg;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangshikai on 2016/5/23.
 */
public class ClientHandler extends ChannelHandlerAdapter {

    private static ConcurrentHashMap<ChannelId,String> CHANNEL_ID_MAP = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String,Channel> CHANNEL_USER_MAP = new ConcurrentHashMap<>();

    private TcpCallback callback;

    public ClientHandler(TcpCallback callback){
        this.callback = callback;
    }


    private void setChannel(Channel channel,String userId){
        CHANNEL_ID_MAP.put(channel.id(),userId);
        CHANNEL_USER_MAP.put(userId,channel);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("client .....");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.callback.callback((ProtobufMsg.CommonMsg)msg);
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

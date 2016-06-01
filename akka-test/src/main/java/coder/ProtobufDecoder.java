package coder;

import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import protobuf.ProtobufMsg.CommonMsg;

import java.util.List;

/**
 * Created by wangshikai on 2016/5/19.
 */
public class ProtobufDecoder extends ByteToMessageDecoder{
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> out) throws Exception {
        buf.markReaderIndex();
        if(buf.readableBytes() < 4){
            buf.resetReaderIndex();
            return;
        }
        int msgLength = buf.readInt();
        if(buf.readableBytes() < msgLength){
            buf.resetReaderIndex();
            return;
        }
        ByteBuf byteBuf = buf.readBytes(msgLength);
        CommonMsg msg = null;
        try {
            msg = CommonMsg.parseFrom(byteBuf.array());
        } catch (InvalidProtocolBufferException e) {
            System.out.println("未知消息,无法解析");
            channelHandlerContext.close();
            e.printStackTrace();
            return;
        }
        if(msg != null){
            out.add(msg);
        }
    }
}

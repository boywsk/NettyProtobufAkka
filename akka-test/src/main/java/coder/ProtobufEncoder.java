package coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import protobuf.ProtobufMsg.CommonMsg;

import java.util.List;

/**
 * Created by wangshikai on 2016/5/19.
 */
public class ProtobufEncoder extends MessageToMessageEncoder<CommonMsg> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CommonMsg msg, List<Object> out) throws Exception {
        ByteBuf buf = Unpooled.buffer();
        if (msg != null) {
            byte[] bytes = msg.toByteArray();
            int length = bytes.length;
            buf.writeInt(length);//int占4个字节
            buf.writeBytes(bytes);
        }
        out.add(buf);
    }
}

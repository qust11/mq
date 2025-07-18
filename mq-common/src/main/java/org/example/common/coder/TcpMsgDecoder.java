package org.example.common.coder;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author qushutao
 * @since 2025/7/14 19:21
 **/
public class TcpMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        short magic = byteBuf.readShort();
        int code = byteBuf.readInt();
        int len = byteBuf.readInt();
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);

        TcpMsg tcpMsg = new TcpMsg(code, bytes);
        list.add(tcpMsg);
    }
}

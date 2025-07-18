package org.example.common.coder;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qushutao
 * @since 2025/7/14 19:20
 **/
public class TcpMsgEncoder extends MessageToByteEncoder<TcpMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TcpMsg tcpMsg, ByteBuf byteBuf) throws Exception {
        System.out.println("TcpMsgEncoder:"+tcpMsg);
        byteBuf.writeShort(tcpMsg.getMagic());
        byteBuf.writeInt(tcpMsg.getCode());
        byteBuf.writeInt(tcpMsg.getLen());
        byteBuf.writeBytes(tcpMsg.getBody());
    }
}

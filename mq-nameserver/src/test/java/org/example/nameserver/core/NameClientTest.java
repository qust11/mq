package org.example.nameserver.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.common.coder.TcpMsg;
import org.example.common.coder.TcpMsgDecoder;
import org.example.common.coder.TcpMsgEncoder;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author qushutao
 * @since 2025/7/15 9:35
 **/
@SpringBootTest()
@RunWith(SpringRunner.class)
@Slf4j
class NameClientTest {
    Bootstrap bootstrap = new Bootstrap();
    NioEventLoopGroup group = new NioEventLoopGroup();

    Channel channel ;
    @BeforeEach
    public  void setUp() throws InterruptedException {

        ChannelFuture sync = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new TcpMsgDecoder());
                        ch.pipeline().addLast(new TcpMsgEncoder());
                    }
                })
                .connect(new InetSocketAddress("127.0.0.1", 9876)).sync();

        channel = sync.channel();
        log.info("启动成功");
    }

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++){
            TimeUnit.SECONDS.sleep(1);
            TcpMsg tcpMsg = new TcpMsg();
            tcpMsg.setCode(1);
            byte[] bytes = "hello world".getBytes();
            tcpMsg.setBody(bytes);
            tcpMsg.setLen(bytes.length);
            channel.writeAndFlush(tcpMsg);
        }
    }
}
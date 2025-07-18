package org.example.nameserver.core;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.TcpDnsQueryDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.example.common.coder.TcpMsgDecoder;
import org.example.common.coder.TcpMsgEncoder;
import org.example.nameserver.event.EventBus;
import org.example.nameserver.handler.TcpServerHandler;

/**
 * @author qushutao
 * @since 2025/7/14 19:10
 **/
public class NameServerStarter {

    private int port;

    public NameServerStarter(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {

        NioEventLoopGroup boss = new NioEventLoopGroup();

        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new TcpMsgDecoder());
                        ch.pipeline().addLast(new TcpMsgEncoder());
                        ch.pipeline().addLast(new TcpServerHandler(new EventBus()));
                        ch.pipeline().addLast(new LoggingHandler());
                    }
                });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }));

        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        channelFuture.channel().closeFuture().sync();

    }
}

package com.thinkwin.push.netty;

import com.thinkwin.common.utils.DateTypeFormat;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * netty 服务端配置
 */
public class NettyServerBootstrap {
    private final static Logger logger = LoggerFactory.getLogger(NettyServerBootstrap.class);

    //端口
    private int port;

    public NettyServerBootstrap(int port) throws InterruptedException {
        this.port = port;
        bind();
    }

    private void bind() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //Netty建立连接的辅助类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //配置属性，向pipeline添加handler
            bootstrap.group(boss, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            //通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            //handler容器初始化
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();
                    p.addLast(new LineBasedFrameDecoder(2048));
                    p.addLast(new StringDecoder());
//                    p.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, 64 * 1024, 0, 4, 0, 4, true));
//                    p.addLast(new StringDecoder(Charset.forName("UTF-8")));
//                    p.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//                    p.addLast("decoder", new StringDecoder());
//                    p.addLast("encoder", new StringEncoder());
//                    p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    p.addLast(new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS));
                    p.addLast(new NettyServerHandler());
                }

            });

            ChannelFuture f = bootstrap.bind(port).sync();
            if (f.isSuccess()) {
                logger.info(DateTypeFormat.DateToStr(new Date()) + "：服务端启动成功了！！！--------------");
            }
            //这里会一直等待，直到socket被关闭
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}

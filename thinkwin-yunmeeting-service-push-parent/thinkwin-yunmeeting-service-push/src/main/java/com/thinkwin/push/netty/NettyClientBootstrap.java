package com.thinkwin.push.netty;

import com.thinkwin.common.dto.publish.CommandResponse;
import com.thinkwin.common.utils.DateTypeFormat;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 模拟客户端
 */
public class NettyClientBootstrap {

    //端口
    private int port;
    //IP
    private String host;

    public SocketChannel socketChannel;

    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(20);

    public NettyClientBootstrap(int port, String host) throws InterruptedException {
        this.port = port;
        this.host = host;
        start();
    }

    private void start() throws InterruptedException {
        //配置客户端NIO 线程组
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.group(eventLoopGroup);
            bootstrap.remoteAddress(host, port);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                    socketChannel.pipeline().addLast(new ObjectEncoder());
                    socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                    socketChannel.pipeline().addLast(new NettyClientHandler());
                }
            });

            //绑定端口, 异步连接操作
            ChannelFuture future = bootstrap.connect(host, port).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
                System.out.println(DateTypeFormat.DateToStr(new Date()) + "：客户端服务启动成功！！！---------");
            } else {
                System.out.println(DateTypeFormat.DateToStr(new Date()) + "：客户端服务启动失败！！！---------");
            }
        } catch (Exception e) {
            System.out.println("002与服务器{}:{}连接出现异常..." + host + "," + port);
            System.out.println(DateTypeFormat.DateToStr(new Date()) +"：002客户端异常了：发起重新登录了");
            TimeUnit.SECONDS.sleep(10);
//            Login();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        //业务1> 客户端启动 登录(模拟)
        Login();
    }

    public static void Login() throws InterruptedException {
        try {
            NettyClientBootstrap bootstrap = new NettyClientBootstrap(9999, "localhost");
            CommandResponse loginMsg = new CommandResponse();
            loginMsg.setCmd("20031");
            loginMsg.setTerminalId("test_terminal_id");
            bootstrap.socketChannel.writeAndFlush(loginMsg);
            System.out.println(DateTypeFormat.DateToStr(new Date()) +"：客户端发起登录了");

        } catch (Exception e) {
            System.out.println(DateTypeFormat.DateToStr(new Date()) + e);
        }
    }
}

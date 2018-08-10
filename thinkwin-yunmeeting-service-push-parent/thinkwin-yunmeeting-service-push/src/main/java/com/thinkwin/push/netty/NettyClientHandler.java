package com.thinkwin.push.netty;

import com.alibaba.fastjson.JSON;
import com.thinkwin.common.dto.publish.Command;
import com.thinkwin.common.dto.publish.CommandResponse;
import com.thinkwin.common.utils.DateTypeFormat;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NettyClientHandler extends SimpleChannelInboundHandler<Command> {
    private final static Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    //利用写空闲发送心跳检测消息
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //客户端的userEventTriggered中对应的触发事件下发送一个心跳包给服务端，检测服务端是否还存活，
        // 防止服务端已经宕机，客户端还不知道
        try{
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                switch (e.state()) {
                    //服务端主动发起  客户端读取心跳开始
                    case READER_IDLE:
                        System.out.println(DateTypeFormat.DateToStr(new Date()) +"：客户端读取超时发送一次心跳包----------");
                        CommandResponse pingMsg1 = new CommandResponse();
                        pingMsg1.setCmd("0000"); //心跳指令
                        pingMsg1.setTerminalId("test_terminal_id");
                        ctx.writeAndFlush(pingMsg1);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 需要用户来实现的实际处理
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command baseMsg) throws Exception {
        logger.info("收到服务端请求："+ JSON.toJSONString(baseMsg));
        //回复
        CommandResponse cmd = new CommandResponse();
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "");
        cmd.setData(data);
        cmd.setTerminalId(baseMsg.getTerminalId());
        channelHandlerContext.channel().writeAndFlush(cmd);
        /*MsgType msgType=baseMsg.getType();
        switch (msgType){
            case LOGIN:{
                LoginMsg loginMsg = (LoginMsg) baseMsg;
                System.out.println("登录状态："+loginMsg.getSuccess());
            }break;

            case PING:{
                System.out.println(DateTypeFormat.DateToStr(new Date()) +":"+baseMsg.getClientId()+"客户端收到服务端请求ping");
            }break;

            case ASK:{
               AskMsg  askMsg = (AskMsg)baseMsg;
               AskParams params = (AskParams)askMsg.getParams();
               System.out.println(DateTypeFormat.DateToStr(new Date()) +"：客户端接受信息参数: "+params.getAuth());
            }break;

            default:break;
        }*/

        ReferenceCountUtil.release(baseMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("服务端关闭程序时捕获异常");
        cause.printStackTrace();

        ctx.fireExceptionCaught(cause);
        //关闭连接调用定时任务 去保持登录
        ctx.close();
    }

}

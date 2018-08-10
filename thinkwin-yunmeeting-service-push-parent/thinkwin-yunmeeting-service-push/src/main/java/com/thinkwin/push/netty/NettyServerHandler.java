package com.thinkwin.push.netty;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.thinkwin.common.dto.publish.Command;
import com.thinkwin.common.dto.publish.CommandResponse;
import com.thinkwin.common.log.BusinessType;
import com.thinkwin.common.log.EventType;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.model.log.TerminalLog;
import com.thinkwin.common.utils.DateTypeFormat;
import com.thinkwin.common.utils.LocalCacheUitl;
import com.thinkwin.common.utils.PropertiesUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.log.service.TerminalLogService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.InfoReleaseTerminalService;
import com.thinkwin.yuncm.service.TerminalService;
import com.thinkwin.yunmeeting.framework.util.spring.SpringContextUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {
    private final static Logger logger = LoggerFactory.getLogger(NettyServerBootstrap.class);
    @Resource
    private TerminalService terminalService;
    @Resource
    private InfoReleaseTerminalService infoReleaseTerminalService;
    @Resource
    private TerminalLogService terminalLogService;

    private TerminalService getTerminalService(){
        if(terminalService==null){
            return (TerminalService) SpringContextUtil.getBean("terminalService");
        }
        return terminalService;
    }
    private InfoReleaseTerminalService getInfoReleaseTerminalService(){
        if(infoReleaseTerminalService==null){
            return (InfoReleaseTerminalService) SpringContextUtil.getBean("infoReleaseTerminalService");
        }
        return infoReleaseTerminalService;
    }
    private TerminalLogService getTerminalLogService(){
        if(terminalLogService==null){
            return (TerminalLogService) SpringContextUtil.getBean("terminalLogService");
        }
        return terminalLogService;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            String type = "";
            if (event.state() == IdleState.READER_IDLE) {
                type = "read idle";
            } else if (event.state() == IdleState.WRITER_IDLE) {
                type = "write idle";
            } else if (event.state() == IdleState.ALL_IDLE) {
                type = "all idle";
            }
            Channel channel = ctx.channel();
            String key = LocalCacheUitl.remove(channel);
            if(StringUtils.isNotBlank(key)) {
                offline(key);
                logger.info(DateTypeFormat.DateToStr(new Date()) + "=" + channel.toString() + "心跳超时：" + type);
            }
            channel.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM,"257468b0ee3c404dbd00aa0f3872ad7d");
//        getTerminalService().updateTerminalStatus("1","257468b0ee3c404dbd00aa0f3872ad7d",1);
        Channel channel = ctx.channel();
        if(!channel.toString().contains("R:/100.")) {
            logger.info(channel.toString() + "在线");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if(!channel.toString().contains("R:/100.")) {
            String key = LocalCacheUitl.remove(channel);
            channel.close();
            offline(key);
            logger.info(key + "|" + channel.toString() + "离线");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        String s =(String) msg;
//        ByteBuf in = (ByteBuf) msg;
//        String s = in.toString(CharsetUtil.UTF_8);
        logger.info("收到客户端请求: " + s);
        logger.info("客户端channel: " +ctx.channel().toString());

        //将请求内容原样返回
//        ctx.channel().writeAndFlush(getSendByteBuf(in.toString(CharsetUtil.UTF_8)));

        if(StringUtils.isNotBlank(s)){
            try {
                CommandResponse c = JSON.parseObject(s, CommandResponse.class);
                //指令类型
                String cmd = c.getCmd();
                String tenantId = c.getTenantId();
                String terminalId = c.getTerminalId();
                if(cmd.equals("20031")){
                    Command command = new Command();
                    command.setTerminalId(terminalId);
                    command.setTimestamp(new Date().getTime());
                    command.setRequestId(c.getRequestId());
                    command.setTenantId(tenantId);
                    RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
                    InfoReleaseTerminal terminal = getInfoReleaseTerminalService().selectInfoReleaseTerminalById(terminalId);
                    String ok = "失败";
                    if (terminal != null) {
                        String cacheKey = tenantId + "_" + terminalId;
//                        if (LocalCacheUitl.get(cacheKey) == null) {
                            //将长连接通道放入本地缓存
                            LocalCacheUitl.put(cacheKey, ctx.channel());
//                        }
                        String key = tenantId + "_publish_" + terminalId;
//                        if(StringUtils.isBlank(RedisUtil.get(key))){
                            //将终端id和队列映射放入redis
                            RedisUtil.set(key, PropertiesUtil.getString("service_queue"));
//                        }
                        //同步设置终端状态为在线
                        RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
                        boolean b = getTerminalService().updateTerminalStatus(terminalId, 1);
                        if(b){
                            //记录最后一次心跳时间
                            if(StringUtils.isNotBlank(c.getTenantId())&&StringUtils.isNotBlank(c.getTerminalId())){
                                RedisUtil.set(c.getTenantId()+"_heartbeat_"+c.getTerminalId(),String.valueOf(new Date().getTime()));
                            }
                            command.setData(1);
                            ok = "成功";
                        }
                    } else {
                        command.setData(0);
                    }
                    command.setCmd(cmd);
                    String send = JSON.toJSONString(command)+"\r\n";
                    ctx.channel().writeAndFlush(Unpooled.copiedBuffer(send.getBytes()));
                    logger.info(DateTypeFormat.DateToStr(new Date()) + "：客户端login：" + terminalId + " 登录" + ok);
                    //记录日志
                    TerminalLog log = new TerminalLog();
                    log.setTenantId(tenantId);
                    log.setTerminalId(terminalId);
                    log.setMethodname(Thread.currentThread().getStackTrace()[1].getMethodName());
                    log.setMethodarg(send);
                    log.setClassname(this.getClass().getName());
                    log.setBusinesstype(BusinessType.terminalLoginOp.toString());
                    log.setEventtype(EventType.terminal_login.toString());
                    log.setBusinesstime(new Date());
                    log.setCommand(cmd);
                    log.setIp(ctx.channel().remoteAddress().toString());
                    log.setSource("PAD");
                    log.setContent("终端登录");
                    if (terminal != null) {
                        log.setStatus(1);
                        log.setTerminalName(terminal.getTerminalName());
                    }else{
                        log.setStatus(0);
                        log.setResult("该终端不存在");
                        log.setTerminalName("");
                    }
                    getTerminalLogService().addTerminalLog(log);
                }else if(cmd.equals("00000")){
                    String send = s.trim()+"\r\n";
                    ctx.channel().writeAndFlush(Unpooled.copiedBuffer(send.getBytes()));
                    logger.info(DateTypeFormat.DateToStr(new Date()) + "：收到心跳包："+ s);
                    //获取最后一次心跳时间
                    String ttlKey = tenantId + "_heartbeat_" +terminalId;
                    String timestamp = RedisUtil.get(ttlKey);
                    if(StringUtils.isNotBlank(timestamp)) {
                        //记录最后一次心跳时间
                        if(StringUtils.isNotBlank(c.getTenantId())&&StringUtils.isNotBlank(c.getTerminalId())){
                            RedisUtil.set(ttlKey,String.valueOf(new Date().getTime()));
                        }
                        String cacheKey = tenantId + "_" + terminalId;
                        if (LocalCacheUitl.get(cacheKey) == null) {
                            LocalCacheUitl.put(cacheKey, ctx.channel());
                            String key = tenantId + "_publish_" + terminalId;
                            if(StringUtils.isBlank(RedisUtil.get(key))){
                                RedisUtil.set(key, PropertiesUtil.getString("service_queue"));
                            }
                            RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
                            boolean b = getTerminalService().updateTerminalStatus(terminalId, 1);
                        }
                    }else {
                        ctx.channel().close();
                        RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
                        boolean b = getTerminalService().updateTerminalStatus(terminalId, 0);
                    }
                }else{
                    RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
                    getTerminalService().handleResponse(c);
                    //记录最后一次心跳时间
                    if(StringUtils.isNotBlank(c.getTenantId())&&StringUtils.isNotBlank(c.getTerminalId())){
                        RedisUtil.set(c.getTenantId()+"_heartbeat_"+c.getTerminalId(),String.valueOf(new Date().getTime()));
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
                logger.info(DateTypeFormat.DateToStr(new Date()) + "：请求失败："+ ex.getMessage());
                ctx.channel().close();
//                Command command = new Command();
//                command.setData(0);
//                String send = JSON.toJSONString(command)+"\r\n";
//                ctx.channel().writeAndFlush(Unpooled.copiedBuffer(send.getBytes()));
//                logger.info("请求失败");
            }
        }

//        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if(!channel.toString().contains("R:/100.")) {
            String key = LocalCacheUitl.remove(channel);
            channel.close();
            offline(key);
            cause.printStackTrace();
            ctx.fireExceptionCaught(cause);

            //关闭客户端连接
            ctx.close();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("触发========handlerAdded");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("触发========handlerRemoved");
    }
    private ByteBuf getSendByteBuf(String message) throws UnsupportedEncodingException {
        byte[] req = message.getBytes("UTF-8");
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);

        return pingMessage;
    }

    private void offline(String key) {
        if(StringUtils.isNotBlank(key)) {
            String tenantId = key.split("_")[0];
            String terminalId = key.split("_")[1];
            logger.info(DateTypeFormat.DateToStr(new Date()) + "=" + tenantId + "离线offline：" + terminalId);
            RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
            getTerminalService().updateTerminalStatus(terminalId, 0);
        }
    }
}

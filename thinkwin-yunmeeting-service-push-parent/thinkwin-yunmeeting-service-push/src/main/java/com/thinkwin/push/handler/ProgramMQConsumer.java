package com.thinkwin.push.handler;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.thinkwin.common.dto.publish.Command;
import com.thinkwin.common.dto.publish.CommandMessage;
import com.thinkwin.common.model.log.TerminalLog;
import  com.thinkwin.common.mqMessage.MQConsumer;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.DateTypeFormat;
import com.thinkwin.common.utils.LocalCacheUitl;
import com.thinkwin.log.service.TerminalLogService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.TerminalService;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

public class ProgramMQConsumer extends MQConsumer{
    private final static Logger logger = LoggerFactory.getLogger(ProgramMQConsumer.class);

    @Resource
    private TerminalService terminalService;
    @Resource
    private TerminalLogService terminalLogService;
    public Boolean processBiz(String tenantId, List<String> bizIdList, String messageStr){
        logger.info("----------------------->>>tenantId:"+tenantId);
        if(StringUtils.isNotBlank(messageStr)) {
            CommandMessage command = JSON.parseObject(messageStr, CommandMessage.class);
            if (bizIdList != null && bizIdList.size() > 0) {
                for (String terminalId : bizIdList) {
                    boolean arrived = false;
                    String cacheKey = tenantId + "_" + terminalId;
                    logger.info("----------------------->>>MapCacheKey:"+cacheKey);
                    Channel channel = (Channel) LocalCacheUitl.get(cacheKey);
                    Command cmd = new Command();
                    cmd.setCmd(command.getCmd());
                    cmd.setTimestamp(command.getTimestamp());
                    cmd.setTenantId(command.getTenantId());
                    cmd.setTerminalId(terminalId);
                    cmd.setData(command.getData());
                    cmd.setRequestId(command.getRequestId());

                    //记录日志
                    TerminalLog log = new TerminalLog();
//                    if(StringUtils.isNotBlank(command.getRequestId())){
//                        log.setId(command.getRequestId());
//                    }
                    log.setTenantId(tenantId);
                    log.setTerminalId(terminalId);
                    log.setMethodname(Thread.currentThread().getStackTrace()[1].getMethodName());
                    log.setMethodarg(messageStr);
                    log.setClassname(this.getClass().getName());
                    log.setBusinesstime(new Date());
                    log.setCommand(command.getCmd());
                    if (channel != null) {
                        logger.info("服务端channel: " +channel.toString());
                        log.setStatus(1);
                        log.setIp(channel.remoteAddress().toString());
                        arrived = true;
                        String send = JSON.toJSONString(cmd, SerializerFeature.WriteNullStringAsEmpty);
                        send = send + "\r\n";
                        String finalSend = send;
                        channel.writeAndFlush(Unpooled.copiedBuffer(send.getBytes()));
                        logger.info(DateTypeFormat.DateToStr(new Date()) + "：服务端向" + terminalId + "发送了一次请求！！！\n" + finalSend);
                    }else {
                        log.setStatus(0);
                    }
                    terminalLogService.addTerminalLog(log);
                    RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
                    terminalService.handleReceipt(cmd, arrived);
                }
            }
        }
        return null;
    }
}

package com.thinkwin.push.handler;


import com.thinkwin.common.dto.publish.Command;
import com.thinkwin.common.dto.publish.CommandResponse;
import com.thinkwin.common.utils.DateTypeFormat;
import com.thinkwin.push.netty.NettyChannelMap;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * netty异步发送节目
 */
public class ProgramHandler implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ProgramHandler.class);
    /**
     * 构造函数参数
     */
    private CommandResponse command;

    /**
     * 构造函数
     * @param command
     */
    public ProgramHandler(CommandResponse command) {
        this.command = command;
    }

    @Override
    public void run() {
        System.out.println("###############进入异步任务系统的handler了##############");
        if (command != null) {
            SocketChannel channel = (SocketChannel) NettyChannelMap.get(command.getTerminalId());
            if (channel != null) {
                Command cmd = new Command();
                cmd.setCmd("10001");
                cmd.setTimestamp(new Date().getTime());
                cmd.setData("我主动发送了节目");
                channel.writeAndFlush(cmd);
                logger.info(DateTypeFormat.DateToStr(new Date()) +"：服务端向"+cmd.getTerminalId()+"发送了一次请求！！！");
            }
        }

    }

}
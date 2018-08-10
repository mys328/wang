package com.thinkwin.push.service.impl;

import com.alibaba.dubbo.rpc.RpcContext;
import com.thinkwin.common.utils.LocalCacheUitl;
import com.thinkwin.push.netty.NettyServerBootstrap;
import com.thinkwin.push.service.ChannelHelperService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.service.TerminalService;
import io.netty.channel.Channel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User:wangxilei
 * Date:2018/6/21
 * Company:thinkwin
 */
@Service("channelHelperService")
public class ChannelHelperServiceImpl implements ChannelHelperService {
    private final static Logger logger = LoggerFactory.getLogger(ChannelHelperServiceImpl.class);

    @Resource
    private TerminalService terminalService;
    @Override
    public boolean offline(String tenantId,String terminalId) {
        if(StringUtils.isNotBlank(tenantId)&&StringUtils.isNotBlank(terminalId)) {
            String cacheKey = tenantId + "_" + terminalId;
            Channel channel = (Channel) LocalCacheUitl.get(cacheKey);
            logger.info("channel====" + channel);
            if (channel != null) {
                channel.close();
                RpcContext.getContext().setAttachment(TenantContext.RPC_CONTEXT_TENANTID_PARAM, tenantId);
                boolean b = terminalService.updateTerminalStatus(terminalId, 0);
                logger.info("主动离线====" + b);
                return true;
            }
        }
        return false;
    }
}

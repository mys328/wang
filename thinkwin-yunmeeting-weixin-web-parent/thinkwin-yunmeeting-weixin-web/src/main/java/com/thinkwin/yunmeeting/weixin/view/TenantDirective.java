package com.thinkwin.yunmeeting.weixin.view;

import com.thinkwin.auth.service.SaasTenantService;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.service.TenantContext;
import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;


public class TenantDirective implements TemplateDirectiveModel {
    @Resource
    SaasTenantService saasTenantService;

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException {
        if(StringUtils.isNotBlank(TenantContext.getTenantId())){
            SaasTenant example = new SaasTenant();
            example.setId(params.get("tenantId").toString());
            SaasTenant saasTenant = saasTenantService.selectSaasTenantServcie(example);

            env.setVariable("tenantInfo",  env.getObjectWrapper().wrap(saasTenant));
            body.render(env.getOut());
        }
    }
}
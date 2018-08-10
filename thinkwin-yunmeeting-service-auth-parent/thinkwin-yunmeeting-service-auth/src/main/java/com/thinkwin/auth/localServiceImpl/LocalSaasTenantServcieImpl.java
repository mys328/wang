package com.thinkwin.auth.localServiceImpl;

import com.thinkwin.auth.localService.LocalSaasTenantServcie;
import com.thinkwin.auth.mapper.core.SaasTenantInfoMapper;
import com.thinkwin.auth.mapper.core.SaasTenantMapper;
import com.thinkwin.common.model.core.SaasDbConfig;
import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.core.SaasTenantInfo;
import com.thinkwin.common.utils.PingYinUtil;
import com.thinkwin.common.utils.StringUtil;
import com.thinkwin.common.utils.createDatabase.AntExecSqlCreateDatabase;
import com.thinkwin.common.utils.createDatabase.AntExecSqlCreateTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/5/27.
 */
@Service("localSaasTenantServcie")
public class LocalSaasTenantServcieImpl implements LocalSaasTenantServcie {

    private final static Logger logger = LoggerFactory.getLogger(LocalSaasTenantServcieImpl.class);

    @Autowired
    SaasTenantMapper saasTenantMapper;
    @Autowired
    SaasTenantInfoMapper saasTenantInfoMapper;

    @Override
    public boolean saveSaasTenantServcie(SaasTenant saasTenant) {
        int i = this.saasTenantMapper.insert(saasTenant);
        if (i > 0) {
            return true;
        }
        return false;
    }

    @Override
    public SaasTenant selectSaasTenantServcie(SaasTenant saasTenant) {
        List<SaasTenant> saasTenants = this.saasTenantMapper.select(saasTenant);
        if (null != saasTenants && saasTenants.size() > 0) {
            return saasTenants.get(0);
        }
        return null;
    }

    @Override
    public SaasTenant createTenantDataBaseAndTable(String tenantName, String tenantId, SaasDbConfig saasDbConfig) {
        SaasTenant saasTenant = new SaasTenant();

        try{
            String tenantFirstSpell = PingYinUtil.getFirstSpell(tenantName);
            String createSchemaName = null;
            if (!StringUtil.isEmpty(tenantFirstSpell)) {
                saasTenant.setTenantCode(tenantFirstSpell);
                //获取公司Id
                createSchemaName = tenantFirstSpell + "_" + tenantId;
                saasTenant.setDbConfig(createSchemaInfo(createSchemaName));
            }
            if (null != createSchemaName) {
                logger.info("*************开始创建数据库：", createSchemaName + "**开始时间："+new Date());
                //创建租户数据库
                boolean createDatabase = AntExecSqlCreateDatabase.createDatabase(createSchemaName, saasDbConfig);
                if (createDatabase) {
                    logger.info("*************创建数据库结束：", createSchemaName + "**结束时间："+new Date());
                    logger.info("*************开始创建表：", createSchemaName + "**开始时间："+new Date());
                    //创建租户数据库表
                    boolean createTable = AntExecSqlCreateTable.createTable(createSchemaName, saasDbConfig);
                    if (createTable) {
                        logger.info("*************创建表结束：", createSchemaName + "**结束时间："+new Date());
                        return saasTenant;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("LocalSaasTenantServcieImpl createTenantDataBaseAndTable tenantName:" + tenantName,",tenantId:"+tenantId+",saasDbConfig"+saasDbConfig,e);
        }
        return null;
    }

    @Override
    public SaasTenantInfo selectSaasTenantInfoBySaasTenantInfo(SaasTenantInfo saasTenantInfo) {
        List<SaasTenantInfo> saasTenantInfos = this.saasTenantInfoMapper.select(saasTenantInfo);
        if (null != saasTenantInfos && saasTenantInfos.size() > 0) {
            return saasTenantInfos.get(0);
        }
        return null;
    }

    @Override
    public boolean saveSaasTenantInfo(SaasTenantInfo saasTenantInfo) {
        int i = this.saasTenantInfoMapper.insertSelective(saasTenantInfo);
        if (i > 0) {
            return true;
        }
        return false;
    }

    /**
     * 方法名：createSchemaInfo</br>
     * 描述：替换租户数据表配置信息  存库用</br>
     * 参数：[createSchemaName]</br>
     * 返回值：java.lang.String</br>
     * 开发人员：weining</br>
     * 创建时间：2017/6/20  </br>
     */
    public String createSchemaInfo(String createSchemaName) {
        String info = "{\n" + "    \"db.driverClass\": \"com.mysql.jdbc.Driver\",\n" + "    \"db.maxWait\": \"5000\",\n" + "    \"db.initialSize\": \"5\",\n" + "    \"db.minIdle\": \"5\",\n" + "    \"db.maxActive\": \"30\",\n" + "    \"db.maxIdle\": \"10\"\n" + "}";
        return info;
    }
}

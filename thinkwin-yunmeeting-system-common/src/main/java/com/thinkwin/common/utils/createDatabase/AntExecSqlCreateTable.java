package com.thinkwin.common.utils.createDatabase;

import com.thinkwin.common.model.core.SaasDbConfig;
import com.thinkwin.common.utils.Base64;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource.DBPasswordUtil;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.SQLExec;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * 调用 ant.jar 的 SQLExec 执行 SQL 脚本文件
 * SQLExec 的还提供事物控制的功能
 * 第二步：创建表结构
 */
public class AntExecSqlCreateTable {
    private final static Logger logger = LoggerFactory.getLogger(AntExecSqlCreateTable.class);

    public static boolean createTable(String databaseName, SaasDbConfig saasDbConfig) {
        boolean success = false;
        try {
        Properties properties = new Properties();
        InputStream is = AntExecSqlCreateTable.class.getClassLoader().getResourceAsStream("jdbc.properties");
        properties.load(is);

        String username = saasDbConfig.getUsername();
        String password = saasDbConfig.getPassword();
        //数据库进行加密处理了，此处进行解密
        if (password != null && !"".equals(password)) {
            password = DBPasswordUtil.getPassword(password);
        }

        String host = saasDbConfig.getUrl();
        String port = saasDbConfig.getPort();
        String createTablePath = properties.getProperty("jdbc.createTablePath") + databaseName + ".sql";
        String outputPath = properties.getProperty("jdbc.outputPath");

        //从reids读取脚本文件  同时解码
        String sql = RedisUtil.get("yunmeeting_db_init_key");
        sql = Base64.decode(sql);
        //生成动态文件
        FileUtil.string2File(sql, createTablePath);

        SQLExec sqlExec = new SQLExec();
        //设置数据库参数
        sqlExec.setDriver("com.mysql.jdbc.Driver");
        sqlExec.setUrl("jdbc:mysql://" + host + ":" + port + "/" + databaseName);
        sqlExec.setUserid(username);
        sqlExec.setPassword(password);

        //运行数据库指定动态数据库
        sqlExec.addText("use " + databaseName + ";");

        //要执行的脚本
        sqlExec.setSrc(new File(createTablePath));
        //有出错的语句该如何处理
        sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
        sqlExec.setPrint(true); //设置是否输出

        //输出到文件 sql.out 中；不设置该属性，默认输出到控制台
        sqlExec.setOutput(new File(outputPath));
        sqlExec.setProject(new Project()); // 要指定这个属性，不然会出错

        sqlExec.execute();
        success = true;

         //删除动态文件
         if (success) {
                FileUtil.deleteFile(createTablePath);
         }

        } catch (Exception e) {
            logger.error("AntExecSqlCreateTable createTable databaseName:" + databaseName+",saasDbConfig"+saasDbConfig,e);
            success = false;
        }


        return success;
    }
}

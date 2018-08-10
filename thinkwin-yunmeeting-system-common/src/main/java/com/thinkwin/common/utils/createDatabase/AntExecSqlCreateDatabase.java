package com.thinkwin.common.utils.createDatabase;

import com.thinkwin.common.model.core.SaasDbConfig;
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
 * 第一步：创建数据库
 */
public class AntExecSqlCreateDatabase {

    private final static Logger logger = LoggerFactory.getLogger(AntExecSqlCreateDatabase.class);

    public static boolean createDatabase(String databaseName, SaasDbConfig saasDbConfig) {

        boolean success = false;

        try {
            Properties properties = new Properties();

            InputStream is = AntExecSqlCreateDatabase.class.getClassLoader().getResourceAsStream("jdbc.properties");

            properties.load(is);

            String username = saasDbConfig.getUsername();
            String password = saasDbConfig.getPassword();
            //数据库进行加密处理了，此处进行解密
            if (password != null && !"".equals(password)) {
                password = DBPasswordUtil.getPassword(password);
            }

            String host = saasDbConfig.getUrl();
            String port = saasDbConfig.getPort();
            String outputPath = properties.getProperty("jdbc.outputPath");

            SQLExec sqlExec = new SQLExec();
            //设置数据库参数
            sqlExec.setDriver("com.mysql.jdbc.Driver");
            sqlExec.setUrl("jdbc:mysql://" + host + ":" + port + "/");
            sqlExec.setUserid(username);
            sqlExec.setPassword(password);

            //要执行的语句 创建数据库
            sqlExec.addText("create database " + databaseName + " character set utf8 collate = utf8_general_ci;");

            //有出错的语句该如何处理
            sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));

            //设置是否输出
            sqlExec.setPrint(true);

            //输出到文件 sql.out 中；不设置该属性，默认输出到控制台
            sqlExec.setOutput(new File(outputPath));
            // 要指定这个属性，不然会出错
            sqlExec.setProject(new Project());

            sqlExec.execute();
            success = true;
        } catch (Exception e) {
            logger.error("AntExecSqlCreateDatabase createDatabase databaseName:" + databaseName+",saasDbConfig"+saasDbConfig,e);
            success = false;
        }
        return success;
    }

    /**
     * 删除数据库功能
     * @param dataBaseName
     * @param saasDbConfig
     * @return
     */
    public static boolean delDatabase(String dataBaseName,SaasDbConfig saasDbConfig){
        boolean success = false;
        try {
            Properties properties = new Properties();
            InputStream is = AntExecSqlCreateDatabase.class.getClassLoader().getResourceAsStream("jdbc.properties");
            properties.load(is);
            String username = saasDbConfig.getUsername();
            String password = saasDbConfig.getPassword();
            //数据库进行加密处理了，此处进行解密
            if (password != null && !"".equals(password)) {
                password = DBPasswordUtil.getPassword(password);
            }
            String host = saasDbConfig.getUrl();
            String port = saasDbConfig.getPort();
            String outputPath = properties.getProperty("jdbc.outputPath");
            SQLExec sqlExec = new SQLExec();
            //设置数据库参数
            sqlExec.setDriver("com.mysql.jdbc.Driver");
            sqlExec.setUrl("jdbc:mysql://" + host + ":" + port + "/");
            sqlExec.setUserid(username);
            sqlExec.setPassword(password);
            //要执行的语句 创建数据库
            sqlExec.addText("drop database if exists "+dataBaseName);
            //有出错的语句该如何处理
            sqlExec.setOnerror((SQLExec.OnError) (EnumeratedAttribute.getInstance(SQLExec.OnError.class, "abort")));
            //设置是否输出
            sqlExec.setPrint(true);
            //输出到文件 sql.out 中；不设置该属性，默认输出到控制台
            sqlExec.setOutput(new File(outputPath));
            // 要指定这个属性，不然会出错
            sqlExec.setProject(new Project());
            sqlExec.execute();
            success = true;
        } catch (Exception e) {
            success = false;
        }
        return success;
    }
}

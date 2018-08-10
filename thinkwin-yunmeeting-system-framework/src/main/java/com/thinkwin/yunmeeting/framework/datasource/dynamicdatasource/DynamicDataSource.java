package com.thinkwin.yunmeeting.framework.datasource.dynamicdatasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangyiqian
 * @describe 实现动态数据源切换逻辑
 */
public class DynamicDataSource extends AbstractRoutingDataSource implements
		ApplicationContextAware {


	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static Map<Object, Object> _targetDataSources;

	private ApplicationContext ctx;

	/**
	 * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#determineCurrentLookupKey()
	 * @describe 数据源为空或者为0时，自动切换至默认数据源，即在配置文件中定义的dataSource数据源
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		String dataSourceName = DBContextHolder.getDBType();
		if (dataSourceName == null) {
			dataSourceName = "dataSource";
		} else {
			this.selectDataSource(dataSourceName);
			if (dataSourceName.equals("0"))
				dataSourceName = "dataSource";
		}
		log.debug("-------->切换至数据源" + dataSourceName);
		return dataSourceName;
	}

	public void setTargetDs(Map<Object, Object> targetDataSources) {
		DynamicDataSource._targetDataSources = targetDataSources;
		super.setTargetDataSources(DynamicDataSource._targetDataSources);
		super.afterPropertiesSet();
	}

	public void addTargetDataSource(String key, DruidDataSource dataSource) {
		DynamicDataSource._targetDataSources.put(key, dataSource);
		this.setTargetDs(DynamicDataSource._targetDataSources);
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}

	public DruidDataSource createDataSource(String driverClassName, String url,String username, String password) {
		DruidDataSource dataSource = new DruidDataSource();
		DruidDataSource parent = (DruidDataSource) this.ctx
				.getBean("parentDataSource");
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDriverClassName(driverClassName);
		dataSource.setDefaultReadOnly(parent.getDefaultReadOnly());
		dataSource.setMaxActive(parent.getMaxActive());
		dataSource.setInitialSize(parent.getInitialSize());
		dataSource.setMaxIdle(parent.getMaxIdle());
		dataSource.setMaxWait(parent.getMaxWait());

		return dataSource;
	}

	/**
	 * @param tanentId
	 * @throws Exception
	 * @describe 数据源存在时不做处理，不存在时创建新的数据源链接，并将新数据链接添加至缓存
	 */
	public Object selectDataSource(String tanentId) {
		Object sid = DBContextHolder.getDBType();

        DruidDataSource dataSource = this.getDataSource(tanentId);

//		Object obj = DynamicDataSource._targetDataSources.get(tanentId);
		if (dataSource != null && sid != null && sid.equals(tanentId)) {
            DBContextHolder.setDBType(tanentId);
            return dataSource;
		} else {
			if (null != dataSource){
                this.setDataSource(tanentId, dataSource);
                return dataSource;
            }
            return null;
		}
	}

	/**
	 * @describe 查询tanentId对应的数据源记录
	 * @param tanentId
	 * @return
	 * @throws Exception
	 */
	public DruidDataSource getDataSource(String tanentId) {

        DruidDataSource dataSource =null;

        if (null == DynamicDataSource._targetDataSources) {
            DynamicDataSource._targetDataSources = new HashMap<Object, Object>();
        }

        if ("0".equals(tanentId)) {
            DBContextHolder.setDBType("0");
            return (DruidDataSource)DynamicDataSource._targetDataSources.get("dataSource");
        }
        dataSource = (DruidDataSource)DynamicDataSource._targetDataSources.get(tanentId);

        if(null!=dataSource)return dataSource;
		//this.determineCurrentLookupKey();
		//从redis或者数据库中获取数据源配置信息

		Connection conn = null;
		PreparedStatement ps=null;
		ResultSet rs =null;
		String driverClassName=null;
		String dataBaseName = null;

		String dbtype = DBContextHolder.getDBType();
		try {
		    DBContextHolder.setDBType(null);
			conn = this.getConnection();
			ps = conn.prepareStatement("select * from saas_tenant t,saas_db_config c where  t.id =? and t.saas_db_config_id = c.id");
			ps.setString(1, tanentId);
			rs = ps.executeQuery();
            DBContextHolder.setDBType(dbtype);
			if (rs.next()) {
				String db_cfg_id = rs.getString("saas_db_config_id");
				String db_cfg = rs.getString("db_config");
				String tenant_code = rs.getString("tenant_code");
				String url = rs.getString("url");
				String port = rs.getString("port");
				String userName = rs.getString("username");
				String password = rs.getString("password");

				//数据库进行加密处理了，此处进行解密
				if(password != null && !"".equals(password)){
					password = DBPasswordUtil.getPassword(password);
				}

				if("plantform_init_yunmeeting_db".equals(tanentId)) {
					dataBaseName = "yunmeeting_db";
				}else{
					dataBaseName = tenant_code + "_" + tanentId;
				}
				if(StringUtils.isBlank(db_cfg_id)||StringUtils.isBlank(db_cfg)) {
					throw new RuntimeException("没有获取到该租户的数据源配置信息。租户ID：" + tanentId);
				}else if(StringUtils.isBlank(dataBaseName)){
					throw new RuntimeException("数据库名称为空。租户ID：" + tanentId);
				}

				JSONObject jb = JSON.parseObject(db_cfg);
				driverClassName = (String) jb.getString("db.driverClass");

				if(StringUtils.isNotBlank(url))
					url = "jdbc:mysql://"+url+":"+port+"/"+dataBaseName+"?useUnicode=true&amp;characterEncoding=utf8&generateSimpleParameterMetadata=true";

				if (StringUtils.isNotBlank(driverClassName) && StringUtils.isNotBlank(port) && StringUtils.isNotBlank(url) && StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
					dataSource = this.createDataSource(driverClassName, url, userName, password);
                    this.setDataSource(tanentId, dataSource);
                }

			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.error("获取数据源出错");
		} finally {
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				log.error("关闭数据源发生错误！");
			}
		}
		return dataSource;
	}



	/**
	 * @param tanentId
	 * @throws Exception
	 * @describe 关闭租户数据源
	 */
	public void closeTenantDataSource(String tanentId) {
		Object sid = DBContextHolder.getDBType();
		DruidDataSource dataSource = this.getDataSource(tanentId);
		if (dataSource != null && sid != null && sid.equals(tanentId)) {
			DBContextHolder.setDBType(tanentId);
			dataSource.close();
		}
	}

	/**
	 * @param tanentId
	 * @param dataSource
	 */
	public void setDataSource(String tanentId, DruidDataSource dataSource) {
		this.addTargetDataSource(tanentId, dataSource);
		DBContextHolder.setDBType(tanentId);
	}

	public java.util.logging.Logger getParentLogger(){
		return java.util.logging.Logger.getLogger(this.getClass().getName());
	}

}

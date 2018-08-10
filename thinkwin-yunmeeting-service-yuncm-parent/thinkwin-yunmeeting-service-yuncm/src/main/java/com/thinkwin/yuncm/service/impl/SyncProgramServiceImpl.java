package com.thinkwin.yuncm.service.impl;

import com.thinkwin.common.model.core.SaasTenant;
import com.thinkwin.common.model.db.*;
import com.thinkwin.common.model.publish.*;
import com.thinkwin.common.utils.redis.RedisUtil;
import com.thinkwin.common.vo.SyncProVo;
import com.thinkwin.core.service.SaasTenantService;
import com.thinkwin.publish.service.PlatformProgramVersionSerevice;
import com.thinkwin.publish.service.PlatformProgrameLabelService;
import com.thinkwin.service.TenantContext;
import com.thinkwin.yuncm.mapper.InfoProgramComponentMapper;
import com.thinkwin.yuncm.mapper.InfoProgramComponentMiddleMapper;
import com.thinkwin.yuncm.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * 类说明：
 * @author lining 2018/5/9
 * @version 1.0
 *
 */
@Service("syncProgramService")
public class SyncProgramServiceImpl implements SyncProgramService {

	private static Logger logger = LoggerFactory.getLogger(SyncProgramServiceImpl.class);


	public static final String alphaProgramStatus = "2"; //内测节目状态
	public static final String releaseProgramStatus = "1";//正式节目状态

	public static final String recorderStatus = "1"; //有效状态

	public final String UserType = "0"; //正式用户类型

	@Resource
	SysSetingService sysSetingService;

	@Resource
	public PlatformProgramVersionSerevice platformProgramVersionSerevice;

	@Resource
	public PlatformProgrameLabelService platformProgrameLabelService;

	@Resource
	public InfoProgramService infoProgramService;

	@Resource
	public InfoProgramLabelService infoProgramLabelService;

	@Resource
	public InfoLabelProgramMiddleService infoLabelProgramMiddleService;

	@Resource
	public BizImageRecorderService bizImageRecorderService;

	@Resource
	public InfoProgramComponentMapper infoProgramComponentMapper;

	@Resource
	public InfoProgramComponentMiddleMapper infoProgramComponentMiddleMapper;

	@Resource
	SaasTenantService saasTenantCoreService;

	@Override
	public Map<String, String> getProgramVersion() {
		Map<String, String> map = new HashMap<>();

		String tenantProgramReleaseVer = sysSetingService.get("tenantProgramReleaseVer");
		String tenantProgramAlphaVer = sysSetingService.get("tenantProgramAlphaVer");

		String tenantDZProgramReleaseVer = sysSetingService.get("customizedProgramVersionNum");
		String tenantDZProgramAlphaVer = sysSetingService.get("customizedVersionUpdateBatch");

		map.put("TenantProgramReleaseVer", (StringUtils.isNotBlank(tenantProgramReleaseVer)) ? tenantProgramReleaseVer : "");
		map.put("TenantProgramAlphaVer", (StringUtils.isNotBlank(tenantProgramAlphaVer)) ? tenantProgramAlphaVer : "");

		map.put("TenantDZProgramReleaseVer", (StringUtils.isNotBlank(tenantDZProgramReleaseVer)) ? tenantDZProgramReleaseVer : "");
		map.put("TenantDZProgramAlphaVer", (StringUtils.isNotBlank(tenantDZProgramAlphaVer)) ? tenantDZProgramAlphaVer : "");
		return map;
	}

	/**
	 * 设置租户节目版本号
	 *
	 * @param tenantProgramReleaseVer
	 * @param tenantProgramAlphaVer
	 */
	@Override
	public void setProgramVersion(String tenantProgramReleaseVer, String tenantProgramAlphaVer) {
		Map<String, String> map = new HashMap<>();

		SysSetting sysSettingAlpha = this.sysSetingService.findByKey("tenantProgramAlphaVer");
		SysSetting sysSettingRelease = this.sysSetingService.findByKey("tenantProgramReleaseVer");
		if (null != sysSettingAlpha) {
			sysSetingService.set(sysSettingAlpha.getId(), "tenantProgramAlphaVer", tenantProgramAlphaVer);
		}
		if (null != sysSettingRelease) {
			sysSetingService.set(sysSettingRelease.getId(), "tenantProgramReleaseVer", tenantProgramReleaseVer);
		}
	}

	/**
	 * 设置租户定制节目版本号
	 *
	 * @param tenantDZProgramReleaseVer
	 * @param tenantDZProgramAlphaVer
	 */
	@Override
	public void setDZProgramVersion(String tenantDZProgramReleaseVer, String tenantDZProgramAlphaVer) {
		Map<String, String> map = new HashMap<>();

		SysSetting customizedVersionUpdateBatch = this.sysSetingService.findByKey("customizedVersionUpdateBatch");
		SysSetting customizedProgramVersionNum = this.sysSetingService.findByKey("customizedProgramVersionNum");
		if (null != customizedVersionUpdateBatch) {
			sysSetingService.set(customizedVersionUpdateBatch.getId(), "customizedVersionUpdateBatch", tenantDZProgramAlphaVer);
		}
		if (null != customizedProgramVersionNum) {
			sysSetingService.set(customizedProgramVersionNum.getId(), "customizedProgramVersionNum", tenantDZProgramReleaseVer);
		}
	}

	/**
	 * 内测用户切换正式用户，删除内测节目
	 *
	 * @param userType
	 */
	@Override
	public void delAlphaProgram(String userType) {
		if (userType.equals(UserType)) {
			this.infoLabelProgramMiddleService.batchPhysicalDel("2");
			this.infoProgramService.batchPhysicalDelProgramByProgramStatus("2");
			this.infoProgramLabelService.batchPhysicalDelLabelByLabelStatus("2");
		}

	}

	@Override
	public List<InfoProgramComponentMiddle> getInfoProgramComponentMiddleList(String programId) {
		Example example = new Example(InfoProgramComponentMiddle.class);
		example.createCriteria().andEqualTo("programId",programId);
		List<InfoProgramComponentMiddle> list = infoProgramComponentMiddleMapper.selectByExample(example);
		return list;
	}

	/**
	 * 查检版本是否要更新
	 * 1000：无须升级;
	 * 1001：正式租户需升级正式版本节目
	 * 1002：内测租户需升级节目（正式节目、内测节目）
	 *
	 * @param tenantId
	 * @param tenantType
	 */
	@Override
	public SyncProVo checkVersion(String tenantId, int tenantType) {

		SyncProVo syncProVo = new SyncProVo();
		syncProVo.setCode(1000);

		//平台内测节目版本号
		String SysProgramAlphaVer;
		//平台正式节目版本号
		String SysProgramReleaseVer;
		//租户内测节目版本号
		String TenantProgramAlphaVer;
		//租户正式节目版本号
		String TenantProgramReleaseVer;

		//平台内测定制节目版本号
		String SysDZProgramAlphaVer;
		//平台正式定制节目版本号
		String SysDZProgramReleaseVer;

		//租户内测定制节目版本号
		String TenantDZProgramAlphaVer;
		//租户正式定制节目版本号
		String TenantDZProgramReleaseVer;

		Map<String, String> sysMap = this.platformProgramVersionSerevice.getUpdateInfo(tenantId);
		SysProgramAlphaVer = sysMap.get("versionUpdateBatch");
		SysProgramReleaseVer = sysMap.get("programVersionNum");
		SysDZProgramAlphaVer = sysMap.get("customizedVersionUpdateBatch");
		SysDZProgramReleaseVer = sysMap.get("customizedProgramVersionNum");

		Map<String, String> tenantMap = this.getProgramVersion();
		TenantProgramAlphaVer = tenantMap.get("TenantProgramAlphaVer");
		TenantProgramReleaseVer = tenantMap.get("TenantProgramReleaseVer");
		TenantDZProgramAlphaVer = tenantMap.get("TenantDZProgramAlphaVer");
		TenantDZProgramReleaseVer = tenantMap.get("TenantDZProgramReleaseVer");

		StringBuffer descride = new StringBuffer();

		boolean customTenant = isCustomTenant(null, tenantId);
		if (tenantType == 1) {
			if (null != SysProgramReleaseVer) {
				this.releaseVer(syncProVo, descride, SysProgramReleaseVer, TenantProgramReleaseVer);
				if (customTenant && syncProVo.getCode() == 1000 && SysDZProgramReleaseVer != null) {
					this.TenantDZVer(syncProVo, descride, SysDZProgramReleaseVer, TenantDZProgramReleaseVer);
				}
			} else if (SysDZProgramReleaseVer != null && customTenant) {
				this.TenantDZVer(syncProVo, descride, SysDZProgramReleaseVer, TenantDZProgramReleaseVer);
			}
		} else {
			if (SysProgramAlphaVer != null && SysProgramReleaseVer != null) {
				this.alphaVer(syncProVo, descride, SysProgramAlphaVer, TenantProgramAlphaVer);
				if (customTenant && syncProVo.getCode() == 1000 && SysDZProgramAlphaVer != null) {
					this.TenantDZVer(syncProVo, descride, SysDZProgramAlphaVer, TenantDZProgramAlphaVer);
				}
			} else if (customTenant && SysDZProgramReleaseVer != null) {
				this.TenantDZVer(syncProVo, descride, SysDZProgramAlphaVer, TenantDZProgramAlphaVer);
			}
		}

		return syncProVo;
	}

	//内测
	public void alphaVer(SyncProVo syncProVo, StringBuffer descride, String SysProgramAlphaVer, String TenantProgramAlphaVer) {
		logger.error("SysProgramAlphaVer:" + SysProgramAlphaVer + ",TenantProgramAlphaVer:" + TenantProgramAlphaVer);
		if (StringUtils.isBlank(SysProgramAlphaVer) || (TenantProgramAlphaVer.equals(SysProgramAlphaVer))) {
			syncProVo.setCode(1000);
		} else {
			syncProVo.setCode(1002);
			descride.append("您有新的节目可以更新");
			syncProVo.setDescribe(descride.toString());
		}
	}

	//正式
	public void releaseVer(SyncProVo syncProVo, StringBuffer descride, String SysProgramReleaseVer, String TenantProgramReleaseVer) {
		if (SysProgramReleaseVer.equals(TenantProgramReleaseVer)) {
			syncProVo.setCode(1000);
		} else {
			syncProVo.setCode(1001);
			descride.append("节目已更新到V");
			descride.append(SysProgramReleaseVer);
			descride.append(",您当的版本为V");
			if (StringUtils.isNotBlank(TenantProgramReleaseVer)) {
				descride.append(TenantProgramReleaseVer);
			} else {
				descride.append("0.0");
			}
			descride.append(",您可以更新体验最新节目");
			syncProVo.setDescribe(descride.toString());
		}

	}

	//定制节目
	public void TenantDZVer(SyncProVo syncProVo, StringBuffer descride
			, String SysDZProgramVer, String TenantProgramVer) {
		if (SysDZProgramVer.equals(TenantProgramVer)) {
			syncProVo.setCode(1000);
		} else {
			syncProVo.setCode(1003);
			descride.append("您有定制节目可更新");
			syncProVo.setDescribe(descride.toString());
		}

	}

	private boolean isCustomTenant(SaasTenant saasTenant, String tenantId){
		if(saasTenant != null){
			return "1".equals(saasTenant.getIsCustomizedTenant());
		}

		boolean isCustomTenant = false;
		String cacheKey = tenantId + "_publish_tenantInfo";
		String tenantInfo = RedisUtil.get(cacheKey);
		if(StringUtils.isNotBlank(tenantInfo)){
			if("1".equals(tenantInfo)){
				isCustomTenant = true;
			}
		} else {
			saasTenant = saasTenantCoreService.selectByIdSaasTenantInfo(tenantId);
			String isCustomTenantStr = saasTenant.getIsCustomizedTenant();
			if(StringUtils.isBlank(isCustomTenantStr)){
				isCustomTenantStr = "0";
			}

			RedisUtil.set(cacheKey, isCustomTenantStr, 3600);
			isCustomTenant = "1".equals(isCustomTenantStr);
		}

		return isCustomTenant;
	}

	/**
	 * 同步节目
	 *
	 * @param tenantId
	 * @param tenantType
	 * @param code
	 * @return
	 */
	@Override
	public SyncProVo updateProgramVer(String tenantId, int tenantType, int code) {
		SyncProVo syncProVo = new SyncProVo();

		//平台节目
		List<PlatformProgrameLabel> sysAlphaPlatformProgrameLabels = null;
		List<PlatformProgrameLabel> sysPlatformProgrameLabels = null;
		List<PlatformLabelProgramMiddle> sysPlatformLabelProgramMiddles = null;
		List<PlatformProgram> sysAlphaPlatformPrograms = null;
		List<PlatformProgram> sysReleasePlatformPrograms = null;
		List<BizImageRecorder> sysBizImageRecorders = null;


		//租户节目
		List<InfoProgrameLabel> alphaInfoProgrameLabels = null;
		List<InfoProgrameLabel> infoProgrameLabels = null;
		List<InfoLabelProgramMiddle> infoLabelProgramMiddles = null;
		List<InfoProgram> alphaInfoPrograms = null;
		List<InfoProgram> formalInfoPrograms = null;
		List<BizImageRecorder> bizImageRecorders = null;

		//变更内测的节目
		List<InfoProgram> addAlphaInfoPrograms = new ArrayList<>();
		List<InfoProgram> updateAlphaInfoPrograms = new ArrayList<>();
		List<String> delAlphaInfoPrograms = new ArrayList<>();
		//变更的正式节目
		List<InfoProgram> addInfoPrograms = new ArrayList<>();
		List<InfoProgram> updateInfoPrograms = new ArrayList<>();
		List<String> delInfoPrograms = new ArrayList<>();
		//变更的内测标签
		List<InfoProgrameLabel> addAlphaInfoProgrameLabels = new ArrayList<>();
		List<InfoProgrameLabel> updateAlphaInfoProgrameLabels = new ArrayList<>();
		List<String> delAlphaInfoProgrameLabels = new ArrayList<>();
		//变更的正式标签
		List<InfoProgrameLabel> addInfoProgrameLabels = new ArrayList<>();
		List<InfoProgrameLabel> updateInfoProgrameLabels = new ArrayList<>();
		List<String> delInfoProgrameLabels = new ArrayList<>();
		//变更的关联关系
		List<InfoLabelProgramMiddle> addInfoLabelProgramMiddles = new ArrayList<>();
		List<InfoLabelProgramMiddle> updateLabelProgramMiddles = new ArrayList<>();
		List<String> delInfoLabelProgramMiddles = new ArrayList<>();
		//变更的节目图片
		List<BizImageRecorder> addBizImageRecorders = new ArrayList<>();
		List<BizImageRecorder> updateBizImageRecorders = new ArrayList<>();
		List<String> delBizImageRecorders = new ArrayList<>();

		//平台正式定制节目
		List<PlatformProgram> customizedPlatformProgram = null;

		//平台内测定制节目
		List<PlatformProgram> customizedInnerTestPlatformProgram = null;

		List<PlatformProgramComponents> platformProgramComponents = new ArrayList<>();
		List<PlatformProgramComponentsMiddle> platformProgramComponentsMiddle = new ArrayList<>();

		try {

			Map sysMap = this.platformProgrameLabelService.selectTenantDateByTenantId(tenantId);
			sysAlphaPlatformPrograms = (List<PlatformProgram>) sysMap.get("innerTestPlatformPrograme");
			sysReleasePlatformPrograms = (List<PlatformProgram>) sysMap.get("platformProgrames");

			customizedPlatformProgram = (List<PlatformProgram>) sysMap.get("customizedPlatformProgrames");
			customizedInnerTestPlatformProgram = (List<PlatformProgram>) sysMap.get("customizedInnerTestPlatformPrograme");

			platformProgramComponents = (List<PlatformProgramComponents>) sysMap.get("platformProgramComponents");
			platformProgramComponentsMiddle = (List<PlatformProgramComponentsMiddle>) sysMap.get("platformProgramComponentsMiddle");

			if (CollectionUtils.isNotEmpty(customizedInnerTestPlatformProgram)) {
				if (sysAlphaPlatformPrograms == null) {
					sysAlphaPlatformPrograms = new ArrayList<>();
				}
				sysAlphaPlatformPrograms.addAll(customizedInnerTestPlatformProgram);
			}

			if (CollectionUtils.isNotEmpty(customizedPlatformProgram)) {
				if (sysReleasePlatformPrograms == null) {
					sysReleasePlatformPrograms = new ArrayList<>();
				}
				sysReleasePlatformPrograms.addAll(customizedPlatformProgram);
			}

			sysPlatformProgrameLabels = (List<PlatformProgrameLabel>) sysMap.get("platformProgrameLabels");
			sysAlphaPlatformProgrameLabels = (List<PlatformProgrameLabel>) sysMap.get("innerTestPlatformProgrameLabels");
			sysPlatformLabelProgramMiddles = (List<PlatformLabelProgramMiddle>) sysMap.get("platformLabelProgramMiddles");
			sysBizImageRecorders = (List<BizImageRecorder>) sysMap.get("bizImageRecorders");


			SaasTenant saasTenant = saasTenantCoreService.selectSaasTenantServcie(TenantContext.getTenantId());
			if(null == saasTenant){
				return null;
			}

			String isInnerTest = saasTenant.getIsInnerTest();
			if(code == 1003){
				if("1".equals(isInnerTest)){
					code = 1002;
				} else {
					code = 1001;
				}
			}


			logger.info("**************SYNC start**********************");
			//比较并执行同步节目操作
			if (code == 1001) {
				//比较并执行正式节目
				syncFormalProgram(addInfoProgrameLabels, updateInfoProgrameLabels, delInfoProgrameLabels, sysPlatformProgrameLabels, infoProgrameLabels, addInfoPrograms, updateInfoPrograms, delInfoPrograms, sysReleasePlatformPrograms, formalInfoPrograms);
			} else if (code == 1002) {
				//内测和正式节目
				syncAlphaAndFormalProgram(addAlphaInfoProgrameLabels, updateAlphaInfoProgrameLabels, delAlphaInfoProgrameLabels, sysAlphaPlatformProgrameLabels, alphaInfoProgrameLabels, addAlphaInfoPrograms, updateAlphaInfoPrograms, delAlphaInfoPrograms, sysAlphaPlatformPrograms, alphaInfoPrograms, addInfoProgrameLabels, updateInfoProgrameLabels, delInfoProgrameLabels, sysPlatformProgrameLabels, infoProgrameLabels, addInfoPrograms, updateInfoPrograms, delInfoPrograms, sysReleasePlatformPrograms, formalInfoPrograms);
			}

			syncMiddle(addInfoLabelProgramMiddles, updateLabelProgramMiddles, delInfoLabelProgramMiddles, sysPlatformLabelProgramMiddles, infoLabelProgramMiddles);

			syncBizImageRecorder(addBizImageRecorders, updateBizImageRecorders, delBizImageRecorders, sysBizImageRecorders, bizImageRecorders);

			syncProgramComponents(platformProgramComponents);
			syncComponentMiddle(platformProgramComponentsMiddle);

            /*if(true){
                throw new RuntimeException("测试异常！！！");
            }*/

			//修改租户节目版本号
			updateTenantProgarmVer(tenantId);
			logger.info("**************SYNC end**********************");

			syncProVo.setCode(1);
			syncProVo.setDescribe("同步成功");
		} catch (Exception e) {
			logger.info("##########节目更新失败，异常信息：" + e);
			syncProVo.setCode(0);
			syncProVo.setDescribe("节目更新失败，请重试");
			throw e;
		}

		return syncProVo;
	}


	/**
	 * 只有内测数据需要同步
	 *
	 * @param addAlphaInfoProgrameLabels
	 * @param updateAlphaInfoProgrameLabels
	 * @param delAlphaInfoProgrameLabels
	 * @param sysAlphaPlatformProgrameLabels
	 * @param alphaInfoProgrameLabels
	 * @param addAlphaInfoPrograms
	 * @param updateAlphaInfoPrograms
	 * @param delAlphaInfoPrograms
	 * @param sysAlphaPlatformPrograms
	 * @param alphaInfoPrograms
	 */
	public void syncAlphaProgram(List<InfoProgrameLabel> addAlphaInfoProgrameLabels, List<InfoProgrameLabel> updateAlphaInfoProgrameLabels, List<String> delAlphaInfoProgrameLabels, List<PlatformProgrameLabel> sysAlphaPlatformProgrameLabels, List<InfoProgrameLabel> alphaInfoProgrameLabels, List<InfoProgram> addAlphaInfoPrograms, List<InfoProgram> updateAlphaInfoPrograms, List<String> delAlphaInfoPrograms, List<PlatformProgram> sysAlphaPlatformPrograms, List<InfoProgram> alphaInfoPrograms) {

		//比较内测标签
		compareLabel(addAlphaInfoProgrameLabels, updateAlphaInfoProgrameLabels, delAlphaInfoProgrameLabels, sysAlphaPlatformProgrameLabels, alphaInfoProgrameLabels, "2");
		alphaLabel(addAlphaInfoProgrameLabels, updateAlphaInfoProgrameLabels, delAlphaInfoProgrameLabels);

		//比较内测节目
		compareAlphaProgram(addAlphaInfoPrograms, updateAlphaInfoPrograms, delAlphaInfoPrograms, sysAlphaPlatformPrograms, alphaInfoPrograms);
		//执行同步
		alphaProgram(addAlphaInfoPrograms, updateAlphaInfoPrograms, delAlphaInfoPrograms);

	}

	/**
	 * 1001 只有正式节目需要同步
	 *
	 * @param addInfoProgrameLabels
	 * @param updateInfoProgrameLabels
	 * @param delInfoProgrameLabels
	 * @param sysPlatformProgrameLabels
	 * @param infoProgrameLabels
	 * @param addInfoPrograms
	 * @param updateInfoPrograms
	 * @param delInfoPrograms
	 * @param sysFormalPlatformPrograms
	 * @param formalInfoPrograms
	 */
	public void syncFormalProgram(List<InfoProgrameLabel> addInfoProgrameLabels, List<InfoProgrameLabel> updateInfoProgrameLabels, List<String> delInfoProgrameLabels, List<PlatformProgrameLabel> sysPlatformProgrameLabels, List<InfoProgrameLabel> infoProgrameLabels, List<InfoProgram> addInfoPrograms, List<InfoProgram> updateInfoPrograms, List<String> delInfoPrograms, List<PlatformProgram> sysFormalPlatformPrograms, List<InfoProgram> formalInfoPrograms) {

		//比较正式标签
		compareLabel(addInfoProgrameLabels, updateInfoProgrameLabels, delInfoProgrameLabels, sysPlatformProgrameLabels, infoProgrameLabels, "1");
		formalLabel(addInfoProgrameLabels, updateInfoProgrameLabels, delInfoProgrameLabels);

		//比较正式节目
		compareFormalProgram(addInfoPrograms, updateInfoPrograms, delInfoPrograms, sysFormalPlatformPrograms, formalInfoPrograms);
		//执行同步
		formalProgram(addInfoPrograms, updateInfoPrograms, delInfoPrograms);

	}

	/**
	 * 1002 内测节目和正式节目都需要更新
	 *
	 * @param addAlphaInfoProgrameLabels
	 * @param updateAlphaInfoProgrameLabels
	 * @param delAlphaInfoProgrameLabels
	 * @param sysAlphaPlatformProgrameLabels
	 * @param alphaInfoProgrameLabels
	 * @param addAlphaInfoPrograms
	 * @param updateAlphaInfoPrograms
	 * @param delAlphaInfoPrograms
	 * @param sysAlphaPlatformPrograms
	 * @param alphaInfoPrograms
	 * @param addInfoProgrameLabels
	 * @param updateInfoProgrameLabels
	 * @param delInfoProgrameLabels
	 * @param sysPlatformProgrameLabels
	 * @param infoProgrameLabels
	 * @param addInfoPrograms
	 * @param updateInfoPrograms
	 * @param delInfoPrograms
	 * @param sysFormalPlatformPrograms
	 * @param formalInfoPrograms
	 */
	public void syncAlphaAndFormalProgram(List<InfoProgrameLabel> addAlphaInfoProgrameLabels, List<InfoProgrameLabel> updateAlphaInfoProgrameLabels, List<String> delAlphaInfoProgrameLabels, List<PlatformProgrameLabel> sysAlphaPlatformProgrameLabels, List<InfoProgrameLabel> alphaInfoProgrameLabels, List<InfoProgram> addAlphaInfoPrograms, List<InfoProgram> updateAlphaInfoPrograms, List<String> delAlphaInfoPrograms, List<PlatformProgram> sysAlphaPlatformPrograms, List<InfoProgram> alphaInfoPrograms, List<InfoProgrameLabel> addInfoProgrameLabels, List<InfoProgrameLabel> updateInfoProgrameLabels, List<String> delInfoProgrameLabels, List<PlatformProgrameLabel> sysPlatformProgrameLabels, List<InfoProgrameLabel> infoProgrameLabels, List<InfoProgram> addInfoPrograms, List<InfoProgram> updateInfoPrograms, List<String> delInfoPrograms, List<PlatformProgram> sysFormalPlatformPrograms, List<InfoProgram> formalInfoPrograms) {

		//比较并执行同步内测节目
		syncAlphaProgram(addAlphaInfoProgrameLabels, updateAlphaInfoProgrameLabels, delAlphaInfoProgrameLabels, sysAlphaPlatformProgrameLabels, alphaInfoProgrameLabels, addAlphaInfoPrograms, updateAlphaInfoPrograms, delAlphaInfoPrograms, sysAlphaPlatformPrograms, alphaInfoPrograms);

		//比较并执行正式节目
		syncFormalProgram(addInfoProgrameLabels, updateInfoProgrameLabels, delInfoProgrameLabels, sysPlatformProgrameLabels, infoProgrameLabels, addInfoPrograms, updateInfoPrograms, delInfoPrograms, sysFormalPlatformPrograms, formalInfoPrograms);
	}

	/**
	 * 同步标签与节目关系
	 *
	 * @param addInfoLabelProgramMiddles
	 * @param updateLabelProgramMiddles
	 * @param delInfoLabelProgramMiddles
	 * @param sysPlatformLabelProgramMiddles
	 * @param infoLabelProgramMiddles
	 */
	public void syncMiddle(List<InfoLabelProgramMiddle> addInfoLabelProgramMiddles, List<InfoLabelProgramMiddle> updateLabelProgramMiddles, List<String> delInfoLabelProgramMiddles, List<PlatformLabelProgramMiddle> sysPlatformLabelProgramMiddles, List<InfoLabelProgramMiddle> infoLabelProgramMiddles) {
		//比较关联关系
		this.compareMiddle(addInfoLabelProgramMiddles, updateLabelProgramMiddles, delInfoLabelProgramMiddles, sysPlatformLabelProgramMiddles, infoLabelProgramMiddles);
		//关联关系
		if (addInfoLabelProgramMiddles != null && addInfoLabelProgramMiddles.size() > 0) {
			this.infoLabelProgramMiddleService.batchAddLabelProgramMiddle(addInfoLabelProgramMiddles);
		}
		if (updateLabelProgramMiddles != null && updateLabelProgramMiddles.size() > 0) {
			this.infoLabelProgramMiddleService.batchUpdateLabelProgramMiddle(updateLabelProgramMiddles);
		}
		if (delInfoLabelProgramMiddles != null && delInfoLabelProgramMiddles.size() > 0) {
			this.infoLabelProgramMiddleService.batchPhysicalDelLabelProgramMiddle(delInfoLabelProgramMiddles);
		}
	}

	/**
	 * 同步节目背景图片
	 *
	 * @param addBizImageRecorders
	 * @param updateBizImageRecorders
	 * @param delBizImageRecorders
	 * @param sysBizImageRecorders
	 * @param bizImageRecorders
	 */
	public void syncBizImageRecorder(List<BizImageRecorder> addBizImageRecorders, List<BizImageRecorder> updateBizImageRecorders, List<String> delBizImageRecorders, List<BizImageRecorder> sysBizImageRecorders, List<BizImageRecorder> bizImageRecorders) {

		//比较节目背景图片
		this.compareBizImageRecorder(addBizImageRecorders, updateBizImageRecorders, delBizImageRecorders, sysBizImageRecorders, bizImageRecorders);
		if (addBizImageRecorders != null && addBizImageRecorders.size() > 0) {
			this.bizImageRecorderService.batchAddBizImageRecorder(addBizImageRecorders);
		}
		if (updateBizImageRecorders != null && updateBizImageRecorders.size() > 0) {
			this.bizImageRecorderService.batchUpdateBizImageRecorder(updateBizImageRecorders);
		}
		if (delBizImageRecorders != null && delBizImageRecorders.size() > 0) {
			this.bizImageRecorderService.batchPhysicalDelBizImageRecorder(delBizImageRecorders);
		}
	}

	/**
	 * 同步节目组件
	 *
	 * @param newData
	 */
	public void syncProgramComponents(List<PlatformProgramComponents> newData) {
		if(null == newData){
			return;
		}

		List<InfoProgramComponent> current = infoProgramComponentMapper.selectAll();

		Map<String, PlatformProgramComponents> map = newData.stream()
				.collect(Collectors.toMap(PlatformProgramComponents::getId, Function.identity()));

		Set<String> delProgramComponent = new HashSet<>();
		List<InfoProgramComponent> addProgramComponent = new ArrayList<>();
		for (InfoProgramComponent component : current) {
			PlatformProgramComponents sysComponent = map.get(component.getId());
			if (sysComponent == null) {
				delProgramComponent.add(component.getId());
			}
			//不论是否有改变都需要从map中移除
			map.remove(component.getId());
		}

		for (Map.Entry<String, PlatformProgramComponents> entry : map.entrySet()) {
			PlatformProgramComponents component = entry.getValue();
			InfoProgramComponent infoProgramComponent = new InfoProgramComponent();
			BeanUtils.copyProperties(component, infoProgramComponent);
			infoProgramComponent.setCode(component.getcCode());
			addProgramComponent.add(infoProgramComponent);
		}

		map.clear();

		if (CollectionUtils.isNotEmpty(addProgramComponent)) {
			this.infoProgramComponentMapper.batchAddProgramComponent(addProgramComponent);
		}
		if (CollectionUtils.isNotEmpty(delProgramComponent)) {
			Example example = new Example(InfoProgramComponent.class);
			example.createCriteria().andIn("id", delProgramComponent);
			this.infoProgramComponentMapper.deleteByExample(example);
		}
	}


	/**
	 * 同步组件与节目关系
	 *
	 * @param newData
	 */
	public void syncComponentMiddle(List<PlatformProgramComponentsMiddle> newData) {
		if(null == newData){
			return;
		}

		List<InfoProgramComponentMiddle> current = infoProgramComponentMiddleMapper.selectAll();

		Map<String, PlatformProgramComponentsMiddle> map = newData.stream()
				.collect(Collectors.toMap(PlatformProgramComponentsMiddle::getId, Function.identity()));

		Set<String> delProgramComponentMiddle = new HashSet<>();

		List<InfoProgramComponentMiddle> addProgramComponentMiddle = new ArrayList<>();
		for (InfoProgramComponentMiddle middle : current) {
			PlatformProgramComponentsMiddle sysComponent = map.get(middle.getId());
			if (sysComponent == null) {
				delProgramComponentMiddle.add(middle.getId());
			}
			map.remove(middle.getId());
		}

		for (Map.Entry<String, PlatformProgramComponentsMiddle> entry : map.entrySet()) {
			PlatformProgramComponentsMiddle entryValue = entry.getValue();
			InfoProgramComponentMiddle middle = new InfoProgramComponentMiddle();
			BeanUtils.copyProperties(entryValue, middle);
			addProgramComponentMiddle.add(middle);
		}

		map.clear();

		if (CollectionUtils.isNotEmpty(addProgramComponentMiddle)) {
			infoProgramComponentMiddleMapper.batchAddProgramComponentMiddle(addProgramComponentMiddle);
		}

		if (CollectionUtils.isNotEmpty(delProgramComponentMiddle)) {
			Example example = new Example(InfoProgramComponentMiddle.class);
			example.createCriteria().andIn("id", delProgramComponentMiddle);
			this.infoProgramComponentMiddleMapper.deleteByExample(example);
		}
	}

	//节目list转map
	public Map<String, PlatformProgram> listToMapByProgram(List<PlatformProgram> list) {
		Map<String, PlatformProgram> map = new HashedMap();
		if (list != null && list.size() > 0) {
			for (PlatformProgram program : list) {
				map.put(program.getId(), program);
			}
		}
		return map;
	}

	//标签List转Map
	public Map<String, PlatformProgrameLabel> listToMapByLabel(List<PlatformProgrameLabel> list) {
		Map<String, PlatformProgrameLabel> map = new HashedMap();
		if (list != null && list.size() > 0) {
			for (PlatformProgrameLabel label : list) {
				map.put(label.getId(), label);
			}
		}
		return map;
	}

	//关联关系List转Map
	public Map<String, PlatformLabelProgramMiddle> listToMapByMiddle(List<PlatformLabelProgramMiddle> list) {
		Map<String, PlatformLabelProgramMiddle> map = new HashedMap();
		if (list != null && list.size() > 0) {
			for (PlatformLabelProgramMiddle labelProgramMiddle : list) {
				map.put(labelProgramMiddle.getId(), labelProgramMiddle);
			}
		}
		return map;
	}

	//标签List转Map
	public Map<String, BizImageRecorder> listToMapByBizImageRecorder(List<BizImageRecorder> list) {
		Map<String, BizImageRecorder> map = new HashedMap();
		if (list != null && list.size() > 0) {
			for (BizImageRecorder imageRecorder : list) {
				map.put(imageRecorder.getId(), imageRecorder);
			}
		}
		return map;
	}

	/**
	 * 比较内测节目
	 *
	 * @param addAlphaInfoPrograms     新增租户内测节目集
	 * @param updateAlphaInfoPrograms  变更租户内测节目集
	 * @param delAlphaInfoPrograms     删除租户内测节目集
	 * @param sysAlphaPlatformPrograms 平台内测节目集
	 * @param alphaInfoPrograms        租户内测节目集
	 */
	public void compareAlphaProgram(List<InfoProgram> addAlphaInfoPrograms, List<InfoProgram> updateAlphaInfoPrograms, List<String> delAlphaInfoPrograms, List<PlatformProgram> sysAlphaPlatformPrograms, List<InfoProgram> alphaInfoPrograms) {
		Map<String, PlatformProgram> map = this.listToMapByProgram(sysAlphaPlatformPrograms);
		alphaInfoPrograms = this.infoProgramService.findInfoProgramByProgramStatusAndRecorderStatus(alphaProgramStatus, recorderStatus);

		for (InfoProgram program : alphaInfoPrograms) {
			PlatformProgram sysProgram = map.get(program.getId());
			if (null != sysProgram) {
				if (!program.getProgramUpdateBatch().equals(sysProgram.getProgramUpdateBatch())) {
					program.setProgramName(sysProgram.getProgramName());
					program.setProgramVersionNum(sysProgram.getProgramVersionNum());
					program.setProgramNamePinyin(sysProgram.getProgramNamePinyin());
					program.setProgramNameJianpin(sysProgram.getProgramNameJianpin());
					program.setSysAttachmentId(sysProgram.getSysAttachmentId());
					program.setSysAttachmentUrl(sysProgram.getSysAttachmentUrl());
					program.setPhotoUrl(sysProgram.getPhotoUrl());
					program.setImgUrlInit(sysProgram.getImgUrlInit());
					program.setImgUrlSmall(sysProgram.getImgUrlSmall());
					program.setImgUrlMiddle(sysProgram.getImgUrlMiddle());
					program.setImgUrlBig(sysProgram.getImgUrlBig());
					program.setProgramUpdateBatch(sysProgram.getProgramUpdateBatch());
					program.setProgramStatus(sysProgram.getProgramStatus());
					program.setRecorderStatus(sysProgram.getRecorderStatus());
					program.setCreatTime(sysProgram.getCreatTime());
					program.setModifyTime(sysProgram.getModifyTime());
					program.setProgramType(sysProgram.getProgramType());
					updateAlphaInfoPrograms.add(program);
				}
			} else {
				delAlphaInfoPrograms.add(program.getId());
			}
			//不论是否有改变都需要从map中移除
			map.remove(program.getId());
		}
		//Map中剩下的为新增的内测节目
		for (Map.Entry<String, PlatformProgram> entry : map.entrySet()) {
			PlatformProgram platformProgram = entry.getValue();
			//判断是否有正式节目转为内测节目，如果存在先从正式节目中删除，再以内测节目加入
			InfoProgram temp = this.infoProgramService.getId(platformProgram.getId());
			if (null != temp) {
				this.infoProgramService.delId(temp.getId());
			}
			InfoProgram infoProgram = new InfoProgram();
			BeanUtils.copyProperties(platformProgram, infoProgram);
			addAlphaInfoPrograms.add(infoProgram);
		}
		map.clear();
	}

	/**
	 * 比较正式节目
	 *
	 * @param addInfoPrograms     新增租户正式节目集
	 * @param updateInfoPrograms  变更租户正式节目集
	 * @param delInfoPrograms     删除租户正式节目集
	 * @param sysPlatformPrograms 平台正式节目集
	 * @param infoPrograms        租户正式节目集
	 */
	public void compareFormalProgram(List<InfoProgram> addInfoPrograms, List<InfoProgram> updateInfoPrograms, List<String> delInfoPrograms, List<PlatformProgram> sysPlatformPrograms, List<InfoProgram> infoPrograms) {
		Map<String, PlatformProgram> map = this.listToMapByProgram(sysPlatformPrograms);
		infoPrograms = this.infoProgramService.findInfoProgramByProgramStatusAndRecorderStatus(releaseProgramStatus, recorderStatus);

		for (InfoProgram program : infoPrograms) {
			PlatformProgram sysProgram = map.get(program.getId());
			if (null != sysProgram) {
				if ((!program.getProgramVersionNum().equals(sysProgram.getProgramVersionNum())) || (!program.getProgramStatus().equals(sysProgram.getProgramStatus()))) {
					program.setProgramName(sysProgram.getProgramName());
					program.setProgramVersionNum(sysProgram.getProgramVersionNum());
					program.setProgramNamePinyin(sysProgram.getProgramNamePinyin());
					program.setProgramNameJianpin(sysProgram.getProgramNameJianpin());
					program.setSysAttachmentId(sysProgram.getSysAttachmentId());
					program.setSysAttachmentUrl(sysProgram.getSysAttachmentUrl());
					program.setPhotoUrl(sysProgram.getPhotoUrl());
					program.setImgUrlInit(sysProgram.getImgUrlInit());
					program.setImgUrlSmall(sysProgram.getImgUrlSmall());
					program.setImgUrlMiddle(sysProgram.getImgUrlMiddle());
					program.setImgUrlBig(sysProgram.getImgUrlBig());
					program.setProgramUpdateBatch(sysProgram.getProgramUpdateBatch());
					program.setProgramStatus(sysProgram.getProgramStatus());
					program.setRecorderStatus(sysProgram.getRecorderStatus());
					program.setCreatTime(sysProgram.getCreatTime());
					program.setModifyTime(sysProgram.getModifyTime());
					program.setProgramType(sysProgram.getProgramType());
					updateInfoPrograms.add(program);
				}
			} else {
				delInfoPrograms.add(program.getId());
			}
			//不论是否有改变都需要从map中移除
			map.remove(program.getId());
		}
		//Map中剩下的为新增的正式节目
		for (Map.Entry<String, PlatformProgram> entry : map.entrySet()) {
			PlatformProgram platformProgram = entry.getValue();
			//判断该数据是否存在内测数据中
			InfoProgram temp = this.infoProgramService.getId(platformProgram.getId());
			if (null != temp) {
				this.infoProgramService.delId(temp.getId());
			}

			InfoProgram infoProgram = new InfoProgram();
			BeanUtils.copyProperties(platformProgram, infoProgram);
			addInfoPrograms.add(infoProgram);
		}
		map.clear();
	}


	/**
	 * 比较定制节目
	 *
	 * @param addInfoPrograms     新增租户定制节目集
	 * @param updateInfoPrograms  变更租户定制节目集
	 * @param delInfoPrograms     删除租户定制节目集
	 * @param sysPlatformPrograms 平台定制节目集
	 * @param infoPrograms        租户定制节目集
	 */
	public void compareDZProgram(List<InfoProgram> addInfoPrograms, List<InfoProgram> updateInfoPrograms, List<String> delInfoPrograms, List<PlatformProgram> sysPlatformPrograms, List<InfoProgram> infoPrograms) {
		Map<String, PlatformProgram> map = this.listToMapByProgram(sysPlatformPrograms);
		infoPrograms = this.infoProgramService.findInfoProgramByProgramStatusAndRecorderStatus(releaseProgramStatus, recorderStatus);

		for (InfoProgram program : infoPrograms) {
			PlatformProgram sysProgram = map.get(program.getId());
			if (null != sysProgram) {
				if ((!program.getProgramVersionNum().equals(sysProgram.getProgramVersionNum())) || (!program.getProgramStatus().equals(sysProgram.getProgramStatus()))) {
					program.setProgramName(sysProgram.getProgramName());
					program.setProgramVersionNum(sysProgram.getProgramVersionNum());
					program.setProgramNamePinyin(sysProgram.getProgramNamePinyin());
					program.setProgramNameJianpin(sysProgram.getProgramNameJianpin());
					program.setSysAttachmentId(sysProgram.getSysAttachmentId());
					program.setSysAttachmentUrl(sysProgram.getSysAttachmentUrl());
					program.setPhotoUrl(sysProgram.getPhotoUrl());
					program.setImgUrlInit(sysProgram.getImgUrlInit());
					program.setImgUrlSmall(sysProgram.getImgUrlSmall());
					program.setImgUrlMiddle(sysProgram.getImgUrlMiddle());
					program.setImgUrlBig(sysProgram.getImgUrlBig());
					program.setProgramUpdateBatch(sysProgram.getProgramUpdateBatch());
					program.setProgramStatus(sysProgram.getProgramStatus());
					program.setRecorderStatus(sysProgram.getRecorderStatus());
					program.setCreatTime(sysProgram.getCreatTime());
					program.setModifyTime(sysProgram.getModifyTime());
					updateInfoPrograms.add(program);
				}
			} else {
				delInfoPrograms.add(program.getId());
			}
			//不论是否有改变都需要从map中移除
			map.remove(program.getId());
		}
		//Map中剩下的为新增的正式节目
		for (Map.Entry<String, PlatformProgram> entry : map.entrySet()) {
			PlatformProgram platformProgram = entry.getValue();
//            //判断该数据是否存在内测数据中
//            InfoProgram temp=this.infoProgramService.getId(platformProgram.getId());
//            if(null!=temp){
//                this.infoProgramService.delId(temp.getId());
//            }

			InfoProgram infoProgram = new InfoProgram();
			BeanUtils.copyProperties(platformProgram, infoProgram);
			addInfoPrograms.add(infoProgram);
		}
		map.clear();
	}

	/**
	 * 比较标签
	 *
	 * @param addInfoProgramLabels
	 * @param updateInfoProgramLabels
	 * @param delInfoProgramLabels
	 * @param sysPlatformProgramLabels
	 * @param infoProgramLabels
	 */
	public void compareLabel(List<InfoProgrameLabel> addInfoProgramLabels, List<InfoProgrameLabel> updateInfoProgramLabels, List<String> delInfoProgramLabels, List<PlatformProgrameLabel> sysPlatformProgramLabels, List<InfoProgrameLabel> infoProgramLabels, String status) {

		Map<String, PlatformProgrameLabel> map = this.listToMapByLabel(sysPlatformProgramLabels);
		infoProgramLabels = this.infoProgramLabelService.getAll(status, recorderStatus);

		for (InfoProgrameLabel label : infoProgramLabels) {
			PlatformProgrameLabel sysLabel = map.get(label.getId());
			if (null != sysLabel) {
				if (!label.getLabelUpdateBatch().equals(sysLabel.getLabelUpdateBatch())) {
					label.setLabelName(sysLabel.getLabelName());
					label.setLabelStatus(sysLabel.getLabelStatus());
					label.setLabelUpdateBatch(sysLabel.getLabelUpdateBatch());
					label.setRecorderStatus(sysLabel.getRecorderStatus());
					label.setLabelSort(sysLabel.getLabelSort());
					updateInfoProgramLabels.add(label);
				}
			} else {
				delInfoProgramLabels.add(label.getId());
			}
			//不论是否有改变都需要从map中移除
			map.remove(label.getId());
		}

		//Map中剩下的为新增的内测节目
		for (Map.Entry<String, PlatformProgrameLabel> entry : map.entrySet()) {
			PlatformProgrameLabel platformaLabel = entry.getValue();
			//判断是否有正式标签转为内测标签，如果存在先从正式标签中删除，再以内测标签加入
			InfoProgrameLabel temp = this.infoProgramLabelService.getId(platformaLabel.getId());
			if (null != temp) {
				this.infoProgramLabelService.delId(temp.getId());
			}
			InfoProgrameLabel infoProgrameLabel = new InfoProgrameLabel();
			BeanUtils.copyProperties(platformaLabel, infoProgrameLabel);
			addInfoProgramLabels.add(infoProgrameLabel);
		}
		map.clear();
	}

	/**
	 * 比较节目组件关系
	 *
	 * @param addInfoProgramLabels
	 * @param updateInfoProgramLabels
	 * @param delInfoProgramLabels
	 * @param sysPlatformProgramLabels
	 * @param infoProgramLabels
	 */
	public void compareComponentMiddle(List<InfoProgrameLabel> addInfoProgramLabels, List<InfoProgrameLabel> updateInfoProgramLabels, List<String> delInfoProgramLabels, List<PlatformProgrameLabel> sysPlatformProgramLabels, List<InfoProgrameLabel> infoProgramLabels, String status) {

		Map<String, PlatformProgrameLabel> map = this.listToMapByLabel(sysPlatformProgramLabels);
		infoProgramLabels = this.infoProgramLabelService.getAll(status, recorderStatus);

		for (InfoProgrameLabel label : infoProgramLabels) {
			PlatformProgrameLabel sysLabel = map.get(label.getId());
			if (null != sysLabel) {
				if (!label.getLabelUpdateBatch().equals(sysLabel.getLabelUpdateBatch())) {
					label.setLabelName(sysLabel.getLabelName());
					label.setLabelStatus(sysLabel.getLabelStatus());
					label.setLabelUpdateBatch(sysLabel.getLabelUpdateBatch());
					label.setRecorderStatus(sysLabel.getRecorderStatus());
					label.setLabelSort(sysLabel.getLabelSort());
					updateInfoProgramLabels.add(label);
				}
			} else {
				delInfoProgramLabels.add(label.getId());
			}
			//不论是否有改变都需要从map中移除
			map.remove(label.getId());
		}

		//Map中剩下的为新增的内测节目
		for (Map.Entry<String, PlatformProgrameLabel> entry : map.entrySet()) {
			PlatformProgrameLabel platformaLabel = entry.getValue();
			//判断是否有正式标签转为内测标签，如果存在先从正式标签中删除，再以内测标签加入
			InfoProgrameLabel temp = this.infoProgramLabelService.getId(platformaLabel.getId());
			if (null != temp) {
				this.infoProgramLabelService.delId(temp.getId());
			}
			InfoProgrameLabel infoProgrameLabel = new InfoProgrameLabel();
			BeanUtils.copyProperties(platformaLabel, infoProgrameLabel);
			addInfoProgramLabels.add(infoProgrameLabel);
		}
		map.clear();
	}


	/**
	 * 比较关联关系
	 *
	 * @param addInfoLabelProgramMiddles     新增租户关联关系集
	 * @param updateInfoLabelProgramMiddles  变更租户关联关系集
	 * @param delInfoLabelProgramMiddles     删除租户关联关系集
	 * @param sysPlatformLabelProgramMiddles 平台关联关系集
	 * @param infoLabelProgramMiddles        租户关联关系集
	 */
	public void compareMiddle(List<InfoLabelProgramMiddle> addInfoLabelProgramMiddles, List<InfoLabelProgramMiddle> updateInfoLabelProgramMiddles, List<String> delInfoLabelProgramMiddles, List<PlatformLabelProgramMiddle> sysPlatformLabelProgramMiddles, List<InfoLabelProgramMiddle> infoLabelProgramMiddles) {

		Map<String, PlatformLabelProgramMiddle> map = this.listToMapByMiddle(sysPlatformLabelProgramMiddles);
		infoLabelProgramMiddles = this.infoLabelProgramMiddleService.getAll();

		for (InfoLabelProgramMiddle middle : infoLabelProgramMiddles) {
			PlatformLabelProgramMiddle sysMiddle = map.get(middle.getId());
			if (null != sysMiddle) {
				if ((!middle.getProgramId().equals(sysMiddle.getProgramId())) || (!middle.getProgramLabelId().equals(sysMiddle.getProgramLabelId()))) {
					middle.setProgramId(sysMiddle.getProgramId());
					middle.setProgramLabelId(sysMiddle.getProgramLabelId());
					updateInfoLabelProgramMiddles.add(middle);
				}
			} else {
				delInfoLabelProgramMiddles.add(middle.getId());
			}
			//不论是否有改变都需要从map中移除
			map.remove(middle.getId());
		}
		//Map中剩下的为新增的内测节目
		for (Map.Entry<String, PlatformLabelProgramMiddle> entry : map.entrySet()) {
			PlatformLabelProgramMiddle platformaMiddle = entry.getValue();
			InfoLabelProgramMiddle infoMiddle = new InfoLabelProgramMiddle();
			BeanUtils.copyProperties(platformaMiddle, infoMiddle);
			addInfoLabelProgramMiddles.add(infoMiddle);
		}
		map.clear();
	}

	/**
	 * 比较节目图片
	 *
	 * @param addBizImageRecorders
	 * @param updateBizImageRecorders
	 * @param delBizImageRecorders
	 * @param sysBizImageRecorders
	 * @param bizImageRecorders
	 */
	public void compareBizImageRecorder(List<BizImageRecorder> addBizImageRecorders, List<BizImageRecorder> updateBizImageRecorders, List<String> delBizImageRecorders, List<BizImageRecorder> sysBizImageRecorders, List<BizImageRecorder> bizImageRecorders) {
		Map<String, BizImageRecorder> map = this.listToMapByBizImageRecorder(sysBizImageRecorders);
		bizImageRecorders = this.bizImageRecorderService.findByType("2", "3");

		for (BizImageRecorder imageRecorder : bizImageRecorders) {
			BizImageRecorder sysBizImageRecorder = map.get(imageRecorder.getId());
			if (null != sysBizImageRecorder) {
				if (!sysBizImageRecorder.getImageId().equals(imageRecorder.getImageId())) {

					updateBizImageRecorders.add(sysBizImageRecorder);
				}
			} else {
				delBizImageRecorders.add(imageRecorder.getId());
			}
			//不论是否有改变都需要从map中移除
			map.remove(imageRecorder.getId());
		}
		//Map中剩下的为新增的节目图片
		for (Map.Entry<String, BizImageRecorder> entry : map.entrySet()) {
			BizImageRecorder imageRecorder = entry.getValue();
			if (imageRecorder.getType().equals("1")) {
				imageRecorder.setType("3"); //节目类型
			}
			addBizImageRecorders.add(imageRecorder);
		}
		map.clear();

	}


	//执行同步内测节目
	public void alphaProgram(List<InfoProgram> addAlphaInfoPrograms, List<InfoProgram> updateAlphaInfoPrograms, List<String> delAlphaInfoPrograms) {
		//内测节目
		if (addAlphaInfoPrograms != null && addAlphaInfoPrograms.size() > 0) {
			this.infoProgramService.batchAddProgram(addAlphaInfoPrograms);
		}
		if (updateAlphaInfoPrograms != null && updateAlphaInfoPrograms.size() > 0) {
			this.infoProgramService.batchUpdateProgram(updateAlphaInfoPrograms);
		}
		if (delAlphaInfoPrograms != null && delAlphaInfoPrograms.size() > 0) {
			this.infoProgramService.batchPhysicalDelProgram(delAlphaInfoPrograms);
		}
	}

	//执行同步正式节目
	public void formalProgram(List<InfoProgram> addInfoPrograms, List<InfoProgram> updateInfoPrograms, List<String> delInfoPrograms) {
		//正式节目
		if (addInfoPrograms != null && addInfoPrograms.size() > 0) {
			this.infoProgramService.batchAddProgram(addInfoPrograms);
		}
		if (updateInfoPrograms != null && updateInfoPrograms.size() > 0) {
			this.infoProgramService.batchUpdateProgram(updateInfoPrograms);
		}
		if (delInfoPrograms != null && delInfoPrograms.size() > 0) {
			this.infoProgramService.batchLogicalDelProgram(delInfoPrograms);
		}
	}


	//执行同步内测标签
	public void alphaLabel(List<InfoProgrameLabel> addAlphaInfoProgrameLabels, List<InfoProgrameLabel> updateAlphaInfoProgramLabels, List<String> delAlphaInfoProgramLabels) {
		//内测节目
		if (addAlphaInfoProgrameLabels != null && addAlphaInfoProgrameLabels.size() > 0) {
			this.infoProgramLabelService.batchAddProgramLabel(addAlphaInfoProgrameLabels);
		}
		if (updateAlphaInfoProgramLabels != null && updateAlphaInfoProgramLabels.size() > 0) {
			this.infoProgramLabelService.batchUpdateProgramLabel(updateAlphaInfoProgramLabels);
		}
		if (delAlphaInfoProgramLabels != null && delAlphaInfoProgramLabels.size() > 0) {
			this.infoProgramLabelService.batchPhysicalDelProgramLabel(delAlphaInfoProgramLabels);
		}
	}

	//执行同步正式标签
	public void formalLabel(List<InfoProgrameLabel> addInfoProgrameLabels, List<InfoProgrameLabel> updateInfoProgrameLabels, List<String> delInfoProgrameLabels) {
		//标签
		if (addInfoProgrameLabels != null && addInfoProgrameLabels.size() > 0) {
			this.infoProgramLabelService.batchAddProgramLabel(addInfoProgrameLabels);
		}
		if (updateInfoProgrameLabels != null && updateInfoProgrameLabels.size() > 0) {
			this.infoProgramLabelService.batchUpdateProgramLabel(updateInfoProgrameLabels);
		}
		if (delInfoProgrameLabels != null && delInfoProgrameLabels.size() > 0) {
			this.infoProgramLabelService.batchLogicalDelProgramLabel(delInfoProgrameLabels);
		}
	}

	//修改租户节目版本号
	private void updateTenantProgarmVer(String tenantId) {

		Map<String, String> sysMap = this.platformProgramVersionSerevice.getUpdateInfo(tenantId);
		String SysProgramAlphaVer = sysMap.get("versionUpdateBatch");
		String SysProgramReleaseVer = sysMap.get("programVersionNum");
		String SysDZProgramAlphaVer = sysMap.get("customizedVersionUpdateBatch");
		String SysDZProgramReleaseVer = sysMap.get("customizedProgramVersionNum");

		this.setProgramVersion(SysProgramReleaseVer, SysProgramAlphaVer);

		this.setDZProgramVersion(SysDZProgramReleaseVer, SysDZProgramAlphaVer);
	}

}

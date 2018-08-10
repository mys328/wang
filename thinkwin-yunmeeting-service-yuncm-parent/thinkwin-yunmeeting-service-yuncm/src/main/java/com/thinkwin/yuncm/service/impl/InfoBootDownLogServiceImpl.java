package com.thinkwin.yuncm.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.thinkwin.common.dto.publish.BootDownTaskLogDto;
import com.thinkwin.common.model.BasePageEntity;
import com.thinkwin.common.model.db.InfoBootDownLog;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.yuncm.mapper.InfoBootDownLogMapper;
import com.thinkwin.yuncm.service.InfoBootDownLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User:wangxilei
 * Date:2018/5/10
 * Company:thinkwin
 */
@Service("infoBootDownLogService")
public class InfoBootDownLogServiceImpl implements InfoBootDownLogService {
    @Autowired
    private InfoBootDownLogMapper infoBootDownLogMapper;

    @Override
    public PageInfo getLogs(BasePageEntity page, String word, String state, String taskId, String startTime, String endTime) {
        return getSearchResult(page, word, state, taskId, startTime, endTime);
    }

    @Override
    public Map<String,Integer> getLogsCount(BasePageEntity page, String word, String state, String taskId, String startTime, String endTime) {
        page = null;
        state = null;
        int all=0,success=0;
        PageInfo result = getSearchResult(page, word, state, taskId, startTime, endTime);
        if(result!=null) {
            all = result.getList().size();
        }
        state = "1";
        PageInfo result1 = getSearchResult(page, word, state, taskId, startTime, endTime);
        if(result1!=null) {
            success = result1.getList().size();
        }
        int fail = all - success;
        Map<String,Integer> map = new HashMap<>();
        map.put("all",all);
        map.put("success",success);
        map.put("fail",fail);
        return map;
    }

    private PageInfo getSearchResult(BasePageEntity page, String word, String state, String taskId, String startTime, String endTime) {
        Example ex = new Example(InfoBootDownLog.class);
        Example.Criteria ec = ex.createCriteria();
        if(StringUtils.isNotBlank(taskId)) {
            ec.andEqualTo("bootDownTaskId", taskId);
        }
        if(StringUtils.isNotBlank(word)){
            ec.andLike("terminalName", "%"+word+"%");
        }
        if(StringUtils.isNotBlank(state)) {
            ec.andEqualTo("runStatus", state);
        }
        if(StringUtils.isNotBlank(startTime)) {
            ec.andGreaterThan("runTime",startTime);
        }
        if(StringUtils.isNotBlank(endTime)) {
            ec.andLessThan("runTime",endTime);
        }
        ex.selectProperties("id");
        ex.selectProperties("cmdContent");
        ex.selectProperties("runStatus");
        ex.selectProperties("runTime");
        ex.setOrderByClause("run_time desc");
        if(page!=null) {
            PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        }
        List<InfoBootDownLog> logs = infoBootDownLogMapper.selectByExample(ex);
        PageInfo<InfoBootDownLog> pageInfo = new PageInfo<>(logs);
        if(logs!=null&&logs.size()>0){
            List<BootDownTaskLogDto> logDtos = new ArrayList<>();
            for (InfoBootDownLog log:logs){
                BootDownTaskLogDto dto = new BootDownTaskLogDto();
                dto.setId(log.getId());
                dto.setCmdContent(log.getCmdContent());
                dto.setRunStatus(log.getRunStatus());
                dto.setRunTime(log.getRunTime());
                logDtos.add(dto);
            }
            PageInfo<BootDownTaskLogDto> info = new PageInfo<>(logDtos);
            info.setTotal(pageInfo.getTotal());
            info.setPages(pageInfo.getPages());
            info.setPageNum(pageInfo.getPageNum());
            return info;
        }
        return null;
    }

    @Override
    public boolean insertLog(InfoBootDownLog log) {
        if(null!=log){
            log.setId(CreateUUIdUtil.Uuid());
            int i = infoBootDownLogMapper.insertSelective(log);
            if(i>0){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean clearLog(String taskId) {
        if(StringUtils.isNotBlank(taskId)){
            Example ex = new Example(InfoBootDownLog.class);
            ex.createCriteria().andEqualTo("bootDownTaskId",taskId);
            int i = infoBootDownLogMapper.deleteByExample(ex);
            if(i>0){
                return true;
            }
        }
        return false;
    }
}

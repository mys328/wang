package com.thinkwin.yuncm.util;

import com.thinkwin.common.dto.publish.TerminalDto;
import com.thinkwin.common.dto.publish.TerminalMessageDto;
import com.thinkwin.common.dto.publish.TerminalProgramDto;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import com.thinkwin.common.vo.publish.TerminalMessageVo;
import com.thinkwin.common.vo.publish.TerminalProgramVo;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

public class TerminalVoUtil {

	public static TerminalDto fromInfoTerminal(InfoReleaseTerminal terminal
			, TerminalMessageDto msg
			, TerminalProgramDto p){

		TerminalDto dto = new TerminalDto();
		dto.setId(terminal.getId());
		dto.setTenantId(terminal.getTenantId());
		dto.setName(terminal.getTerminalName());
		if(StringUtils.isBlank(dto.getName())){
			dto.setName(terminal.getHardwareId());
		}
		dto.setBackgroundUrl(terminal.getBackgroundUrl());
		dto.setHardwareId(terminal.getHardwareId());
		dto.setVersion(terminal.getCurrClientVer());
		if(null == terminal.getStatus()){
			dto.setStatus(0);
		} else{
			dto.setStatus(Integer.parseInt(terminal.getStatus()));
		}

		if(msg != null && msg.getMessageId() != null){
			TerminalMessageVo msgVo = new TerminalMessageVo();
			msgVo.setId(msg.getMessageId());
			msgVo.setContent(msg.getContent());
			msgVo.setStatus(msg.getStatus());
			Date expiryTime = msg.getExpiryTime();
			if(null != expiryTime) {
				int i = new Date().compareTo(expiryTime);
				if (i < 0) {
					dto.setMessage(msgVo);
				}
			}
		}

		if(p != null && p.getProgramId() != null){
            Date endTime = p.getEndTime();
            boolean longPlay = p.isLongPlay();
            if(null != endTime && !longPlay && "1".equals(p.getReleaseStatus())){
                int i = new Date().compareTo(endTime);
                if (i < 0) {
                    TerminalProgramVo programVo = new TerminalProgramVo();
                    dto.setProgram(programVo);
                    programVo.setId(p.getProgramId());
                    programVo.setName(p.getName());
                    if(p.getStartTime() != null){
                        programVo.setStartTime(p.getStartTime().getTime());
                    }
                    if(StringUtils.isNotBlank(p.getStatus())){
                        programVo.setStatus(Integer.parseInt(p.getStatus()));
                    }
                }
            }else if(longPlay && "1".equals(p.getReleaseStatus())){
                TerminalProgramVo programVo = new TerminalProgramVo();
                dto.setProgram(programVo);
                programVo.setId(p.getProgramId());
                programVo.setName(p.getName());
                programVo.setStartTime(new Date().getTime());
                programVo.setStatus(1);
            }
		}

		return dto;
	}

}

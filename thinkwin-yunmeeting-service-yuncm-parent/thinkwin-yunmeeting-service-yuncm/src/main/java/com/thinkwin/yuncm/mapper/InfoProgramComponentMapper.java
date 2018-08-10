package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.InfoProgramComponent;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface InfoProgramComponentMapper extends Mapper<InfoProgramComponent> {

	void batchAddProgramComponent(List<InfoProgramComponent> list);
}

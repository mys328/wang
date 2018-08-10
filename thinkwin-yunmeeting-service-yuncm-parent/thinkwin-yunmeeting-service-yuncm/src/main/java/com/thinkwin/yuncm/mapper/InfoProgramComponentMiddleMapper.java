package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.model.db.InfoProgramComponentMiddle;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface InfoProgramComponentMiddleMapper extends Mapper<InfoProgramComponentMiddle> {

	void batchAddProgramComponentMiddle(List<InfoProgramComponentMiddle> list);

}

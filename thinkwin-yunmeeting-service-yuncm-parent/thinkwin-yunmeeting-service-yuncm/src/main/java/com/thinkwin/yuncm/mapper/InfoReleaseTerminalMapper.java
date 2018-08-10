package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.dto.publish.TerminalStatsDto;
import com.thinkwin.common.model.db.InfoReleaseTerminal;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface InfoReleaseTerminalMapper extends Mapper<InfoReleaseTerminal> {
	List<InfoReleaseTerminal> searchTerminal(@Param("word")String word
			, @Param("status")Integer status
			, @Param("type")String type
			, @Param("isInnerTest")String isInnerTest);

	TerminalStatsDto getTerminalStats(@Param("word")String word, @Param("isInnerTest")String isInnerTest);

	List<InfoReleaseTerminal> selectTerminals(@Param("terminals")List<String> terminals);
}
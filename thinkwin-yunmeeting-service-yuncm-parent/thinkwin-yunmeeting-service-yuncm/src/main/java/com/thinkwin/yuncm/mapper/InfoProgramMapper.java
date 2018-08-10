package com.thinkwin.yuncm.mapper;

import com.thinkwin.common.dto.publish.TerminalProgramCommandDto;
import com.thinkwin.common.dto.publish.TerminalProgramDto;
import com.thinkwin.common.model.db.InfoProgram;
import com.thinkwin.common.vo.publish.TenantProgramVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface InfoProgramMapper extends Mapper<InfoProgram> {

	List<TerminalProgramDto> selectProgramByTerminalIds(@Param("terminalIds")List<String> terminalIds
			, @Param("isInnerTest")String isInnerTest);

	TerminalProgramCommandDto getProgramCommandById(@Param("id")String id
			, @Param("isInnerTest")String isInnerTest);

	List<TerminalProgramDto> getProgramByTag(@Param("tagId")String tagId
			, @Param("isInnerTest")String isInnerTest);

	List<InfoProgram> findInfoProgramByProgramStatusAndRecorderStatus(Map<String,String> map);

    TerminalProgramCommandDto queryProgramCommandById(@Param("id")String id
            , @Param("isInnerTest")String isInnerTest);

	List<TenantProgramVo> getDingZhiProgram(@Param("isInnerTest")String isInnerTest);

	boolean getCustomProgramStatus(@Param("isInnerTest")String isInnerTest);

    /**
     * 批量添加节目
     * @param lists
     * @return
     */
    public boolean batchAddProgram(List<InfoProgram> lists);

    /**
     * 批量更新节目
     * @param lists
     * @return
     */
    public boolean batchUpdateProgram(List<InfoProgram> lists);

    /**
     * 批量逻辑删除节目
     * @param ids
     * @return
     */
    public boolean batchLogicalDelProgram(List<String> ids);

    /**
     * 批量物理删除节目
     * @param ids
     * @return
     */
    public boolean batchPhysicalDelProgram(List<String> ids);


    /**
     * 按状态物理删除节目
     * @param programStatus
     * @return
     */
    boolean batchPhysicalDelProgramByProgramStatus(String programStatus);



}
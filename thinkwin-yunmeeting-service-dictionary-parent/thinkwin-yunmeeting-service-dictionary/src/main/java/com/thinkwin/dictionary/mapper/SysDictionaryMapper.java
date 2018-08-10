package com.thinkwin.dictionary.mapper;

import com.thinkwin.common.model.db.SysDictionary;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysDictionaryMapper extends Mapper<SysDictionary> {

    /**
     * 根据字典id查看字典所有子级
     * @param dictId
     * @return
     */
    List<SysDictionary> selectAllSysDictionary(String dictId);
}
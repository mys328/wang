package com.thinkwin.web.controller;

import com.thinkwin.common.businessException.BusinessExceptionStatusEnum;
import com.thinkwin.common.model.db.SysAttachment;
import com.thinkwin.common.model.db.SysDictionary;
import com.thinkwin.common.response.ResponseResult;
import com.thinkwin.common.utils.ResponseResultAuxiliaryUtil;
import com.thinkwin.common.vo.FastdfsVo;
import com.thinkwin.dictionary.service.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
  *  开发人员:daipengkai
  *  创建时间:2017/6/14
 */
@Controller
@RequestMapping(value = "/dictionary")
public class DictionaryController {
    private final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 新建字典数据
     * @param sysDictionary
     * @return
     */
    @RequestMapping("/insertSysDictionary")
    @ResponseBody
    public Object insertSysDictionary(SysDictionary sysDictionary) {

            sysDictionary = this.dictionaryService.insertDictionary(sysDictionary);
            if(sysDictionary != null){

              return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),sysDictionary);
            }else{
              return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
            }
    }

    /**
     * 修改字典数据
     * @param sysDictionary
     * @return
     */
    @RequestMapping("/updateSysDictionary")
    @ResponseBody
    public Object updateSysDictionary(SysDictionary sysDictionary) {

            sysDictionary=this.dictionaryService.updateDictionary(sysDictionary);
            if(sysDictionary != null){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),sysDictionary);

            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
            }
    }

    /**
     * 删除字典
     * @param dictId 字典ID
     * @return
     */
    @RequestMapping("/deleteSysDictionary")
    @ResponseBody
    public Object deleteSysDictionary(String dictId) {

            dictId="bffa4e13cea0486ba5f3f12c23d9757f";
            boolean success=this.dictionaryService.deleteDictionary(dictId);
            if(success){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription());
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
            }
    }

    /**
     * 根据字典code查看单条
     * @param dicCode
     * @return
     */
    @RequestMapping("/selectByIdSysDictionary")
    @ResponseBody
    public Object selectByIdSysDictionary(String dicCode) {

            SysDictionary sysDictionary = this.dictionaryService.selectByIdSysDictionary(dicCode);
            if(sysDictionary != null){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),sysDictionary);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
            }
    }

    /**
     * 根据字典code查看单条
     * @param dicCode
     * @return
     */
    @RequestMapping("/selectSysDictionary")
    @ResponseBody
    public Object selectSysDictionary(String dictid) {

            List<SysDictionary> sysDictionary = this.dictionaryService.selectSysDictionary(dictid);
            if(sysDictionary != null){
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(1, BusinessExceptionStatusEnum.Success.getDescription(),sysDictionary);
            }else{
                return ResponseResultAuxiliaryUtil.responseResultAuxiliaryUtil(0, BusinessExceptionStatusEnum.Failure.getDescription());
            }
    }



}

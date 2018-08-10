package com.thinkwin.serialnumber.service.impl;

import com.thinkwin.common.model.db.SerialNumberGeneration;
import com.thinkwin.common.utils.CreateUUIdUtil;
import com.thinkwin.common.utils.TimeUtil;
import com.thinkwin.serialnumber.mapper.SerialNumberGenerationMapper;
import com.thinkwin.serialnumber.service.SerialNumberService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
  *  获取序列号接口实现
  *
  *  开发人员:daipengkai
  *  创建时间:2017/6/30
  *
  */
@Service("serialNumberService")
public class SerialNumberServiceImpl implements SerialNumberService {

    @Autowired
    SerialNumberGenerationMapper serialNumberGenerationMapper;

    @Override
    public Object generateSerialNumber(String businessType, Map<String, String> agents) {
        SerialNumberGeneration serialNumberGeneration=new SerialNumberGeneration();

        //将数据插入到数据库
        serialNumberGeneration.setBusinessType(businessType);
        serialNumberGeneration.setCreateTime(new Date());
        serialNumberGeneration.setId(CreateUUIdUtil.Uuid());
        serialNumberGenerationMapper.insert(serialNumberGeneration);
        //获取当前时间
        String timeString=TimeUtil.timeString();
        if("order".equals(businessType)){
            /**
             * 订单生成 年的后两位+月日+5,5位流水号从10000开始 ，位流水号的方式：如17080810005
             */
            //获取时间

            //获取之前的生成的信息条数
            Example example=new Example(SerialNumberGeneration.class,true,true);
            example.setCountProperty("id");
            example.or().andEqualTo("businessType",businessType);
            List<SerialNumberGeneration> generationList=serialNumberGenerationMapper.selectByExample(example);
            String serialNumber=null;
            int count=10000;
            count=count+generationList.size()+1;
            serialNumber=timeString+count;
            return serialNumber;
        }
        if("coupon".equals(businessType)){
            /**
             * 优惠券的生成规则：
             * 类型代码+生成日期+5位随机码
             * 类型：
             * 折扣劵（代码Z），会议室劵（代码H），存储空间劵（代码K）。
             * 例如：Z17063057642 代表折扣劵，17年6月30日生成。
             */
            List<String> strList=new ArrayList<String>();
            if(agents!=null){
                String z=agents.get("Z");
                String h=agents.get("H");
                String k=agents.get("K");
                int num=0;
               if(StringUtils.isNotBlank(z)){
                   num=num+Integer.parseInt(z);
               }
               if(StringUtils.isNotBlank(h)){
                   num=num+Integer.parseInt(h);
               }
               if(StringUtils.isNotBlank(k)){
                   num=num+Integer.parseInt(k);
               }
               List<Integer> array=TimeUtil.getArray(num);
               if(StringUtils.isNotBlank(z)){
                    for(int i=0;i<Integer.parseInt(z);i++){
                       strList.add("Z"+timeString+array.get(i));
                    }
                   for(int i=0;i<Integer.parseInt(z);i++){
                       array.remove(0);
                   }
               }
                if(StringUtils.isNotBlank(h)){
                    for(int i=0;i<Integer.parseInt(h);i++){
                        strList.add("H"+timeString+array.get(i));
                    }
                    for(int i=0;i<Integer.parseInt(h);i++){
                        array.remove(0);
                    }
                }
                if(StringUtils.isNotBlank(k)){
                    for(int i=0;i<Integer.parseInt(k);i++){
                        strList.add("K"+timeString+array.get(i));
                    }
                }
                return strList;
            }


        }else if("".equals(businessType)){
            /**
             *代理商订单生成 分为一级二级 生成方式待定
             */

        }

        return null;
    }
}

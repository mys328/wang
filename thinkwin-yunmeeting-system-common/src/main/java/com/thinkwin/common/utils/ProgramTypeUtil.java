package com.thinkwin.common.utils;

import com.thinkwin.common.model.db.InfoProgramComponentMiddle;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/7/23 0023.
 */
public class ProgramTypeUtil implements Serializable {
    private static final long serialVersionUID = 8987528656811328353L;




    public static String getProgramType(List<InfoProgramComponentMiddle> list){

        String type = "";
        if(list != null ){
            for (InfoProgramComponentMiddle middle : list){
                 type +=middle.getComponentsId()+",";
            }
        }else {
            type = null;
        }
        return type;
    }

}

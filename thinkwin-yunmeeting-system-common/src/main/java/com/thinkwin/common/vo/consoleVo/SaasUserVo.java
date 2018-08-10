package com.thinkwin.common.vo.consoleVo;

import com.thinkwin.common.model.console.SaasUser;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SaasUserVo extends SaasUser implements Serializable{
    private static final long serialVersionUID = 4379770190515134085L;

    private List<Map<String,String>> rolesName; //角色标识名称列表

    public List<Map<String, String>> getRolesName() {
        return rolesName;
    }

    public void setRolesName(List<Map<String, String>> rolesName) {
        this.rolesName = rolesName;
    }
}

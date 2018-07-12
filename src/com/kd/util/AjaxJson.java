package com.kd.util;

import java.util.Map;


/**
 * <b>Application name:</b> AjaxJson.java <br>
 * <b>Application describing:$.ajax后需要接受的JSON </b> <br>
 * <b>Copyright:</b> Copyright &copy; 2015 zhursh 版权所有。<br>
 * <b>Company:</b> zhursh <br>
 * <b>Date:</b> 2015-6-29 <br>
 * @author <a href="mailto:zhursh133@sina.com"> zhursh </a>
 * @version V1.0
 */
public class AjaxJson {

    private boolean success = true;// 是否成功

    private String msg = "操作成功";// 提示信息

    private Object obj = null;// 其他信息

    private Map<String, Object> attributes;// 其他参数

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}

package com.huimin.util;


/**
 * 
 * @author zhuliang
 *
 * @Date 2018年2月24日下午5:12:01
 */
public enum ResultEnum {

    OK(10200,  "ok"),
    ERROR(10500, "服务器内部错误"),
    BAD_REQUEST(10400, "非法参数"),
    NOT_LOGIN(10101, "未登录"),
    TICKET_INVALID(10103, "不存在的票据"),
	TOKRN_BEING_STALEN(10102, "用户登录信息被盗取,建议修改密码");
    private int code;
    private String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    
    
}

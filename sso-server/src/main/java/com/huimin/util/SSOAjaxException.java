package com.huimin.util;


/**
 * 
 * @author zhuliang
 *
 * @date 2018年1月7日
 */
public class SSOAjaxException extends RuntimeException {

    
	private static final long serialVersionUID = 1L;
	private ResultEnum code;
    private String errorMessage;

    public SSOAjaxException() {
        super(ResultEnum.ERROR.getMessage());
        this.code = ResultEnum.ERROR;
        this.errorMessage = ResultEnum.ERROR.getMessage();
    }

    public SSOAjaxException(ResultEnum code) {
        super(code.getMessage());
        this.code = code;
        this.errorMessage = code.getMessage();
    }

    public SSOAjaxException(ResultEnum code, String message) {
        super(message);
        this.code = code;
        this.errorMessage = message;
    }

    public ResultEnum getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

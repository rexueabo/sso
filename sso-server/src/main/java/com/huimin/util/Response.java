package com.huimin.util;


import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;



/**
 * 
 * @author zhuliang
 *
 * @date 2018年1月7日
 */
public class Response extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 1547756933384804412L;

    /**
     * 返回操作成功的结果信息
     *
     * @return
     */
    public static ResponseBuilder ok() {
        ResponseBuilder builder = new ResponseBuilder();
        builder.setCode(ResultEnum.OK.getCode()).setMessage(ResultEnum.OK.getMessage());
        return builder;
    }

    /**
     * 返回操作失败的结果信息
     *
     * @return
     */
    public static ResponseBuilder error() {
        return error(ResultEnum.ERROR);
    }

    /**
     * 返回操作失败的结果信息
     *
     * @param code
     * @return
     */
    public static ResponseBuilder error(ResultEnum code) {
        ResponseBuilder builder = new ResponseBuilder();
        builder.setCode(code.getCode()).setMessage(code.getMessage());
        return builder;
    }

    public static ResponseBuilder error(SSOAjaxException ex) {
        ResponseBuilder builder = new ResponseBuilder();
        builder.setCode(ex.getCode().getCode());
        if (StringUtils.isNotBlank(ex.getErrorMessage())) {
            builder.setMessage(ex.getErrorMessage());
        } else {
            builder.setMessage(ex.getCode().getMessage());
        }
        return builder;
    }

    /**
     * 添加响应信息
     *
     * @param key
     * @param value
     * @return
     */
    private Response add(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public static class ResponseBuilder {

        private Response root = new Response();
        private Response data;

        public ResponseBuilder setCode(Object value) {
            this.root.add("code", value);
            return this;
        }

        public ResponseBuilder setMessage(Object value) {
            this.root.add("message", value);
            return this;
        }

        public ResponseBuilder setDataDetail(Object value) {
            if (data == null) {
                data = new Response();
            }
            this.data.add("detail", value);
            return this;
        }

        public ResponseBuilder setDataList(Object value) {
            if (data == null) {
                data = new Response();
            }
            this.data.add("list", value);
            return this;
        }

        public ResponseBuilder addData(String key, Object value) {
            if (data == null) {
                data = new Response();
            }
            this.data.add(key, value);
            return this;
        }

        public Response build() {
            this.root.add("data", data);
            return root;
        }
    }

}

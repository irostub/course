package com.example.dto;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private Map<String, String> params = new HashMap<>();
    private String body;

    public Map<String, String> getParams() {
        return params;
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

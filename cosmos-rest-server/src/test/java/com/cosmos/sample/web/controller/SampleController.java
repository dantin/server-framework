package com.cosmos.sample.web.controller;

import com.cosmos.sample.service.SampleService;
import com.cosmos.server.commons.annotations.RequestMapping;
import com.cosmos.server.commons.annotations.ResponseBody;
import com.cosmos.server.commons.annotations.Rest;
import com.cosmos.server.commons.constant.http.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Sample controller
 */
@Controller
@Rest
@RequestMapping("/sample")
public class SampleController {

    @Autowired
    private SampleService sampleService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody SampleResult<String> getBind() {
        String message = sampleService.doSample();
        SampleResult<String> response = new SampleResult<>();
        response.data = message;
        return response;
    }

    static class SampleResult<T> {
        private int errorCode = 0;
        private String errorMsg = "success";
        private T data;
    }
}

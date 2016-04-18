package com.cosmos.sample.service.impl;

import com.cosmos.sample.service.SampleService;
import org.springframework.stereotype.Service;

/**
 * {@link SampleService} implementation.
 */
@Service("sampleService")
public class SampleServiceImpl implements SampleService {

    @Override
    public String doSample() {
        return "hello";
    }
}

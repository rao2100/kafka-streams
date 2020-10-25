package com.rao2100.kstreamApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "usecase", havingValue = "rao")
public class RaoUseCase implements Runnable{

    private static Logger LOG = LoggerFactory.getLogger(RaoUseCase.class);

    @Override
    public void run() {
        // TODO Auto-generated method stub

        LOG.info("########################################");
        LOG.info("running RaoUseCase");
        LOG.info("########################################");

    }
    
}
package com.rao2100.kstreamApp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties
@Component
public class AppConfig {

 
    String bootstrapServers;
    String schemaRegistryUrl;
    String inputTopic;
    String outputTopic;
    String intermediateTopic;
    String appId;
    int produceEventCount;


    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public void setSchemaRegistryUrl(String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

    public void setOutputTopic(String outputTopic) {
        this.outputTopic = outputTopic;
    }

    public int getProduceEventCount() {
        return produceEventCount;
    }

    public void setProduceEventCount(int produceEventCount) {
        this.produceEventCount = produceEventCount;
    }
    
    

    @Override
    public String toString() {
        return "AppConfig [bootstrapServers=" + bootstrapServers + ", inputTopic=" + inputTopic + ", outputTopic="
                + outputTopic + ", produceEventCount=" + produceEventCount + ", schemaRegistryUrl=" + schemaRegistryUrl
                + "]";
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getIntermediateTopic() {
        return intermediateTopic;
    }

    public void setIntermediateTopic(String intermediateTopic) {
        this.intermediateTopic = intermediateTopic;
    }

    
    
    
}
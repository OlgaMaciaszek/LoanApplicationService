package com.toomuchcoding;

import org.springframework.stereotype.Component;

@Component
public class FraudServiceClient {

    private static final String SERVICE_URL = "http://localhost:8090/";

    public static final String FRAUDCHECK_SUFFIX = "/fraudcheck";

    private String fraudServiceUrl;

    public FraudServiceClient() {
        fraudServiceUrl = SERVICE_URL;
    }

    public void setServiceUrl(String serviceUrl) {
        fraudServiceUrl = serviceUrl;
    }

    public String getServiceUrl(){
        return  fraudServiceUrl + FRAUDCHECK_SUFFIX;
    }




}

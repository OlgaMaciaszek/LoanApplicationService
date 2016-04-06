package com.blogspot.toomuchcoding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.blogspot.toomuchcoding.model.FraudCheckStatus;
import com.blogspot.toomuchcoding.model.FraudServiceRequest;
import com.blogspot.toomuchcoding.model.FraudServiceResponse;
import com.blogspot.toomuchcoding.model.LoanApplication;
import com.blogspot.toomuchcoding.model.LoanApplicationResult;
import com.blogspot.toomuchcoding.model.LoanApplicationStatus;

@Service
public class LoanApplicationService {

    private final FraudServiceClient fraudServiceClient;

    private static final String FRAUD_SERVICE_JSON_VERSION_1 =
            "application/vnd.fraud.v1+json";

    private final RestTemplate restTemplate;

    @Autowired
    public LoanApplicationService(FraudServiceClient fraudServiceClient) {
        this.fraudServiceClient = fraudServiceClient;
        this.restTemplate = new RestTemplate();
    }

    public LoanApplicationResult loanApplication(LoanApplication loanApplication) {
        FraudServiceRequest request =
                new FraudServiceRequest(loanApplication);

        FraudServiceResponse response =
                sendRequestToFraudDetectionService(request);

        return buildResponseFromFraudResult(response);
    }

    private FraudServiceResponse sendRequestToFraudDetectionService(
            FraudServiceRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, FRAUD_SERVICE_JSON_VERSION_1);

        ResponseEntity<FraudServiceResponse> response =
                restTemplate.exchange(fraudServiceClient.getServiceUrl(), HttpMethod.PUT,
                        new HttpEntity<>(request, httpHeaders),
                        FraudServiceResponse.class);

        return response.getBody();
    }

    private LoanApplicationResult buildResponseFromFraudResult(FraudServiceResponse response) {
        LoanApplicationStatus applicationStatus = null;
        if (FraudCheckStatus.OK == response.getFraudCheckStatus()) {
            applicationStatus = LoanApplicationStatus.LOAN_APPLIED;
        } else if (FraudCheckStatus.FRAUD == response.getFraudCheckStatus()) {
            applicationStatus = LoanApplicationStatus.LOAN_APPLICATION_REJECTED;
        }

        return new LoanApplicationResult(applicationStatus, response.getRejectionReason());
    }

}

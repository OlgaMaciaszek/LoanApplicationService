package com.blogspot.toomuchcoding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.blogspot.toomuchcoding.model.FraudCheckStatus;
import com.blogspot.toomuchcoding.model.FraudServiceRequest;
import com.blogspot.toomuchcoding.model.FraudServiceResponse;
import com.blogspot.toomuchcoding.model.LoanApplication;
import com.blogspot.toomuchcoding.model.LoanApplicationResult;
import com.blogspot.toomuchcoding.model.LoanApplicationStatus;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;

@Service
public class LoanApplicationService {

    private static final String FRAUD_SERVICE_JSON_VERSION_1 =
            "application/vnd.fraud.v1+json";

    private final ServiceRestClient serviceRestClient;

    @Autowired
    public LoanApplicationService(ServiceRestClient serviceRestClient) {
        this.serviceRestClient = serviceRestClient;
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
                serviceRestClient.forService("fraudDetectionService").put().onUrl("/fraudcheck").httpEntity(
                        new HttpEntity<>(request, httpHeaders))
                        .andExecuteFor().aResponseEntity().ofType(FraudServiceResponse.class);

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

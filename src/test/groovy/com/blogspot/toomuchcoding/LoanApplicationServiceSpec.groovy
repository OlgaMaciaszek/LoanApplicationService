package com.blogspot.toomuchcoding

import com.blogspot.toomuchcoding.model.Client
import com.blogspot.toomuchcoding.model.LoanApplication
import com.blogspot.toomuchcoding.model.LoanApplicationResult
import com.blogspot.toomuchcoding.model.LoanApplicationStatus
import com.github.tomakehurst.wiremock.junit.WireMockClassRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@WebIntegrationTest(randomPort = true)
@ActiveProfiles("test")
class LoanApplicationServiceSpec extends Specification {

    @ClassRule
    @Shared
    WireMockClassRule wireMockRule = new WireMockClassRule()

    @Autowired
    LoanApplicationService loanApplicationService

    def 'should successfully apply for loan'() {
        given:
            LoanApplication application =
                    new LoanApplication(client: new Client(pesel: '12345678902'), amount: 123.123)
        when:
            LoanApplicationResult loanApplication = loanApplicationService.loanApplication(application)
        then:
            loanApplication.loanApplicationStatus == LoanApplicationStatus.LOAN_APPLIED
            loanApplication.rejectionReason == null
    }

    def 'should be rejected due to abnormal loan amount'() {
        given:
            LoanApplication application =
                    new LoanApplication(client: new Client(pesel: '12345678902'), amount: 99_999)
        when:
            LoanApplicationResult loanApplication = loanApplicationService.loanApplication(application)
        then:
            loanApplication.loanApplicationStatus == LoanApplicationStatus.LOAN_APPLICATION_REJECTED
            loanApplication.rejectionReason == 'Amount too high'
    }


}

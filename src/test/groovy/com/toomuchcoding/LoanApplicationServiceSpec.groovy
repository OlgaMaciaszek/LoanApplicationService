package com.toomuchcoding

import com.toomuchcoding.model.Client
import com.toomuchcoding.model.LoanApplication
import com.toomuchcoding.model.LoanApplicationResult
import com.toomuchcoding.model.LoanApplicationStatus
import io.codearte.accurest.stubrunner.junit.AccurestRule
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

    public static final String LOCAL_MAVEN = "/home/omaciaszek/.m2/repository/"
    public static final String FRAUD_SERVICE_ID = "com.toomuchcoding:fraudDetectionService"

    @ClassRule
    @Shared
    AccurestRule accurestRule = new AccurestRule().repoRoot(LOCAL_MAVEN)
            .downloadStub(FRAUD_SERVICE_ID).withPort(8090)

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

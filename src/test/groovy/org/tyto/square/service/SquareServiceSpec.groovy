package org.tyto.square.service

import org.tyto.square.TestBase
import org.tyto.square.client.SquareRestClient
import org.tyto.square.configuration.SquareConfiguration
import org.tyto.square.exception.SquareStateIdException
import org.tyto.square.exception.SquareTokenRequestException
import reactor.core.publisher.Mono
import spock.lang.Unroll

class SquareServiceSpec extends TestBase {

    SquareOAuthService service
    SquareConfiguration config
    SquareRestClient mockClient

    def setup() {
        config = buildSquareConfig()
        mockClient = Mock()
        service = new SquareOAuthService(config, mockClient)
    }

    def "startOAuthFlow returns redirect"() {
        when:
        service.startOAuthFlow()

        then:
        1 * mockClient.getOAuthRedirect(_) >> "https://some-url"
        service.cachedStateId != null
        service.stateExpiration != null
    }

    @Unroll
    def "finishOAuthFlow returns #desc"() {
        given:
        service.cachedStateId = 2938457128
        service.stateExpiration = System.currentTimeMillis() + 1000
        and:
        String testCode = "someTestCode"

        when:
        Mono<Boolean> response = service.finishOAuthFlow(testCode, service.cachedStateId)

        then:
        1 * mockClient.authorizeApi(testCode) >> clientResponse
        response.block() == expectedResponse

        where:
        desc      | clientResponse   | expectedResponse
        "success" | Mono.just(true)  | true
        "failure" | Mono.just(false) | false
    }

    @Unroll
    def "finisheOAuthFlow throws error on #desc"() {
        given:
        service.cachedStateId = cachedStateId
        service.stateExpiration = stateExpiration

        when:
        Mono<Boolean> response = service.finishOAuthFlow("doesnt-matter", givenStateId)

        then:
        0 * mockClient.authorizeApi(_)
        response.onErrorResume(error -> {
            if (error instanceof SquareStateIdException) {
                return Mono.just(true)
            } else {
                return Mono.error(error)
            }
        })

        where:
        desc                      | cachedStateId | givenStateId     | stateExpiration
        "stateId mismatch"        | 38275791932   | 238857271478     | System.currentTimeMillis() + 1000
        "cachedStateId is null"   | null          | 2838495223984757 | System.currentTimeMillis() + 1000
        "stateId is null"         | 234905782     | null             | System.currentTimeMillis() + 1000
        "stateId TTL expired"     | 234905782     | 234905782        | System.currentTimeMillis() - 1000
        "stateExpiration is null" | 234905782     | 234905782        | null
    }

    @Unroll
    def "checkIfAuthorized returns isAuthorized #desc"() {
        when:
        boolean response = service.checkIfAuthorized()

        then:
        1 * mockClient.isAuthorized() >> clientResponse
        response == clientResponse

        where:
        desc    | clientResponse
        "true"  | true
        "false" | false
    }

    @Unroll
    def "revokeAccessToken #desc"() {
        when:
        Mono<Boolean> response = service.revokeAccessToken()

        then:
        1 * mockClient.isAuthorized() >> isAuthorized
        callRevoke * mockClient.revokeToken() >> Mono.just(revokeResponse)
        response.block() == expectedResponse

        where:
        desc                             | isAuthorized | callRevoke | revokeResponse | expectedResponse
        "returns true if not authorized" | false        | 0          | false          | true
        "returns true if token revoked"  | true         | 1          | true           | true
    }

    def "revokeAccessToken returns false on error"() {
        when:
        boolean response = service.revokeAccessToken().block()

        then:
        1 * mockClient.isAuthorized() >> true
        1 * mockClient.revokeToken() >> Mono.error(new SquareTokenRequestException("Failed to revoke token"))
        !response
    }
}

package org.tyto.square.controller

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.tyto.square.service.SquareService
import org.tyto.square.exception.SquareTokenRequestException
import org.tyto.square.util.FrontendUtil
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Unroll

@ActiveProfiles(profiles = ["test"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SquareOAuthControllerSpec extends Specification {

    @SpringBean
    SquareService squareService = Mock()
    @SpringBean
    FrontendUtil frontendUtil = Mock()

    @Autowired
    TestRestTemplate template

    def "oauth start returns redirect"() {
        given:
        String redirectLocation = "/test/success"

        when:
        ResponseEntity response = template.exchange(URI.create("/square/oauth/start"), HttpMethod.GET, HttpEntity.EMPTY, String.class)

        then:
        1 * squareService.startSquareOAuthFlow() >> redirectLocation
        response.getStatusCode() == HttpStatus.OK
    }

    def "oauth finish returns success redirect on true"() {
        given:
        String code = "some123long456code"
        Long state = 38859282717483
        String pathWithQuery = "/square/oauth/finish?code=" + code + "&state=" + state

        when:
        ResponseEntity response = template.exchange(URI.create(pathWithQuery), HttpMethod.GET, HttpEntity.EMPTY, String.class)

        then:
        1 * frontendUtil.getSuccessUri() >> "/test/success"
        1 * squareService.finishSquareOAuthFlow(code, state) >> Mono.just(true)
        response.getStatusCode() == HttpStatus.OK
    }

    def "oauth finish returns failure redirect on false"() {
        given:
        String code = "some123long456code"
        Long state = 38859282717483
        String pathWithQuery = "/square/oauth/finish?code=" + code + "&state=" + state

        when:
        ResponseEntity response = template.exchange(URI.create(pathWithQuery), HttpMethod.GET, HttpEntity.EMPTY, String.class)

        then:
        1 * frontendUtil.getFailureUri() >> "/test/failure"
        1 * squareService.finishSquareOAuthFlow(code, state) >> Mono.just(false)
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    def "oauth finish returns failure redirect on server error"() {
        given:
        String code = "bad123code456"
        Long state = 1111111111111
        String pathWithQuery = "/square/oauth/finish?code=" + code + "&state=" + state

        when:
        ResponseEntity response = template.exchange(URI.create(pathWithQuery), HttpMethod.GET, HttpEntity.EMPTY, String.class)

        then:
        1 * frontendUtil.getFailureUri() >> "/test/failure"
        1 * squareService.finishSquareOAuthFlow(code, state) >> Mono.error(new SquareTokenRequestException("Failed"))
        response.getStatusCode() == HttpStatus.BAD_REQUEST
    }

    @Unroll
    def "oauth finish returns failure redirect on #desc"() {
        given:
        String pathWithQuery = "/square/oauth/finish"
        if (code != null || state != null) {
            pathWithQuery = pathWithQuery + "?"
            if (code != null && state != null) {
                pathWithQuery = pathWithQuery + "code=" + code + "&state=" + state
            } else {
                pathWithQuery = pathWithQuery + (code != null ? ("code=" + code) : "") + (state != null ? ("state=" + state) : "")
            }
        }

        when:
        ResponseEntity response = template.exchange(URI.create(pathWithQuery), HttpMethod.GET, HttpEntity.EMPTY, String.class)

        then:
        1 * frontendUtil.getFailureUri() >> "/test/failure"
        0 * squareService.finishSquareOAuthFlow(_, _)
        response.getStatusCode() == HttpStatus.BAD_REQUEST

        where:
        desc                  | code            | state
        "null code"           | null            | 1111111111111L
        "null state"          | "bad123code456" | null
        "null code and state" | null            | null
    }
}

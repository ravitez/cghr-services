package org.cghr.security.controller
import com.github.tomakehurst.wiremock.junit.WireMockRule
import groovy.sql.Sql
import org.cghr.security.model.User
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.junit.Rule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
/**
 * Created by ravitej on 27/1/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class AuthIntegrationSpec extends Specification {

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt
    @Autowired
    Auth auth

    @Shared
    List dataSet
    @Shared
    User validUser = new User(username: 'user1', password: 'secret1')
    @Shared
    User invalidUser = new User(username: 'invaliduser', password: 'secret1')

    @Shared
    String userJson = '{"id":1,"username":"user1","password":"secret1","role":{"title":"user","bitMask":2}}'

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);


    def setupSpec() {

        dataSet = new MockData().sampleData.get("user")

    }


    def setup() {


        dt.cleanInsert("user")
        dt.clean('authtoken,userlog')

    }

    /*
    Offline Mode
     */



    @spock.lang.Ignore
    def "should verify http responses for valid and invalid users"() {

        given:

        MockHttpServletResponse response = new MockHttpServletResponse()
        MockHttpServletRequest request = new MockHttpServletRequest()

        when:
        def actualJsonResp = auth.authenticate(user, response, request)

        then:
        response.status == httpStatus
        actualJsonResp == expectedJsonResp

        where:
        user        | httpStatus                   | expectedJsonResp
        validUser   | HttpStatus.OK.value()        | userJson
        invalidUser | HttpStatus.FORBIDDEN.value() | '{}'


    }

    /*
      Offline Mode
       */


    @spock.lang.Ignore
    def "should verify  response cookies for valid and invalid users"() {
        given:
        MockHttpServletResponse response = new MockHttpServletResponse()
        MockHttpServletRequest request = new MockHttpServletRequest()

        when:
        auth.authenticate(user, response, request)

        then:
        response.getCookie("username")?.getValue() == usernameCookie
        response.getCookie("userid")?.getValue() == useridCookie
        response.getCookie("user")?.getValue() == userCookie


        where:
        user        | usernameCookie | useridCookie | userCookie
        validUser   | "user1"        | '1'          | userJson
        invalidUser | null           | null         | null


    }

    /*
      Offline Mode
       */


    @spock.lang.Ignore
    def "should verify database changes on successful and failure authentications"() {
        given:
        MockHttpServletResponse response = new MockHttpServletResponse()
        MockHttpServletRequest request = new MockHttpServletRequest()

        when:
        auth.authenticate(user, response, request)

        then:
        gSql.rows("select * from authtoken").size() == authTokenEntries
        gSql.firstRow("select username,status from userlog") == userlogEntry

        where:
        user        | authTokenEntries | userlogEntry
        validUser   | 1                | [username: 'user1', status: 'success']
        invalidUser | 0                | [username: 'invaliduser', status: 'fail']

    }
    /*
    Online Mode
     */


    @spock.lang.Ignore
    def "should verify http responses for valid and invalid users in Online Mode"() {

        setup:
        dt.clean("user") //Local database will be empty initially
        MockHttpServletResponse response = new MockHttpServletResponse()
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.remoteHost = 'dummyServer'

        stubFor(post(urlEqualTo("/app/api/security/auth"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(validUser.toJson()))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(userJson)));

        stubFor(post(urlEqualTo("/app/api/security/auth"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(invalidUser.toJson()))
                .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/json")
                .withBody('{"role":{}}')));




        when:
        def actualJsonResp = auth.authenticate(user, response, request)
        String responseEntity = new RestTemplate().getForObject('http://localhost:8089/__admin', String.class)
        //Holds all the stub mappings


        then:
        responseEntity.length() != 0 //check whether the stub server is running
        response.status == httpStatus
        actualJsonResp == expectedJsonResp



        where:
        user        | httpStatus                   | expectedJsonResp
        validUser   | HttpStatus.OK.value()        | userJson
        invalidUser | HttpStatus.FORBIDDEN.value() | '{}'


    }


}

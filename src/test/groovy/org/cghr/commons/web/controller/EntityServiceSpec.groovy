package org.cghr.commons.web.controller

import groovy.sql.Sql
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.GenericGroovyXmlContextLoader
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Created by ravitej on 25/11/14.
 */
@ContextConfiguration(value = "classpath:spring-context.groovy", loader = GenericGroovyXmlContextLoader.class)
class EntityServiceSpec extends Specification {

    @Autowired
    @Subject
    EntityService entityService

    MockMvc mockMvc
    @Autowired
    DbTester dbTester
    @Shared
    def dataSet

    @Autowired
    Sql gSql

    def setupSpec() {

        dataSet = new MockData().sampleData.get('country')
    }

    def setup() {

        dbTester.cleanInsert("country")
        mockMvc = MockMvcBuilders.standaloneSetup(entityService).build()

    }

    def "should respond with the entityData"() {

        expect:
        mockMvc.perform(get('/entity/country/1'))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dataSet[0].toJson()))

        mockMvc.perform(get('/entity/country/2'))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dataSet[1].toJson()))


    }

    def "should respond with entityList"() {

        expect:
        mockMvc.perform(get('/entity/country'))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(dataSet.toJson()))

    }

    def "should respond with empty  entity for invalid and unavailable entities"() {

        expect:
        mockMvc.perform(get('/entity/dummyEntity/1'))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"))

        mockMvc.perform(get('/entity/country/999'))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"))

    }

    def "should save a new entity"() {

        given:
        dbTester.clean('country,datachangelog')
        String json = dataSet[0].toJson()

        when:
        mockMvc.perform(post('/entity/country')
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();


        then:
        gSql.firstRow("select * from country where id=?", [1]) == dataSet[0]
        gSql.rows("select * from datachangelog").size() == 1

    }

    def "should delete a given entity"() {
        when:
        mockMvc.perform(delete('/entity/country/1'))
                .andExpect(status().isOk())

        then:
        gSql.firstRow("select * from country where id=?", [1]) == null

    }


}
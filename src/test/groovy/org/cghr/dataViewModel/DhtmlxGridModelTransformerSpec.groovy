package org.cghr.dataViewModel

import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class DhtmlxGridModelTransformerSpec extends Specification {

    @Autowired
    DhtmlxGridModelTransformer transformer

    @Shared
    def dataStore = 'country'
    @Shared
    def multipleRowSql = 'select * from country where continent=?'
    @Shared
    def validParamsMultipleRow = ['asia'];
    @Shared
    def invalidParamsMultipleRow = ['dummyContinent'];

    //General
    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    @Shared
    Map sampleData=[rows: [[id: 1, data: [1, 'india', 'asia']], [id: 2, data: [2, 'pakistan', 'asia']], [id: 3, data: [3, 'srilanka', 'asia']]]]

    def setup() {
        dt.cleanInsert("country")
    }

    def "verify dhtmlxGrid transformer"() {

        expect:
        transformer.getModel(sql, params).toJson() == result.toJson()

        where:
        sql            | params                   || result
        multipleRowSql | validParamsMultipleRow   || sampleData
        multipleRowSql | invalidParamsMultipleRow || [rows: []]
    }
}
package org.cghr.commons.web.controller
import groovy.sql.Sql
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.cghr.test.db.MockData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:spring-context.xml")
class DataStoreSpec extends Specification {

    @Shared DataStore dataStore
    def data = [id: 1, name: 'india', continent: 'asia']


    @Shared
    def dataSet
    @Autowired
    Sql gSql
    @Autowired
    DbStore dbStore
    @Autowired
    DbTester dt

    def setupSpec() {
        dataSet = new MockData().sampleData.get("country")
    }

    def setup() {


        dataStore = new DataStore(dbStore)

        dt.clean("country")
        dt.clean("datachangelog")
    }

    def "should save a map to database"() {
        setup:
        Map data = dataSet[0]
        data.put("datastore", "country")

        when:
        dataStore.saveData(data)


        then:
        gSql.firstRow("select * from country where id=?", [1]) == dataSet[0]
        gSql.rows("select * from datachangelog").size()==1
    }
}

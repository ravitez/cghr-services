package org.cghr.commons.file

import groovy.sql.Sql
import org.cghr.GenericGroovyContextLoader
import org.cghr.commons.db.DbStore
import org.cghr.test.db.DbTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by ravitej on 24/4/14.
 */
@ContextConfiguration(value = "classpath:appContext.groovy", loader = GenericGroovyContextLoader.class)
class FileSystemStoreSpec extends Specification {

    FileSystemStore fileSystemStore

    @Autowired
    Sql gSql
    @Autowired
    DbTester dt

    @Autowired
    DbStore dbStore


    String userHome = File.createTempDir().absolutePath + File.separator
    Map fileStoreFactory = [memberImage: [memberConsent: userHome + 'hcDemo/images/consent/', memberPhoto: userHome + 'hcDemo/images/photo/']]

    def setupSpec() {

    }

    def setup() {

        fileSystemStore = new FileSystemStore(fileStoreFactory, dbStore)

        dt.clean('memberImage')
        dt.clean('datachangelog')
    }

    def "should create a file with a given filename and dir path"() {
        given:
        def filename = '151001001_consent.png'
        def dirPath = File.createTempDir().path


        when:
        fileSystemStore.getNewFile(dirPath, filename)

        then:
        File finalDir = new File(dirPath)
        finalDir.listFiles().length == 1

    }

    @Ignore
    def "should save the data and write consent file to appropriate path"() {

        given:
        Map formData = [memberId: '151001001', consent: '151001001_consent.png', filename: '151001001_consent', category: 'memberConsent']
        String fileStore = 'memberImage'
        byte[] content = "dummy File contents".getBytes()
        MockMultipartFile multipartFile = new MockMultipartFile("151001001_consent.png", 'fileData', "text/plain", content);


        when:
        fileSystemStore.saveOrUpdate(formData, fileStore, multipartFile)

        then:
        File dir = new File(fileStoreFactory.get(fileStore).get('memberConsent'))
        println dir.path
        dir.listFiles().length == 1
        gSql.rows('select * from memberImage').size() == 1


    }

    @Ignore
    def "should save the data and write the photo file to appropriate path"() {

        given:
        Map formData = [memberId: '151001001', photo: '151001001_photo.png', filename: '151001001_photo', category: 'memberPhoto']
        String fileStore = 'memberImage'
        byte[] content = "dummy File contents".getBytes()
        MockMultipartFile multipartFile = new MockMultipartFile("151001001_photo.png", 'fileData', "text/plain", content);

        when:
        fileSystemStore.saveOrUpdate(formData, fileStore, multipartFile)

        then:
        File dir = new File(fileStoreFactory.get(fileStore).get('memberPhoto'))
        println dir.path
        dir.listFiles().length == 1
        gSql.rows('select * from memberImage').size() == 1


    }

}
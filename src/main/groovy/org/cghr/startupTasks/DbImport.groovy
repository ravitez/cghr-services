package org.cghr.startupTasks

import groovy.sql.Sql

import javax.annotation.PostConstruct

/**
 * Created by ravitej on 25/4/14.
 */
class DbImport {

    //Create bean in appContext to load it by spring container

    String dbScriptsPath
    Sql gSql

    DbImport(String dbScriptsPath, Sql gSql) {
        this.dbScriptsPath = dbScriptsPath
        this.gSql = gSql

    }

    @PostConstruct
    void importSqlScripts() {

        File[] sqlDir = new File(dbScriptsPath).listFiles().sort() { it.name }
        sqlDir.each() {
            importSqlFile(it)
        }

    }

    void importSqlFile(File file) {

        String[] sqls = file.text.split(";")
        gSql.withBatch {
            stmt ->
                sqls.each {
                    stmt.addBatch(it)
                }

        }

    }


}

package org.cghr.commons.db

import groovy.transform.TupleConstructor

/**
 * Created by ravitej on 9/5/14.
 */
@TupleConstructor
class CleanUp {

    String excludedEntities
    DbAccess dbAccess

    void cleanupTables() {
        dbAccess.removeData(getTableListForCleanup())
    }

    List getTableListForCleanup() {
        List excludedTables = excludedEntities.split(",")
        allTables - excludedTables
    }

    List getAllTables() {

        dbAccess.rows('show tables', [])
                .collect { it.values() as List }
                .collect { List values -> values.first() }

    }
}

package org.cghr.commons.web.controller

import org.cghr.commons.db.DbAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody


@Controller
@RequestMapping("/data/dataAccessBatchService")
class DataAccessBatch {

    @Autowired
    DbAccess dbAccess

    @RequestMapping(value = "/{dataStore}/{keyField}/{keyFieldValue}", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ResponseBody
    String getDataAsJsonArray(
            @PathVariable String dataStore, @PathVariable String keyField, @PathVariable String keyFieldValue) {

        dbAccess.getRowsAsJsonArray(dataStore, keyField, keyFieldValue)
    }

    DataAccessBatch() {

    }

    DataAccessBatch(DbAccess dbAccess) {

        this.dbAccess = dbAccess
    }
}
package org.cghr.chart

import groovy.transform.TupleConstructor
import org.cghr.commons.db.DbAccess

/**
 * Created by ravitej on 4/2/15.
 */

@TupleConstructor
class AngularChartModel {


    DbAccess dbAccess

    Map getChartDataModel(String sql, List params = []) {

        List rows = dbAccess.rows(sql, params)
        List columns = dbAccess.columns(sql, params)

        [series: columns.drop(1),
         data  : transformToChartModel(rows)]

    }

    List transformToChartModel(List<Map<String, String>> rows) {

        rows.collect { Map row ->

            List values = row.values().toList()
            [x: values.head(), y: values.tail()]
        }

    }

}

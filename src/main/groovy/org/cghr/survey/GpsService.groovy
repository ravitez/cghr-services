package org.cghr.survey

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Created by ravitej on 7/4/14.
 */
@Controller
@RequestMapping("/GpsService")
class GpsService {


    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    String getGps() {

        return '{"latitude":123.123,"longitude":456.456}';

    }


}
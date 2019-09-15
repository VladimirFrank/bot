package com.sbrf.loyalist.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Index {

    @RequestMapping(
            path = "/index",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String index() {
        return "Hello world";
    }

}

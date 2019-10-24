package com.sbrf.loyalist.controller;

import com.sbrf.loyalist.entity.SberUser;
import com.sbrf.loyalist.telegram.TelegramBot;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class Index {

    private TelegramBot bot;

    public Index(TelegramBot bot) {
        this.bot = bot;
    }

    @RequestMapping(
            path = "/kick",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String index() {
        System.out.println("Вызван /kick");
        List<SberUser> kickedUsers = bot.kick();
        return kickedUsers.toString();
    }

}

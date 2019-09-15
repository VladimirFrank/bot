package com.sbrf.loyalist.entity;

import com.sbrf.loyaltist.api.User;

/**
 * Пользователь чата или группы.
 */
public class ChatUser implements User {

    private String name;
    private String telephoneNumber;

    public ChatUser(String name, String telephoneNumber) {
        this.name = name;
        this.telephoneNumber = telephoneNumber;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

}

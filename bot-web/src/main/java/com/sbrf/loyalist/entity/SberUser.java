package com.sbrf.loyalist.entity;

import org.telegram.telegrambots.meta.api.objects.User;

public class SberUser extends User {
    private String telephone;

    // TODO Наследоваться от телеграмного Юзера для доступа к полям name, lastname и пр.

    public SberUser(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}

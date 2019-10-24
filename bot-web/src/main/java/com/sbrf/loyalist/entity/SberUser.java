package com.sbrf.loyalist.entity;

import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

public class SberUser {
    private String telephone;
    private Integer id;

    // TODO Наследоваться от телеграмного Юзера для доступа к полям name, lastname и пр.

    public SberUser(String telephone, Integer id) {
        this.telephone = telephone;
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SberUser sberUser = (SberUser) o;
        return Objects.equals(id, sberUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

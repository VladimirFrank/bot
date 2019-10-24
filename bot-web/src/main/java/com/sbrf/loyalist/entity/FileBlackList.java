package com.sbrf.loyalist.entity;

import java.util.List;

public class FileBlackList implements BlackList {

    private List<BlackListEntity> blackListEntities;

    public FileBlackList(List<BlackListEntity> blackListEntities) {
        this.blackListEntities = blackListEntities;
    }

    @Override
    public List<BlackListEntity> get() {
        return blackListEntities;
    }

    @Override
    public boolean isUserInBlackList(SberUser user) {
        if (blackListEntities == null || blackListEntities.isEmpty()) {
            return false;
        } else {
            // TODO Добавить проверку по блэклисту.
            // TODO Нужно найти, как смаппить юзера на сущность блэклиста.
            return false;
        }
    }
}

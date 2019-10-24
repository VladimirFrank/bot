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
    public boolean isUserInBlackList(String phone) {
        if (blackListEntities == null || blackListEntities.isEmpty()) {
            return false;
        } else {
            for (BlackListEntity entity : blackListEntities) {
                if (entity.getTelephone() != null && entity.getTelephone().equals(phone)) {
                    return true;
                }
            }
        }

        return false;
    }
}

package com.sbrf.loyalist.entity;

import java.util.List;

public interface BlackList {

    List<BlackListEntity> get();

    boolean isUserInBlackList(String phone);

}

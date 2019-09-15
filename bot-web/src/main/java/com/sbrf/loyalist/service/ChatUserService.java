package com.sbrf.loyalist.service;

import com.sbrf.loyalist.dao.UserDao;
import com.sbrf.loyaltist.api.User;
import com.sbrf.loyaltist.api.UserService;

/**
 * Реализация сервиса по работе с пользователями чата.
 */
public class ChatUserService implements UserService {

    private final UserDao userDao;

    public ChatUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void addUser(User user) {
        // Заглушка.
    }

    @Override
    public boolean isUserInBlackList(User user) {
        return false;
    }

    @Override
    public User findUser(String telephoneNumber) {
        return null;
    }
}

package com.sbrf.loyaltist.api;

/**
 * Интерфейс сервиса для работы с пользователями чата или группы.
 */
public interface UserService {

    void addUser(User user);

    boolean isUserInBlackList(User user);

    User findUser(String telephoneNumber);

}

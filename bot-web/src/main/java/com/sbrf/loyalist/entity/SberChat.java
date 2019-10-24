package com.sbrf.loyalist.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SberChat {

    private List<SberUser> users = new ArrayList<>();

    private Long chatId;

    public SberChat(Long chatId) {
        this.chatId = chatId;
    }

    public void addUser(SberUser user) {
        users.add(user);
    }

    public boolean containsUser(SberUser user) {
        return users.contains(user);
    }

    public void deleteUserIfInChat(Integer userId) {
        Iterator<SberUser> iterator = users.iterator();
        if (iterator.hasNext()) {
            SberUser user = iterator.next();
            if (user.getId().equals(userId)) {
                iterator.remove();
            }
        }
    }

    public Long getChatId() {
        return chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SberChat sberChat = (SberChat) o;
        return Objects.equals(chatId, sberChat.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId);
    }
}

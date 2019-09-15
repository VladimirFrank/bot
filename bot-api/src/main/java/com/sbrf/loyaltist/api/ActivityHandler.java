package com.sbrf.loyaltist.api;

/**
 * Интерфейс обработчика событий в чатах или группах.
 */
public interface ActivityHandler<T, V> {

    V handle(T request);

}

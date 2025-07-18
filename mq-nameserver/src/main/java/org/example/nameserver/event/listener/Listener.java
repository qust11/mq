package org.example.nameserver.event.listener;


import org.example.nameserver.event.model.Event;

/**
 * @author qushutao
 * @since 2025/7/16 15:42
 **/
public interface Listener<T extends Event> {
    void onEvent(T event);
}

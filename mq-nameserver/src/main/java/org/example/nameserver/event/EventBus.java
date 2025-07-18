package org.example.nameserver.event;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.example.common.util.ReflectUtil;
import org.example.nameserver.event.listener.Listener;
import org.example.nameserver.event.listener.RegisterListener;
import org.example.nameserver.event.model.Event;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author qushutao
 * @since 2025/7/16 15:40
 **/
@Slf4j
public class EventBus {

    public Map<Class<? extends Event>, List<Listener>> map = new ConcurrentHashMap<>();

    ThreadPoolExecutor executor = new ThreadPoolExecutor(10,
            10, 0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("event-bus-thread" + UUID.randomUUID().toString());
                return thread;
            });

    public void init() {
        ServiceLoader<Listener> serviceLoader = ServiceLoader.load(Listener.class);
        for (Listener listener : serviceLoader) {
            // 通过反射拿到listener的泛型
            Class anInterface = ReflectUtil.getInterface(listener, 0);
            register(anInterface, listener);
        }
    }


    public <E extends Event> void register(Class<? extends Event> eventClass, Listener<E> listener) {
        map.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    public void publish(Event event) {
        List<Listener> listeners = map.get(event.getClass());
        if (CollectionUtils.isEmpty(listeners)) {
            return;
        }
        executor.execute(() -> {
            try {
                for (Listener listener : listeners) {
                    listener.onEvent(event);
                }
            } catch (Exception e) {
                log.error("event bus error", e);
            }
        });
    }
}

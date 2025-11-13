package com.example.demo.order.service;

import com.example.demo.order.OrderEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class OrderEventStore {

    private final Map<String, CopyOnWriteArrayList<OrderEvent>> store = new ConcurrentHashMap<>();

    public void recordEvent(String serviceName, OrderEvent event) {
        store.computeIfAbsent(serviceName, key -> new CopyOnWriteArrayList<>()).add(event);
    }

    public List<OrderEvent> getEvents(String serviceName) {
        return store.containsKey(serviceName)
                ? List.copyOf(store.get(serviceName))
                : List.of();
    }

    public Map<String, List<OrderEvent>> getAllEvents() {
        return store.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(entry.getValue())
                ));
    }

    public void replaceEvents(String serviceName, List<OrderEvent> events) {
        store.put(serviceName, new CopyOnWriteArrayList<>(events));
    }

    public void replaceEventsForServices(List<String> serviceNames, List<OrderEvent> events) {
        CopyOnWriteArrayList<OrderEvent> copied = new CopyOnWriteArrayList<>(events);
        for (String serviceName : serviceNames) {
            store.put(serviceName, new CopyOnWriteArrayList<>(copied));
        }
    }

    public Map<String, List<OrderEvent>> resetAndReplay(List<String> serviceNames, List<OrderEvent> events) {
        replaceEventsForServices(serviceNames, events);
        return getAllEvents();
    }

    public void clearAll() {
        store.clear();
    }

    public Map<String, List<OrderEvent>> snapshot() {
        return Collections.unmodifiableMap(getAllEvents());
    }
}

package wtf.bhopper.nonsense.event.bus;

import wtf.bhopper.nonsense.event.Event;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
public class EventBus {

    private final Map<Type, List<CallSite>> callSiteMap;
    private final Map<Type, List<Listener<Event>>> listenerCache;

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public EventBus() {
        callSiteMap = new HashMap<>();
        listenerCache = new HashMap<>();
    }

    public void subscribe(final Object subscriber) {

        for (final Field field : subscriber.getClass().getDeclaredFields()) {
            final EventLink annotation = field.getAnnotation(EventLink.class);
            if (annotation != null) {
                final Type eventType = ((ParameterizedType) (field.getGenericType())).getActualTypeArguments()[0];

                if (!field.canAccess(subscriber)) {
                    field.setAccessible(true);
                }

                try {
                    @SuppressWarnings("unchecked")
                    final Listener<Event> listener =
                            (Listener<Event>) LOOKUP.unreflectGetter(field)
                                    .invokeWithArguments(subscriber);

                    final byte priority = annotation.value();

                    final List<CallSite> callSites;
                    final CallSite callSite = new CallSite(subscriber, listener, priority);

                    if (this.callSiteMap.containsKey(eventType)) {
                        callSites = this.callSiteMap.get(eventType);
                        callSites.add(callSite);
                        callSites.sort(Comparator.comparingInt(o -> o.priority));
                    } else {
                        callSites = new ArrayList<>(1);
                        callSites.add(callSite);
                        this.callSiteMap.put(eventType, callSites);
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        this.populateListenerCache();
    }

    public void unsubscribe(final Object subscriber) {
        for (List<CallSite> callSites : this.callSiteMap.values()) {
            callSites.removeIf(eventCallSite -> eventCallSite.owner == subscriber);
        }

        this.populateListenerCache();
    }

    public void post(final Event event) {
        final List<Listener<Event>> listeners = listenerCache.getOrDefault(event.getClass(), Collections.emptyList());

        int listenersSize = listeners.size();

        while (listenersSize > 0) {
            listeners.get(--listenersSize).call(event);
        }
    }

    private void populateListenerCache() {
        final Map<Type, List<CallSite>> callSiteMap = this.callSiteMap;
        final Map<Type, List<Listener<Event>>> listenerCache = this.listenerCache;

        for (final Type type : callSiteMap.keySet()) {
            final List<CallSite> callSites = callSiteMap.get(type);
            final int size = callSites.size();
            final List<Listener<Event>> listeners = new ArrayList<>(size);

            for (CallSite callSite : callSites) {
                listeners.add(callSite.listener);
            }

            listenerCache.put(type, listeners);
        }
    }

    private record CallSite(Object owner, Listener<Event> listener, byte priority) { }

}

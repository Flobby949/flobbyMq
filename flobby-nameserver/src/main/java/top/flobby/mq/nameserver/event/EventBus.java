package top.flobby.mq.nameserver.event;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.utils.ReflectUtil;
import top.flobby.mq.nameserver.event.model.Event;
import top.flobby.mq.nameserver.event.spi.listener.Listener;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 事件总线
 * @create : 2025-04-30 15:49
 **/

public class EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);

    private static Map<Class<? extends Event>, List<Listener<? super Event>>> eventListenerMap = new ConcurrentHashMap<>();

    /**
     * 线程任务名称
     */
    private String taskName;

    public EventBus(String taskName) {
        this.taskName = taskName + "-";
    }

    public EventBus() {
        this.taskName = "event-bus-thread-";
    }

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10,
            100,
            3,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            r -> {
                Thread thread = new Thread(r);
                thread.setName(taskName + UUID.randomUUID());
                return thread;
            }
    );

    /**
     * 初始化
     */
    public void init() {
        // spi机制，jdk内置的机遇文件管理接口实现的机制
        ServiceLoader<Listener> serviceLoader = ServiceLoader.load(Listener.class);
        for (Listener listener : serviceLoader) {
            Class clazz = ReflectUtil.getInterfaceT(listener, 0);
            this.registry(clazz, listener);
        }
        LOGGER.info("事件总线初始化完成: {}", eventListenerMap);
    }

    /**
     * 注册
     *
     * @param clazz    事件类
     * @param listener 监听器
     */
    public void registry(Class<? extends Event> clazz, Listener<? super Event> listener) {
        List<Listener<? super Event>> listenerList = eventListenerMap.get(clazz);
        if (CollectionUtils.isEmpty(listenerList)) {
            eventListenerMap.put(clazz, Lists.newArrayList(listener));
        } else {
            listenerList.add(listener);
            eventListenerMap.put(clazz, listenerList);
        }
        LOGGER.info(" {} 事件的处理器 {} 注册成功", clazz.getSimpleName(), listener.getClass().getSimpleName());
    }

    /**
     * 事件发布
     *
     * @param event 事件
     */
    public void publish(Event event) {

        List<Listener<? super Event>> listenerList = eventListenerMap.get(event.getClass());
        threadPoolExecutor.execute(() -> {
            try {
                // 异步
                for (Listener<? super Event> listener : listenerList) {
                    listener.onReceive(event);
                }
            } catch (Exception e) {
                LOGGER.error("Error publishing event: {}, ", JSON.toJSONString(event), e);
            }
        });
    }

    // public static void main(String[] args) {
    //     EventBus eventBus = new EventBus();
    //     eventBus.init();
    // }
}

package com.mz.jarboot.core.advisor;

import com.mz.jarboot.core.basic.ConcurrentWeakKeyHashMap;
import com.mz.jarboot.core.basic.EnvironmentContext;
import com.mz.jarboot.core.constant.CoreConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author jianzhengma
 * 以下代码来自开源项目Arthas，进行了较大的修改
 */
public class AdviceListenerManager { //NOSONAR
    private static final Logger logger = LoggerFactory.getLogger(CoreConstant.LOG_NAME);
    private static final FakeBootstrapClassLoader FAKEBOOTSTRAPCLASSLOADER = new FakeBootstrapClassLoader();

    static {
        init();
    }

    private static void init() {
        // 清理失效的 AdviceListener
        EnvironmentContext.getScheduledExecutorService().scheduleWithFixedDelay(new Runnable() { //NOSONAR
            @Override
            public void run() {
                cleanOutDateListener();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private static void cleanOutDateListener() { //NOSONAR
        try {
            if (adviceListenerMap != null) {
                for (Map.Entry<ClassLoader, ClassLoaderAdviceListenerManager> entry : adviceListenerMap.entrySet()) {
                    ClassLoaderAdviceListenerManager adviceListenerManager = entry.getValue();
                    synchronized (adviceListenerManager) {
                        for (Map.Entry<String, List<AdviceListener>> eee : adviceListenerManager.map.entrySet()) {
                            List<AdviceListener> listeners = eee.getValue();
                            List<AdviceListener> newResult = new ArrayList<>();
                            for (AdviceListener listener : listeners) {
                                if (listener instanceof JobAware) {
                                    JobAware job = (JobAware) listener;
                                    if (EnvironmentContext.checkJobEnd(job.getSessionId(), job.getJobId())) {
                                        continue;
                                    }
                                    newResult.add(listener);
                                }
                            }

                            if (newResult.size() != listeners.size()) {
                                adviceListenerManager.map.put(eee.getKey(), newResult);
                            }

                        }
                    }
                }
            }
        } catch (Throwable e) { //NOSONAR
            logger.error("clean AdviceListener error", e);
        }
    }

    private static final ConcurrentWeakKeyHashMap<ClassLoader, ClassLoaderAdviceListenerManager> adviceListenerMap = new ConcurrentWeakKeyHashMap<>();

    static class ClassLoaderAdviceListenerManager {
        private ConcurrentHashMap<String, List<AdviceListener>> map = new ConcurrentHashMap<>();

        private String key(String className, String methodName, String methodDesc) {
            return className + methodName + methodDesc;
        }

        private String keyForTrace(String className, String owner, String methodName, String methodDesc) {
            return className + owner + methodName + methodDesc;
        }

        public void registerAdviceListener(String className, String methodName, String methodDesc,
                AdviceListener listener) {
            synchronized (this) {
                className = className.replace('/', '.');
                String key = key(className, methodName, methodDesc);

                List<AdviceListener> listeners = map.get(key); //NOSONAR
                if (listeners == null) {
                    listeners = new ArrayList<>();
                    map.put(key, listeners);
                }
                if (!listeners.contains(listener)) {
                    listeners.add(listener);
                }
            }
        }

        public List<AdviceListener> queryAdviceListeners(String className, String methodName, String methodDesc) {
            className = className.replace('/', '.');
            String key = key(className, methodName, methodDesc);
            return map.get(key);
        }

        public void registerTraceAdviceListener(String className, String owner, String methodName, String methodDesc,
                AdviceListener listener) {

            className = className.replace('/', '.');
            String key = keyForTrace(className, owner, methodName, methodDesc);
            List<AdviceListener> listeners = map.get(key); //NOSONAR
            if (listeners == null) {
                listeners = new ArrayList<>();
                map.put(key, listeners);
            }
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }

        public List<AdviceListener> queryTraceAdviceListeners(String className, String owner, String methodName,
                String methodDesc) {
            className = className.replace('/', '.');
            String key = keyForTrace(className, owner, methodName, methodDesc);
            return map.get(key);
        }
    }

    public static void registerAdviceListener(ClassLoader classLoader, String className, String methodName,
            String methodDesc, AdviceListener listener) {
        classLoader = wrap(classLoader);
        className = className.replace('/', '.');

        ClassLoaderAdviceListenerManager manager = adviceListenerMap.get(classLoader); //NOSONAR

        if (manager == null) {
            manager = new ClassLoaderAdviceListenerManager();
            adviceListenerMap.put(classLoader, manager);
        }
        manager.registerAdviceListener(className, methodName, methodDesc, listener);
    }

    public static List<AdviceListener> queryAdviceListeners(ClassLoader classLoader, String className,
            String methodName, String methodDesc) {
        classLoader = wrap(classLoader);
        className = className.replace('/', '.');
        ClassLoaderAdviceListenerManager manager = adviceListenerMap.get(classLoader);

        if (manager != null) {
            return manager.queryAdviceListeners(className, methodName, methodDesc);
        }

        return null; //NOSONAR
    }

    public static void registerTraceAdviceListener(ClassLoader classLoader, String className, String owner,
            String methodName, String methodDesc, AdviceListener listener) {
        classLoader = wrap(classLoader);
        className = className.replace('/', '.');

        ClassLoaderAdviceListenerManager manager = adviceListenerMap.get(classLoader); //NOSONAR

        if (manager == null) {
            manager = new ClassLoaderAdviceListenerManager();
            adviceListenerMap.put(classLoader, manager);
        }
        manager.registerTraceAdviceListener(className, owner, methodName, methodDesc, listener);
    }

    public static List<AdviceListener> queryTraceAdviceListeners(ClassLoader classLoader, String className,
            String owner, String methodName, String methodDesc) {
        classLoader = wrap(classLoader);
        className = className.replace('/', '.');
        ClassLoaderAdviceListenerManager manager = adviceListenerMap.get(classLoader);

        if (manager != null) {
            return manager.queryTraceAdviceListeners(className, owner, methodName, methodDesc);
        }
        return null; //NOSONAR
    }

    private static ClassLoader wrap(ClassLoader classLoader) {
        if (classLoader != null) {
            return classLoader;
        }
        return FAKEBOOTSTRAPCLASSLOADER;
    }

    private static class FakeBootstrapClassLoader extends ClassLoader {

    }
}

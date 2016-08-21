package ru.sbt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {
    /**
     * Scans object "from" for all getters. If object "to"
     * contains correspondent setter, it will invoke it
     * to set property value for "to" which equals to the property
     * of "from".
     * <p/>
     * The type in setter should be compatible to the value returned
     * by getter (if not, no invocation performed).
     * Compatible means that parameter type in setter should
     * be the same or be superclass of the return type of the getter.
     * <p/>
     * The method takes care only about public methods.
     *
     * @param to   Object which properties will be set.
     * @param from Object which properties will be used to get values.
     */
    public static void assign(Object to, Object from) throws InvocationTargetException, IllegalAccessException {
        Class objectFrom = from.getClass();
        Class objectTo = to.getClass();

        Method[] mapFrom = objectFrom.getMethods();
        Method[] mapTo = objectTo.getMethods();

        Map<String, Method> mapGetter = new HashMap<>();

        for (Method method : mapTo) {
            if (method.getName().startsWith("get") && method.getParameterCount() == 0 && method.getReturnType() != void.class)
                mapGetter.put("set" + method.getName().substring(3), method);
        }

        for (Method method : mapFrom) {
            if (method.getName().startsWith("set") && method.getParameterCount() == 1 && method.getReturnType() == void.class) {
                Method getter = mapGetter.get(method.getName());
                if (getter != null) {
                    if (method.getParameterTypes()[0].isAssignableFrom(getter.getReturnType())) {
                        method.invoke(to, getter.invoke(from));
                    }
                }
            }
        }
    }
}
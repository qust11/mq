package org.example.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectUtil {

    public static Class<?> getInterface(Object o, int index) {
        Type[] genericInterfaces = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[index];
        Type type = parameterizedType.getActualTypeArguments()[index];
        return checkType(type , index);
    }

    private static Class<?> checkType(Type type, int index) {
        if (type instanceof Class<?>){
            return (Class<?>) type;
        }else if (type instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type t = parameterizedType.getActualTypeArguments()[index];
            return checkType(t, index);
        }else{
            String className = type == null ? "unknown" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, but <" + className + "> is found.");
        }
    }

}
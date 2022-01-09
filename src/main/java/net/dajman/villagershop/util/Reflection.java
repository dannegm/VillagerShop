package net.dajman.villagershop.util;

import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class Reflection {

    public static String getVersion(){
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1) + ".";
    }

    public static int getVersionNumber(){
        final String name = Bukkit.getServer().getClass().getPackage().getName();
        final String[] splitted = name.substring(name.lastIndexOf('.') + 2).split("_");
        return Integer.parseInt(splitted[0] + splitted[1] + splitted[2].substring(1));
    }

    @Nullable
    public static Class<?> getClass(final String name){
        try{
            return Class.forName(name);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Class<?> getNmsClass(final String name){
        return getClass("net.minecraft.server." + getVersion() + name);
    }

    @Nullable
    public static Class<?> getCBClass(final String name){
        return getClass("org.bukkit.craftbukkit." + getVersion() + name);
    }

    @Nullable
    public static Field getField(final Class<?> clazz, final String fieldName){
        try{
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Field getFieldByType(final Class<?> clazz, final Class<?> type){
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getType().equals(type)){
                declaredField.setAccessible(true);
                return declaredField;
            }
        }
        return null;
    }

    @Nullable
    public static Object getFieldVal(final Class<?> clazz, final String fieldName){
        return getFieldVal(clazz, clazz, fieldName);
    }

    @Nullable
    public static Object getFieldVal(final Class<?> clazz, final Object object, final String fieldName){
        try {
            return getField(clazz, fieldName).get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

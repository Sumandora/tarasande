package net.lenni0451.mcstructs.core;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtils {

    private static final Unsafe UNSAFE;

    static {
        try {
            Unsafe unsafe = null;
            for (Field field : Unsafe.class.getDeclaredFields()) {
                if (Unsafe.class.equals(field.getType())) {
                    field.setAccessible(true);
                    unsafe = (Unsafe) field.get(null);
                    break;
                }
            }
            if (unsafe == null) throw new IllegalStateException("Failed to find unsafe field");
            UNSAFE = unsafe;
        } catch (Throwable t) {
            throw new IllegalStateException("Failed to get unsafe instance", t);
        }
    }

    public static <T> T allocateInstance(final Class<?> clazz) throws InstantiationException {
        return (T) UNSAFE.allocateInstance(clazz);
    }

}

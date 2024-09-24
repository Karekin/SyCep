package util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: SyCep
 * @description:
 * @author: lijinzhong
 * @create: 2024-09-24
 */
public class Obj2Map {
    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();

        return Arrays.stream(fields)
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toMap(Field::getName, field -> {
                    try {
                        return field.get(obj);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }
}

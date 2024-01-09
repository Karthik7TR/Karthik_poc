package com.thomsonreuters.uscl.ereader;

import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestUtils {

    @SneakyThrows
    public static void setLogger(final Class aClass, final Logger log) {
        Field field = aClass.getDeclaredField("log");
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, log);
    }
}

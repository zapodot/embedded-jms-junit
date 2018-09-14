package org.zapodot.junit5.jms.internal;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldInjectorTest {

    @Test
    void instantiationNotAllowed() throws NoSuchMethodException {
        final Constructor<FieldInjector> constructor = FieldInjector.class.getDeclaredConstructor();
        assertNotNull(constructor);
        assertFalse(constructor.isAccessible());
        assertThrows(IllegalAccessException.class, constructor::newInstance);
    }

    @Test
    void instantiateAnyway() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<FieldInjector> constructor = FieldInjector.class.getDeclaredConstructor();
        assertNotNull(constructor);
        try {
            constructor.setAccessible(true);
            assertNotNull(constructor.newInstance());
        } finally {
            constructor.setAccessible(false);
        }
    }
}
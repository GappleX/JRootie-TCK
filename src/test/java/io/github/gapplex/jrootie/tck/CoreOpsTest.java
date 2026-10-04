package io.github.gapplex.jrootie.tck;

import io.github.gapplex.jrootie.operators.Rootie;
import io.github.gapplex.jrootie.tck.targets.Target;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CoreOpsTest {
    @Test
    void readFinalInstanceField() {
        try (Rootie r = Rootie.acquire()) {
            Target t = new Target("test");
            String value = r.rtdoField()
                    .getFieldValue(t, "s", String.class);
            assertEquals("test", value);
        }
    }

    @Test
    void writeFinalInstanceField() {
        Target t = new Target("test");

        try (Rootie r = Rootie.acquireTest()) {
            r.rtdoField().setFieldValue(t, "s", "modified");
            assertEquals("modified", r.rtdoField().getFieldValue(t, "s", String.class));
        }
        try (Rootie r = Rootie.acquire()) {
            assertEquals("test", r.rtdoField().getFieldValue(t, "s", String.class));
        }
    }

    @Test
    void staticFinalField_roundtrip() {
        try (Rootie r = Rootie.acquire()) {
            assertEquals(1, r.rtdoField()
                    .getStaticFieldValue(Target.class, "num", int.class));
        }

        try (Rootie r = Rootie.acquireTest()) {
            r.rtdoField().setStaticFieldValue(Target.class, "num", 999);
            assertEquals(999, r.rtdoField()
                    .getStaticFieldValue(Target.class, "num", int.class));
        }

        try (Rootie r = Rootie.acquire()) {
            assertEquals(1, r.rtdoField()
                    .getStaticFieldValue(Target.class, "num", int.class));
        }
    }

    @Test
    void invokePrivateInstanceMethod() {
        try (Rootie r = Rootie.acquire()) {
            Target t = new Target("test");
            Object result = r.rtdoMethod()
                    .invoke(t, "getS", new Class<?>[0]);
            assertEquals("test", result);
        }
    }

    @Test
    void invokePrivateConstructor() {
        try (Rootie r = Rootie.acquire()) {
            Target t = r.rtdoConstructor()
                    .newInstance(Target.class, new Class<?>[]{String.class}, "test");
            assertNotNull(t);
        }
    }

    @Test
    void allocateWithoutConstructor() {
        try (Rootie r = Rootie.acquire()) {
            Target t = r.rtdoClass().allocate(Target.class);
            assertNotNull(t);
            assertNull(r.rtdoField().getFieldValue(t, "s", String.class));
        }
    }

    @Test
    void listDeclaredClasses() {
        try (Rootie r = Rootie.acquire()) {
            Class<?>[] inner = r.rtdoClass().getDeclaredClasses(Target.class);
            assertEquals(0, inner.length);
        }
    }
}
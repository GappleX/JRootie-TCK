package io.github.gapplex.jrootie.tck;

import io.github.gapplex.jrootie.operators.Rootie;
import io.github.gapplex.jrootie.tck.targets.MockString;
import io.github.gapplex.jrootie.tck.targets.Target;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RedefineTest {

    @Test
    void makeReturn_onApplicationClass() {
        try (Rootie r = Rootie.acquireTest()) {
            r.rtdoRedefine().makeReturn(
                    Target.class, "compute", new Class<?>[0], 42);
            assertEquals(42, new Target("test").compute());
        }
        assertEquals(1, new Target("test").compute());
    }

    @Test
    void makeThrow_onApplicationClass() {
        try (Rootie r = Rootie.acquireTest()) {
            r.rtdoRedefine().makeThrow(
                    Target.class, "getS",
                    new Class<?>[0],
                    new IllegalStateException("injected"));
            assertThrows(IllegalStateException.class,
                    () -> new Target("test").getS());
        }
        assertEquals("test", new Target("test").getS());
    }

    @Test
    void replace_onApplicationClass() {
        try (Rootie r = Rootie.acquireTest()) {
            r.rtdoRedefine().replace(
                    Target.class, "getS",
                    new Class<?>[0],
                    ctx -> "hacked");
            assertEquals("hacked", new Target("test").getS());
        }
        assertEquals("test", new Target("test").getS());
    }

    @Test
    void replace_onStaticMethod() {
        try (Rootie r = Rootie.acquireTest()) {
            r.rtdoRedefine().replace(
                    Target.class, "render",
                    new Class<?>[]{Object.class},
                    ctx -> "hacked:" + ctx.arg(0));
            assertEquals("hacked:hi", Target.render("hi"));
        }
        assertEquals("hi", Target.render("hi"));
    }

    @Test
    void replace_voidMethod() {
        Target t = new Target("test");
        AtomicInteger counter = new AtomicInteger(0);

        try (Rootie r = Rootie.acquireTest()) {
            r.rtdoRedefine().replace(
                    Target.class, "voidMethod",
                    new Class<?>[0],
                    ctx -> {
                        counter.incrementAndGet();
                        return null;
                    });
            t.voidMethod();
            assertEquals(1, counter.get(), "lambda 应被调用一次");
            t.voidMethod();
            assertEquals(2, counter.get());
        }

        t.voidMethod();
        assertEquals(2, counter.get(), "回滚后不应再走 lambda");
    }

    @Test
    void replace_onJdkInternalClass() {
        try (Rootie r = Rootie.acquireTest()) {
            r.rtdoRedefine().replace(
                    java.util.Objects.class, "toString",
                    new Class<?>[]{Object.class},
                    ctx -> "hacked:" + ctx.arg(0));
            assertEquals("hacked:hi", java.util.Objects.toString("hi"));
        }
        assertEquals("hi", java.util.Objects.toString("hi"));
    }
}
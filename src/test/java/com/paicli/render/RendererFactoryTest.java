package com.paicli.render;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RendererFactoryTest {

    private String savedProp;

    @BeforeEach
    void saveProp() {
        savedProp = System.getProperty("paicli.renderer");
    }

    @AfterEach
    void restoreProp() {
        if (savedProp == null) {
            System.clearProperty("paicli.renderer");
        } else {
            System.setProperty("paicli.renderer", savedProp);
        }
    }

    @Test
    void fallbackWarningIsReadableInGbkWithoutReplacementQuestionMarks() throws Exception {
        Charset gbk = Charset.forName("GBK");
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        try (PrintStream err = new PrintStream(sink, true, gbk);
             Terminal terminal = new DumbTerminal("display-test", Terminal.TYPE_DUMB,
                     new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream(), gbk)) {
            System.setErr(err);
            Renderer renderer = RendererFactory.create(RendererFactory.Mode.INLINE, terminal);
            assertInstanceOf(PlainRenderer.class, renderer);
        } finally {
            System.setErr(originalErr);
        }
        String message = sink.toString(gbk);
        assertTrue(message.startsWith("[警告] 未检测到可用的 ANSI 终端"), message);
        assertTrue(message.contains("type=dumb, input=GBK"), message);
        assertFalse(message.contains("?"), message);
    }

    @Test
    void defaultsToInlineWhenUnset() {
        System.clearProperty("paicli.renderer");
        // We can't easily clear env vars in tests; only verify property path
        assertEquals(RendererFactory.Mode.INLINE, RendererFactory.resolveMode());
    }

    @Test
    void propertyValueLanternaResolves() {
        System.setProperty("paicli.renderer", "lanterna");
        assertEquals(RendererFactory.Mode.LANTERNA, RendererFactory.resolveMode());
    }

    @Test
    void propertyValuePlainResolves() {
        System.setProperty("paicli.renderer", "plain");
        assertEquals(RendererFactory.Mode.PLAIN, RendererFactory.resolveMode());
    }

    @Test
    void propertyValueIsCaseInsensitive() {
        System.setProperty("paicli.renderer", "LANTERNA");
        assertEquals(RendererFactory.Mode.LANTERNA, RendererFactory.resolveMode());
    }

    @Test
    void unknownValueFallsBackToInline() {
        System.setProperty("paicli.renderer", "weird");
        assertEquals(RendererFactory.Mode.INLINE, RendererFactory.resolveMode());
    }

    @Test
    void tuiAliasResolvesToLanterna() {
        System.setProperty("paicli.renderer", "tui");
        assertEquals(RendererFactory.Mode.LANTERNA, RendererFactory.resolveMode());
    }

    @Test
    void createPlainReturnsPlainRenderer() {
        Renderer renderer = RendererFactory.create(RendererFactory.Mode.PLAIN, null);
        assertInstanceOf(PlainRenderer.class, renderer);
    }

    @Test
    void createInlineReturnsRendererInstance() {
        // Day 1 stub still returns PlainRenderer; Day 2 will swap to InlineRenderer.
        Renderer renderer = RendererFactory.create(RendererFactory.Mode.INLINE, null);
        assertInstanceOf(PlainRenderer.class, renderer);
    }
}

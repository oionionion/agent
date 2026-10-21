package com.paicli.util;

import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class TerminalEncodingTest {
    private static final Charset GBK = Charset.forName("GBK");
    private static final String QUESTION = "联网搜索一下明天是几月几号，天气怎么样，你的数据来源是哪里？";
    private static final Map<String, String> WINDOWS = Map.of(
            "os.name", "Windows 11", "native.encoding", "GBK", "file.encoding", "UTF-8");

    @Test
    void windowsInputUsesNativeEncodingNotFileEncodingWhenConsoleIsUnavailable() {
        assertEquals(GBK, TerminalEncoding.resolve("stdin", WINDOWS::get, null));
    }

    @Test
    void consoleCharsetTakesPrecedenceOverNativeEncoding() {
        assertEquals(StandardCharsets.UTF_8,
                TerminalEncoding.resolve("stdin", WINDOWS::get, StandardCharsets.UTF_8));
    }

    @Test
    void specificJlinePropertyTakesPrecedenceOverJdkAndGlobalProperties() {
        Map<String, String> properties = Map.of(
                "org.jline.terminal.stdin.encoding", "UTF-8",
                "stdin.encoding", "GBK",
                "org.jline.terminal.encoding", "US-ASCII");
        assertEquals(StandardCharsets.UTF_8, TerminalEncoding.resolve("stdin", properties::get, GBK));
    }

    @Test
    void jdkStreamPropertiesTakePrecedenceOverGlobalProperty() {
        Map<String, String> properties = Map.of(
                "stdin.encoding", "UTF-8", "org.jline.terminal.encoding", "GBK");
        assertEquals(StandardCharsets.UTF_8, TerminalEncoding.resolve("stdin", properties::get, GBK));
    }

    @Test
    void legacyJdkStreamPropertyIsRespected() {
        Map<String, String> properties = Map.of("sun.stdin.encoding", "GBK");
        assertEquals(GBK, TerminalEncoding.resolve("stdin", properties::get, StandardCharsets.UTF_8));
    }

    @Test
    void globalJlineOverrideIsRespectedWithoutStreamOverride() {
        Map<String, String> properties = Map.of("org.jline.terminal.encoding", "UTF-8");
        assertEquals(StandardCharsets.UTF_8, TerminalEncoding.resolve("stdin", properties::get, GBK));
    }

    @Test
    void invalidPropertiesFallThroughWithoutCrashing() {
        Map<String, String> properties = Map.of(
                "org.jline.terminal.stdin.encoding", "not a charset!",
                "stdin.encoding", "unknown-charset", "sun.stdin.encoding", "  GBK  ");
        assertEquals(GBK, TerminalEncoding.resolve("stdin", properties::get, null));
    }

    @Test
    void unixKeepsUtf8FallbackEvenIfNativeEncodingIsDifferent() {
        Map<String, String> properties = Map.of("os.name", "Linux", "native.encoding", "US-ASCII");
        assertEquals(StandardCharsets.UTF_8, TerminalEncoding.resolve("stdin", properties::get, null));
    }

    @Test
    void missingOrInvalidNativeEncodingFallsBackToUtf8() {
        assertEquals(StandardCharsets.UTF_8, TerminalEncoding.resolve("stdin", key -> null, null));
        Map<String, String> properties = Map.of("os.name", "Windows 11", "native.encoding", "invalid");
        assertEquals(StandardCharsets.UTF_8, TerminalEncoding.resolve("stdin", properties::get, null));
    }

    @Test
    void configuresInputAndOutputIndependently() {
        Map<String, String> properties = Map.of(
                "os.name", "Windows 11", "native.encoding", "GBK",
                "stdout.encoding", "UTF-8", "stderr.encoding", "US-ASCII");
        TerminalBuilder builder = TerminalEncoding.configure(TerminalBuilder.builder(), properties::get, null);
        assertEquals(GBK, builder.computeEncoding());
        assertEquals(GBK, builder.computeStdinEncoding());
        assertEquals(StandardCharsets.UTF_8, builder.computeStdoutEncoding());
        assertEquals(StandardCharsets.US_ASCII, builder.computeStderrEncoding());
    }

    @Test
    void chineseQuestionSurvivesGbkDumbTerminalAndJlineInput() throws Exception {
        assertJlineInput(QUESTION, GBK, WINDOWS);
    }

    @Test
    void utf8InputKeepsChineseAndEmoji() throws Exception {
        assertJlineInput(QUESTION + " ☀️", StandardCharsets.UTF_8, Map.of(
                "os.name", "Windows 11", "native.encoding", "GBK",
                "org.jline.terminal.stdin.encoding", "UTF-8",
                "org.jline.terminal.stdout.encoding", "UTF-8"));
    }

    @Test
    void approvalReaderUsesTerminalDecoderForChineseReason() throws Exception {
        try (Terminal terminal = terminal("n\n中文拒绝原因\n", GBK, WINDOWS)) {
            var reader = TerminalEncoding.reader(terminal);
            assertEquals("n", reader.readLine());
            assertEquals("中文拒绝原因", reader.readLine());
        }
    }

    private static void assertJlineInput(String text, Charset encoding, Map<String, String> properties)
            throws Exception {
        try (Terminal terminal = terminal(text + "\n", encoding, properties)) {
            assertInstanceOf(org.jline.terminal.impl.DumbTerminal.class, terminal);
            String input = LineReaderBuilder.builder().terminal(terminal).build().readLine("> ");
            assertEquals(text, input);
            assertFalse(input.contains("\uFFFD"));
        }
    }

    private static Terminal terminal(String input, Charset inputEncoding, Map<String, String> properties)
            throws Exception {
        TerminalBuilder builder = TerminalEncoding.configure(TerminalBuilder.builder(), properties::get, null);
        // 与 system terminal 退回 plain 时相同的 DumbTerminal，避免外部终端的后台泵在 EOF 时提前关闭。
        return new org.jline.terminal.impl.DumbTerminal(null, null, "encoding-test", Terminal.TYPE_DUMB,
                new ByteArrayInputStream(input.getBytes(inputEncoding)), new ByteArrayOutputStream(),
                builder.computeEncoding(), builder.computeStdinEncoding(), builder.computeStdoutEncoding(),
                Terminal.SignalHandler.SIG_DFL);
    }
}

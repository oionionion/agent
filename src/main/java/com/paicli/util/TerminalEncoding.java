package com.paicli.util;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Function;

/** 终端字节的编码选择；不要把 Windows 控制台输入固定当成 UTF-8。 */
public final class TerminalEncoding {

    private TerminalEncoding() {
    }

    public static TerminalBuilder configure(TerminalBuilder builder) {
        return configure(builder, System::getProperty, consoleEncoding());
    }

    static TerminalBuilder configure(TerminalBuilder builder, Function<String, String> properties,
                                     Charset consoleEncoding) {
        return builder
                .encoding(baseEncoding(properties, consoleEncoding))
                .stdinEncoding(resolve("stdin", properties, consoleEncoding))
                .stdoutEncoding(resolve("stdout", properties, consoleEncoding))
                .stderrEncoding(resolve("stderr", properties, consoleEncoding));
    }

    /** 审批输入复用 JLine 的读取器，避免绕过编码设置或与它抢读 System.in。 */
    public static BufferedReader reader(Terminal terminal) {
        return new BufferedReader(terminal != null
                ? terminal.reader()
                : new InputStreamReader(System.in, resolve("stdin", System::getProperty, consoleEncoding())));
    }

    static Charset resolve(String stream, Function<String, String> properties, Charset consoleEncoding) {
        for (String key : new String[]{"org.jline.terminal." + stream + ".encoding",
                stream + ".encoding", "sun." + stream + ".encoding"}) {
            Charset charset = charset(properties.apply(key));
            if (charset != null) {
                return charset;
            }
        }
        return baseEncoding(properties, consoleEncoding);
    }

    private static Charset baseEncoding(Function<String, String> properties, Charset consoleEncoding) {
        Charset configured = charset(properties.apply("org.jline.terminal.encoding"));
        if (configured != null) {
            return configured;
        }
        if (consoleEncoding != null) {
            return consoleEncoding;
        }
        String os = properties.apply("os.name");
        if (os != null && os.toLowerCase(Locale.ROOT).startsWith("windows")) {
            Charset nativeEncoding = charset(properties.apply("native.encoding"));
            if (nativeEncoding != null) {
                return nativeEncoding;
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static Charset consoleEncoding() {
        Console console = System.console();
        return console == null ? null : console.charset();
    }

    private static Charset charset(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return Charset.forName(name.trim());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}

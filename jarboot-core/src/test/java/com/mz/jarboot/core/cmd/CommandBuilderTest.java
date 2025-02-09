package com.mz.jarboot.core.cmd;

import com.mz.jarboot.common.CommandRequest;
import com.mz.jarboot.core.cmd.impl.SearchClassCommand;
import com.mz.jarboot.core.cmd.impl.ThreadCommand;
import com.mz.jarboot.core.cmd.impl.TraceCommand;
import com.mz.jarboot.core.cmd.internal.CancelCommand;
import com.mz.jarboot.core.cmd.internal.ExitCommand;
import com.mz.jarboot.core.server.LogTest;
import com.mz.jarboot.core.session.CommandSession;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("all")
public class CommandBuilderTest {
    @BeforeClass
    public static void init() {
        LogTest.initTest();
    }
    @Test
    public void testBuild() {
        //测试trace命令构建
        String line = "u123 trace demo.Test run 'params.length>=0' -n 5";
        CommandRequest request = new CommandRequest();
        CommandSession session = Mockito.mock(CommandSession.class);
        request.fromRaw(line);
        Command cmd = CommandBuilder.build(request, session);
        assertThat(cmd instanceof TraceCommand).isTrue();
        TraceCommand trace = (TraceCommand)cmd;
        assertEquals("trace", trace.getName());
        assertEquals("demo.Test", trace.getClassPattern());
        assertEquals("run", trace.getMethodPattern());
        assertEquals(5, trace.getNumberOfLimit());
        assertEquals("'params.length>=0'", trace.getConditionExpress());

        line = "u123 trace demo.Test run 'params.length>=0' -n 3 -p path1 path2 path3";
        request = new CommandRequest();
        session = Mockito.mock(CommandSession.class);
        request.fromRaw(line);
        cmd = CommandBuilder.build(request, session);
        assertThat(cmd instanceof TraceCommand).isTrue();
        trace = (TraceCommand)cmd;
        assertEquals("trace", trace.getName());
        assertEquals("demo.Test", trace.getClassPattern());
        assertEquals("run", trace.getMethodPattern());
        assertEquals(3, trace.getNumberOfLimit());
        assertEquals("'params.length>=0'", trace.getConditionExpress());
        List<String> patterns = trace.getPathPatterns();
        assertEquals(3, patterns.size());

        //测试thread命令构建
        line = "u123 thread 1";
        request = new CommandRequest();
        request.fromRaw(line);
        cmd = CommandBuilder.build(request, session);
        assertThat(cmd instanceof ThreadCommand).isTrue();
        ThreadCommand thread = (ThreadCommand)cmd;
        assertEquals("thread", thread.getName());
        try {
            Field field = ThreadCommand.class.getDeclaredField("id");
            field.setAccessible(true);
            assertEquals(1L, field.get(thread));
        } catch (Exception e) {
            org.junit.Assert.fail(e.getMessage());
        }

        //测试thread命令构建
        line = "u123 sc -d -f com.mz.jarboot.core.ws.WebSocketClient";
        request = new CommandRequest();
        request.fromRaw(line);
        cmd = CommandBuilder.build(request, session);
        assertThat(cmd instanceof SearchClassCommand).isTrue();
        SearchClassCommand sc = (SearchClassCommand)cmd;
        assertEquals("sc", sc.getName());
        try {
            Field field = SearchClassCommand.class.getDeclaredField("classPattern");
            field.setAccessible(true);
            assertEquals("com.mz.jarboot.core.ws.WebSocketClient", field.get(sc));

            field = SearchClassCommand.class.getDeclaredField("isDetail");
            field.setAccessible(true);
            assertEquals(true, field.get(sc));

            field = SearchClassCommand.class.getDeclaredField("isField");
            field.setAccessible(true);
            assertEquals(true, field.get(sc));
        } catch (Exception e) {
            org.junit.Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInternalBuild() {
        //测试trace命令构建
        String line = "i123 exit";
        CommandRequest request = new CommandRequest();
        CommandSession session = Mockito.mock(CommandSession.class);
        request.fromRaw(line);
        Command cmd = CommandBuilder.build(request, session);
        assertThat(cmd instanceof ExitCommand).isTrue();
        assertEquals("exit", cmd.getName());

        line = "i123 cancel ";
        request = new CommandRequest();
        request.fromRaw(line);
        cmd = CommandBuilder.build(request, session);
        assertThat(cmd instanceof CancelCommand).isTrue();
        assertEquals("cancel", cmd.getName());
    }
}

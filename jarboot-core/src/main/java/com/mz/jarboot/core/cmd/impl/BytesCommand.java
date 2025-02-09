package com.mz.jarboot.core.cmd.impl;

import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.tree.AbstractInsnNode;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.InsnList;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.alibaba.deps.org.objectweb.asm.util.Printer;
import com.alibaba.deps.org.objectweb.asm.util.Textifier;
import com.alibaba.deps.org.objectweb.asm.util.TraceMethodVisitor;
import com.mz.jarboot.core.basic.EnvironmentContext;
import com.mz.jarboot.core.cmd.Command;
import com.mz.jarboot.core.cmd.annotation.Argument;
import com.mz.jarboot.core.cmd.annotation.Description;
import com.mz.jarboot.core.constant.CoreConstant;
import com.mz.jarboot.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * show the jvm detail
 * @author jianzhengma
 */
public class BytesCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(CoreConstant.LOG_NAME);
    private static Printer printer = new Textifier();
    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);

    private String classPattern;

    @Argument(argName = "class-pattern", index = 0)
    @Description("Class name pattern, use either '.' or '/' as separator")
    public void setClassPattern(String classPattern) {
        this.classPattern = classPattern;
    }

    @Override
    public boolean isRunning() {
        return null != session && session.isRunning();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void cancel() {
        //do nothing
    }

    @Override
    public void run() {
        logger.info("bytes 开始执行>>{}", name);
        if (StringUtils.isEmpty(this.classPattern)) {
            //未指定要打印的类
            session.end(true, "用法: bytes className");
            return;
        }
        Class<?> cls = null;
        Class[] classes = EnvironmentContext.getInstrumentation().getAllLoadedClasses();
        for (Class<?> c : classes) {
            if (c.getName().equals(this.classPattern)) {
                cls = c;
                break;
            }
        }
        if (null == cls) {
            session.end(true, "没有找到类," + this.classPattern);
            return;
        }
        //打印classloader
        session.console("ClassLoader: " + cls.getClassLoader().toString());
        session.console("------");
        EnvironmentContext.getTransformerManager()
                .addOnceTransformer(cls, (loader, className, classBeingRedefined,
                                          protectionDomain, classfileBuffer) -> {
                    try {
                        ClassReader reader = new ClassReader(classfileBuffer);
                        ClassNode classNode = new ClassNode();
                        reader.accept(classNode, 0);
                        final List<MethodNode> methods = classNode.methods;
                        for (MethodNode m : methods) {
                            InsnList inList = m.instructions;
                            session.console(m.name);
                            for (int i = 0; i < inList.size(); i++) {
                                session.console(nodeToString(inList.get(i)));
                            }
                        }
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                        session.console("解析类失败，" + e.getMessage());
                    }
                    session.end();
                    return null;
                });

        EnvironmentContext.getTransformerManager().retransformClasses(cls);
    }

    public static String nodeToString(AbstractInsnNode node){
        node.accept(mp);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }
}

package com.mz.jarboot.core.advisor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.*;
import java.jarboot.SpyAPI;

/**
 * byte kit 织入切点
 * @author jianzhengma
 * 以下代码基于开源项目Arthas适配修改
 */
@SuppressWarnings("all")
public class SpyInterceptors {

    public static class SpyInterceptor1 {

        @AtEnter(inline = true)
        public static void atEnter(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.MethodInfo String methodInfo, @Binding.Args Object[] args) {
            SpyAPI.atEnter(clazz, methodInfo, target, args);
        }
    }
    
    public static class SpyInterceptor2 {
        @AtExit(inline = true)
        public static void atExit(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.MethodInfo String methodInfo, @Binding.Args Object[] args, @Binding.Return Object returnObj) {
            SpyAPI.atExit(clazz, methodInfo, target, args, returnObj);
        }
    }
    
    public static class SpyInterceptor3 {
        @AtExceptionExit(inline = true)
        public static void atExceptionExit(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.MethodInfo String methodInfo, @Binding.Args Object[] args,
                @Binding.Throwable Throwable throwable) {
            SpyAPI.atExceptionExit(clazz, methodInfo, target, args, throwable);
        }
    }

    public static class SpyTraceInterceptor1 {
        @AtInvoke(name = "", inline = true, whenComplete = false, excludes = {"java.jarboot.SpyAPI", "java.lang.Byte"
                , "java.lang.Boolean"
                , "java.lang.Short"
                , "java.lang.Character"
                , "java.lang.Integer"
                , "java.lang.Float"
                , "java.lang.Long"
                , "java.lang.Double"})
        public static void onInvoke(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.InvokeInfo String invokeInfo) {
            SpyAPI.atBeforeInvoke(clazz, invokeInfo, target);
        }
    }
    
    public static class SpyTraceInterceptor2 {
        @AtInvoke(name = "", inline = true, whenComplete = true, excludes = {"java.jarboot.SpyAPI", "java.lang.Byte"
                , "java.lang.Boolean"
                , "java.lang.Short"
                , "java.lang.Character"
                , "java.lang.Integer"
                , "java.lang.Float"
                , "java.lang.Long"
                , "java.lang.Double"})
        public static void onInvokeAfter(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.InvokeInfo String invokeInfo) {
            SpyAPI.atAfterInvoke(clazz, invokeInfo, target);
        }
    }
    
    public static class SpyTraceInterceptor3 {
        @AtInvokeException(name = "", inline = true, excludes = {"java.jarboot.SpyAPI", "java.lang.Byte"
                , "java.lang.Boolean"
                , "java.lang.Short"
                , "java.lang.Character"
                , "java.lang.Integer"
                , "java.lang.Float"
                , "java.lang.Long"
                , "java.lang.Double"})
        public static void onInvokeException(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.InvokeInfo String invokeInfo, @Binding.Throwable Throwable throwable) {
            SpyAPI.atInvokeException(clazz, invokeInfo, target, throwable);
        }
    }

    public static class SpyTraceExcludeJDKInterceptor1 {
        @AtInvoke(name = "", inline = true, whenComplete = false, excludes = "java.**")
        public static void onInvoke(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.InvokeInfo String invokeInfo) {
            SpyAPI.atBeforeInvoke(clazz, invokeInfo, target);
        }
    }

    public static class SpyTraceExcludeJDKInterceptor2 {
        @AtInvoke(name = "", inline = true, whenComplete = true, excludes = "java.**")
        public static void onInvokeAfter(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.InvokeInfo String invokeInfo) {
            SpyAPI.atAfterInvoke(clazz, invokeInfo, target);
        }
    }

    public static class SpyTraceExcludeJDKInterceptor3 {
        @AtInvokeException(name = "", inline = true, excludes = "java.**")
        public static void onInvokeException(@Binding.This Object target, @Binding.Class Class<?> clazz,
                @Binding.InvokeInfo String invokeInfo, @Binding.Throwable Throwable throwable) {
            SpyAPI.atInvokeException(clazz, invokeInfo, target, throwable);
        }
    }

}

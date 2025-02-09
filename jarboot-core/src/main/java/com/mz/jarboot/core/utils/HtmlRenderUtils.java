package com.mz.jarboot.core.utils;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.mz.jarboot.core.constant.CoreConstant;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class HtmlRenderUtils {
    private TemplateEngine engine;
    private HtmlCompressor compressor;
    private HtmlRenderUtils() {
        // 初始化模版引擎
        engine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setCharacterEncoding("UTF-8");
        engine.setTemplateResolver(resolver);

        // 初始化压缩模式
        compressor = new HtmlCompressor();
        compressor.setEnabled(true);                   // 如果false，则关闭所有压缩（默认值为true）
        compressor.setRemoveComments(true);            // 如果false保留HTML注释（默认值为true）
        compressor.setRemoveMultiSpaces(true);         // 如果false保留多个空格字符（默认值为true）
        compressor.setRemoveIntertagSpaces(true);      // 删除iter标记空白字符
        compressor.setRemoveQuotes(true);              // 删除不必要的标记属性引号
        compressor.setSimpleDoctype(true);             // 简化现有doctype
        compressor.setRemoveScriptAttributes(true);    // 从script标签中移除可选属性
        compressor.setRemoveStyleAttributes(true);     // 从style标签中移除可选属性
        compressor.setRemoveLinkAttributes(true);      // 从link标签中移除可选属性
        compressor.setRemoveFormAttributes(true);      // 从form标签中移除可选属性
        compressor.setRemoveInputAttributes(true);     // 从input标签中移除可选属性
        compressor.setSimpleBooleanAttributes(true);   // 从布尔标签属性中移除值
        compressor.setRemoveJavaScriptProtocol(true);  // 从内联事件处理程序中删除“javascript:”
        compressor.setRemoveHttpProtocol(true);        // 将“http://”替换为“//”内部标记属性
        compressor.setRemoveHttpsProtocol(true);       // 将“https://”替换为“//”内部标记属性
        compressor.setPreserveLineBreaks(false);       // 去原始换行符
        compressor.setRemoveSurroundingSpaces("br,p"); // 删除提供的标记周围的空格
        compressor.setCompressCss(true);               // 压缩内联css
        compressor.setCompressJavaScript(true);        // 压缩内联js
        compressor.setYuiCssLineBreak(80);             // Yahoo YUI压缩机的换行参数
        compressor.setYuiJsDisableOptimizations(false);// 启动Yahoo YUI压缩器的优化参数
        compressor.setYuiJsLineBreak(-1);              // Yahoo YUI压缩机的换行参数
        compressor.setYuiJsNoMunge(true);              //--nomunge param for Yahoo YUI Compressor
        compressor.setYuiJsPreserveAllSemiColons(true);// 为Yahoo YUI Compressor保留半参数
    }
    private static class HtmlRenderUtilsHolder {
        static HtmlRenderUtils inst = new HtmlRenderUtils();
    }

    public static HtmlRenderUtils getInstance() {
        return HtmlRenderUtilsHolder.inst;
    }

    public String processHtml(String template, Context context) {
        if (StringUtils.isEmpty(template)) {
            return CoreConstant.EMPTY_STRING;
        }
        String html = engine.process(template, context);
        return compressor.compress(html);
    }
}

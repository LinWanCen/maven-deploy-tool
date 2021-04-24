package io.github.linwancen.maven.tool.scan;

import io.github.linwancen.maven.tool.Conf;
import io.github.linwancen.maven.tool.MvnKey;
import io.github.linwancen.maven.tool.Suffix;
import io.github.linwancen.util.EnvUtils;
import io.github.linwancen.util.JsUtils;
import io.github.linwancen.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class Scan {

    private static final Logger LOG = LoggerFactory.getLogger(Scan.class);

    private Scan() {}

    public static void run(Map<String, StringBuilder> paramMap, File... files) {
        // 在方法中获取而不是成员变量中获取以便支持重设
        Double calculate = JsUtils.calculate(EnvUtils.get("skipSize", Conf.prop));
        int skipSize;
        if (calculate == null) {
            skipSize = 1024 * 1024 * 50;
            LOG.warn("set default skipSize\t1024 * 1024 * 50 = {}", skipSize);
        } else {
            skipSize = calculate.intValue();
        }
        recursion(skipSize, paramMap, files);
    }

    public static void recursion(int skipSize, Map<String, StringBuilder> paramMap, File... files) {
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // java 没有尾递归优化，所以卫语句让代码清晰
                recursion(skipSize, paramMap, file.listFiles());
                continue;
            }
            String canonicalPath = PathUtils.canonicalPath(file);
            long length = file.length();
            // 一般不会有其他类型或上传需求，所以这里写死
            if (canonicalPath.endsWith(".zip") || canonicalPath.endsWith(".tar")) {
                LOG.info("skip zip/tar size:\t{}\tfile:///{}", length, canonicalPath);
                continue;
            }
            if (length > skipSize) {
                LOG.error("skip big size:\t{}\tfile:///{}", length, canonicalPath);
                continue;
            }
            LOG.debug("load size:\t{}\tfile:///{}", length, canonicalPath);
            // 因为 src 和 javadoc 也是 jar 结尾，所以要放在前面
            if (canonicalPath.endsWith(Suffix.SRC)) {
                ParamMapUtils.append(paramMap, canonicalPath, Suffix.SRC, MvnKey.SRC);
            } else if (canonicalPath.endsWith(Suffix.DOC)) {
                ParamMapUtils.append(paramMap, canonicalPath, Suffix.DOC, MvnKey.DOC);
            } else if (canonicalPath.endsWith(Suffix.JAR)) {
                ParamMapUtils.append(paramMap, canonicalPath, Suffix.JAR, MvnKey.JAR_PACKAGING_FILE);
            } else if (canonicalPath.endsWith(Suffix.POM)) {
                ParamMapUtils.append(paramMap, canonicalPath, Suffix.POM, MvnKey.POM_FILE);
            }
        }
    }
}

package io.github.linwancen.maven.tool;

import io.github.linwancen.maven.tool.deploy.Deploy;
import io.github.linwancen.maven.tool.scan.Scan;

import java.io.File;
import java.util.LinkedHashMap;

public class Service {

    private Service() {}

    public static void run(File file) {
        // <路径去掉扩展名，命令行参数>，加上自带解压功能，放到同一个目录中也能上传
        LinkedHashMap<String, StringBuilder> paramMap = new LinkedHashMap<>();
        Scan.run(paramMap, file.listFiles());
        Deploy.run(paramMap, file);
    }
}

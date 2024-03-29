package io.github.linwancen.maven.tool.scan;

import java.util.Map;

class ParamMapUtils {

    private ParamMapUtils() {}

    static void append(
            Map<String, StringBuilder> paramMap,
            String canonicalPath, String suffix, String paramKey) {
        String k = canonicalPath.substring(0, canonicalPath.length() - suffix.length());
        StringBuilder v = paramMap.get(k);
        if (v == null) {
            v = new StringBuilder();
            paramMap.put(k, v);
        }
        v.append(paramKey).append(canonicalPath);
    }
}

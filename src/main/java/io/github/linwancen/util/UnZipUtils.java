package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZipUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UnZipUtils.class);

    private UnZipUtils() {}

    public static List<File> unZip(File inFile, File outDir, String name) {
        return unZip(inFile, outDir, name, null, null);
    }

    /**
     * 排除优先于包含
     * <br/>
     */
    public static List<File> unZip(File inFile, File outDir, Pattern inclusion, Pattern exclusion) {
        return unZip(inFile, outDir, null, inclusion, exclusion);
    }

    private static List<File> unZip(File inFile, File outDir, String name,
                                    Pattern inclusion, Pattern exclusion) {
        ArrayList<File> list = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(inFile)) {
            // 在 unZipEntry(...) 方法会判断文件夹是否存在并创建所以这里就不创建了
            String outPath = outDir.getCanonicalPath();

            if (name != null) {
                name = name.replace('\\', '/');
                while (name.startsWith("/")) {
                    name = name.substring(1);
                }
                ZipEntry entry = zipFile.getEntry(name);
                if (entry != null) {
                    unZipEntry(list, zipFile, entry, outPath);
                    return list;
                }
            }

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                name = entry.getName();
                // 有正则就筛选，没有就全部
                boolean isNotExclusion = exclusion == null || !exclusion.matcher(name).find();
                boolean isInclusion = inclusion == null || inclusion.matcher(name).find();
                if (isNotExclusion && isInclusion) {
                    unZipEntry(list, zipFile, entry, outPath);
                }
            }
        } catch (IOException e) {
            String path = PathUtils.canonicalPath(inFile);
            LOG.error("ZipFile IOException, file:///{}", path, e);
            // 这里就不返回 null 了，避免使用的人没判空导致空指针
            return list;
        }
        return list;
    }

    private static void unZipEntry(List<File> list, ZipFile zipFile, ZipEntry entry, String outPath) {
        File outFile = new File(outPath, entry.getName());
        try {
            String unZipPath = outFile.getCanonicalPath();
            if (!unZipPath.startsWith(outPath)) {
                // 解决：路径注入漏洞(path injection vulnerabilities)-压缩滑动漏洞(zip slip vulnerabilities)
                // 防止任意文件访问 ../../../../../etc/password
                LOG.error("Entry is outside of the target directory\n  unZipPath: {}\n  outPath: file:///{}",
                        unZipPath, outPath);
                return;
            }
        } catch (IOException e) {
            LOG.error("outFile.getCanonicalPath IOException\n  outFile.getPath(): {}\n  outPath: file:///{}",
                    outFile.getPath(), outPath);
            return;
        }
        if (entry.isDirectory()) {
            if (!outFile.exists() && outFile.mkdirs()) {
                String path = PathUtils.canonicalPath(outFile);
                LOG.warn("unZipEntry mkdir fail, file:///{}", path);
            }
            list.add(outFile);
            return;
        }
        PathUtils.mkdir(outFile.getParentFile());
        try (InputStream inputStream = zipFile.getInputStream(entry);
             OutputStream outputStream = new FileOutputStream(outFile)
        ) {
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = inputStream.read(buf1)) > 0) {
                outputStream.write(buf1, 0, len);
            }
            list.add(outFile);
        } catch (IOException e) {
            String path = zipFile.getName().replace('\\', '/');
            LOG.error("unZipEntry IOException file:///{}\n  {}", path, entry, e);
        }
    }
}

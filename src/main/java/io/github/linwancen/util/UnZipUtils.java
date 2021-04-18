package io.github.linwancen.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZipUtils {

    private static final Logger LOG = LoggerFactory.getLogger(UnZipUtils.class);

    private UnZipUtils() {}

    public static ArrayList<File> unZip(File inFile, File outDir, String name) {
        return unZip(inFile, outDir, name, null, null);
    }

    /**
     * 排除优先于包含
     * <br/>
     */
    public static ArrayList<File> unZip(File inFile, File outDir, Pattern inclusion, Pattern exclusion) {
        return unZip(inFile, outDir, null, inclusion, exclusion);
    }

    private static ArrayList<File> unZip(File inFile, File outDir, String name,
                                         Pattern inclusion, Pattern exclusion) {
        ArrayList<File> list = new ArrayList<>();

        ZipFile zipFile;
        try {
            zipFile = new ZipFile(inFile);
        } catch (IOException e) {
            LOG.error("new ZipFile IOException, file:///{}", PathUtils.canonicalPath(inFile), e);
            // 这里就不返回 null 了，避免使用的人没判空导致空指针
            return list;
        }

        if (name != null) {
            name = name.replace('\\', '/');
            while (name.startsWith("/")) {
                name = name.substring(1);
            }
            ZipEntry entry = zipFile.getEntry(name);
            if (entry != null) {
                unZipEntry(list, zipFile, entry, new File(outDir, name));
                return list;
            }
        }

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            name = entry.getName();
            // 有正则就筛选，没有就全部
            if (exclusion != null && exclusion.matcher(name).find()) {
                continue;
            }
            if (inclusion != null && !inclusion.matcher(name).find()) {
                continue;
            }
            unZipEntry(list, zipFile, entry, new File(outDir, name));
        }
        return list;
    }

    private static void unZipEntry(ArrayList<File> list, ZipFile zipFile, ZipEntry entry, File outFile) {
        if (entry.isDirectory()) {
            if (!outFile.exists() && outFile.mkdirs()) {
                LOG.warn("unZipEntry mkdirs fail, file:///{}", PathUtils.canonicalPath(outFile));
            }
            list.add(outFile);
            return;
        }
        PathUtils.mkdirParent(outFile);
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
            LOG.error("unZipEntry IOException file:///{}\n  {}",
                    zipFile.getName().replace('\\', '/'), entry, e);
        }
    }
}

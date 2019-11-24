package com.dream.spring.excel;

import java.io.File;
import java.util.Arrays;

/**
 * @author DreamJM
 */
public class FileUtils {

    private static String ROOT = Thread.currentThread().getContextClassLoader().getResource("").toString().replace("file:", "");

    public static File file(String cacheDirPath, long timestamp) {
        clearCache(cacheDirPath);
        return new File(cacheDir(cacheDirPath), String.valueOf(timestamp));
    }

    private static File cacheDir(String cacheDirPath) {
        File cacheDir = new File(ROOT, cacheDirPath);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    public static void clearCache(String cacheDirPath) {
        File[] files = cacheDir(cacheDirPath).listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public static File newestFile(String cacheDirPath) {
        File[] files = cacheDir(cacheDirPath).listFiles(File::isFile);
        if (files != null && files.length > 0) {
            return Arrays.stream(files).filter(file -> {
                try {
                    Long.parseLong(file.getName());
                    return true;
                } catch (NumberFormatException ex) {
                    return false;
                }
            }).max((f1, f2) -> (int) (Long.parseLong(f2.getName()) - Long.parseLong(f1.getName()))).orElse(null);
        }
        return null;
    }

}

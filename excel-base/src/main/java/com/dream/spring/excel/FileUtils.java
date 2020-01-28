/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dream.spring.excel;

import java.io.File;
import java.util.Arrays;

/**
 * Common File Utils
 *
 * @author DreamJM
 */
public class FileUtils {

    private static String ROOT = ".";

    public static void setCacheRoot(String cacheRoot) {
        FileUtils.ROOT = cacheRoot;
    }

    /**
     * Get new export cache file
     *
     * @param cacheDirPath cache dir
     * @param timestamp    data timestamp
     * @return export cache file
     */
    public static File file(String cacheDirPath, long timestamp) {
        clearCache(cacheDirPath);
        return new File(cacheDir(cacheDirPath), String.valueOf(timestamp));
    }

    /**
     * Get cache dir relative to root class path
     *
     * @param cacheDirPath relative cache dir
     * @return cache directory
     */
    public static File cacheDir(String cacheDirPath) {
        File cacheDir = new File(ROOT, cacheDirPath);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir.getAbsoluteFile();
    }

    /**
     * Remove all files under cache dir
     *
     * @param cacheDirPath cache dir
     */
    public static void clearCache(String cacheDirPath) {
        File[] files = cacheDir(cacheDirPath).listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    /**
     * Get the newest file in cache dir
     *
     * @param cacheDirPath cache dir
     * @return the newest cache file
     */
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

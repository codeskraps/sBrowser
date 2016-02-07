package com.codeskraps.sbrowser.misc;

import android.content.Context;

import java.io.File;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    private static int clearCacheFolder(final File dir) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {

                    //first delete subdirectories recursively
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child);
                    }

                    if (child.delete())
                        deletedFiles++;
                }
            } catch (Exception e) {
                L.e(TAG, String.format("Failed to clean the cache, error %s", e.getMessage()));
            }
        }

        L.i(TAG, "ClearCachFolder deleted files:" + deletedFiles);
        return deletedFiles;
    }

    /*
     * Delete the files older than numDays days from the application cache
     * 0 means all files.
     */
    public static void clearCache(final Context context) {
        L.i(TAG, "Starting cache prune");
        int numDeletedFiles = clearCacheFolder(context.getCacheDir());
        L.i(TAG, String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
    }
}

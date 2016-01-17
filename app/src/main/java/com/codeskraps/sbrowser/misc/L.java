package com.codeskraps.sbrowser.misc;

import android.util.Log;

import com.codeskraps.sbrowser.BuildConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class L {
    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
    private static final int MAX_LOG_SIZE = 4000;
    private static final boolean PRINT_TO_LOGCAT = BuildConfig.DEBUG;

    private L() {
    }

    public static void v(String tag, String message) {
        v(tag, message, null);
    }

    public static void v(String tag, String message, Throwable throwable) {
        if (PRINT_TO_LOGCAT) {
            if (message == null || message.length() < MAX_LOG_SIZE) Log.v(tag, message, throwable);
            else THREAD_POOL.execute(new LogLongMessages(Log.VERBOSE, tag, message, throwable));
        }
    }

    public static void d(String tag, String message) {
        d(tag, message, null);
    }

    public static void d(String tag, String message, Throwable throwable) {
        if (PRINT_TO_LOGCAT) {
            if (message == null || message.length() < MAX_LOG_SIZE) Log.d(tag, message, throwable);
            else THREAD_POOL.execute(new LogLongMessages(Log.DEBUG, tag, message, throwable));
        }
    }

    public static void i(String tag, String message) {
        i(tag, message, null);
    }

    public static void i(String tag, String message, Throwable throwable) {
        if (PRINT_TO_LOGCAT) {
            if (message == null || message.length() < MAX_LOG_SIZE) Log.i(tag, message, throwable);
            else THREAD_POOL.execute(new LogLongMessages(Log.INFO, tag, message, throwable));
        }
    }

    public static void w(String tag, String message) {
        w(tag, message, null);
    }

    public static void w(String tag, String message, Throwable throwable) {
        if (PRINT_TO_LOGCAT) {
            if (message == null || message.length() < MAX_LOG_SIZE) Log.w(tag, message, throwable);
            else THREAD_POOL.execute(new LogLongMessages(Log.WARN, tag, message, throwable));
        }
    }

    public static void e(String tag, String message) {
        e(tag, message, null);
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (PRINT_TO_LOGCAT) {
            if (message == null || message.length() < MAX_LOG_SIZE) Log.e(tag, message, throwable);
            else THREAD_POOL.execute(new LogLongMessages(Log.ERROR, tag, message, throwable));
        }
    }

    private static class LogLongMessages implements Runnable {
        private final int priority;
        private final String tag;
        private final String message;
        private final Throwable throwable;

        public LogLongMessages(int priority, String tag, String message, Throwable throwable) {
            this.priority = priority;
            this.tag = tag;
            this.message = message;
            this.throwable = throwable;
        }

        @Override
        public void run() {
            for (int i = 0; i <= message.length() / MAX_LOG_SIZE; i++) {
                int start = i * MAX_LOG_SIZE;
                int end = (i + 1) * MAX_LOG_SIZE;
                end = end > message.length() ? message.length() : end;
                // @formatter:off
				switch(priority) {
				case Log.VERBOSE: 	Log.v(tag, message.substring(start, end), throwable); break;
				case Log.DEBUG: 	Log.d(tag, message.substring(start, end), throwable); break;
				case Log.INFO: 		Log.i(tag, message.substring(start, end), throwable); break;
				case Log.WARN: 		Log.w(tag, message.substring(start, end), throwable); break;
				case Log.ERROR:		Log.e(tag, message.substring(start, end), throwable); break;
				case Log.ASSERT: 	Log.wtf(tag, message.substring(start, end), throwable); break;
				} // @formatter:on
            }
        }
    }
}


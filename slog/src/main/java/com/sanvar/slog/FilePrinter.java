package com.sanvar.slog;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/***
 * 写文件的类
 */
public class FilePrinter implements Printer {

    private Handler handler;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.CHINA);
    private SimpleDateFormat FileFormat = new SimpleDateFormat("yyyyMMdd",Locale.CHINA);
    private String logDir;
    private long clock;
    private File dir;
    private String fileDate;
    private File file;
    private BufferedWriter bufWriter;
    private int saveDay;
    private int priority;
    private static long Hours = 60 * 60 * 1000;
    private boolean encrypt;
    private static String keys = "sanvar_log";

    /**
     * 写入文件的类
     * @param priority  日志级别，高于该日志就打印
     * @param logDir    文件目录
     * @param saveDay   保存天数
     * @param encrypt   是否需要加密
     */
    public FilePrinter(int priority,String logDir,int saveDay,boolean encrypt) {
        this.logDir= logDir;
        this.priority = priority;
        this.saveDay = saveDay;
        this.encrypt = encrypt;
        HandlerThread thread = new HandlerThread("LogToFile");
        thread.start();
        init();
        this.clock = System.currentTimeMillis();
        handler = new Handler(thread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                return true;
            }
        });
    }




    private void init() {
        if (TextUtils.isEmpty(logDir)) return;
        dir = new File(logDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            fileDate = FileFormat.format(System.currentTimeMillis());
            file = new File(dir, fileDate+"SLog.txt");
            bufWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private byte[] xor(byte[] text) {
        byte[] result = new byte[text.length];
        byte[] keyArray = new byte[0];
        try {
            keyArray = keys.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte key;
        int size = text.length;
        for (int i = 0; i < size; i++) {
            key = keyArray[i % keyArray.length];
            result[i] = (byte) (text[i] ^ key);
        }
        return result;
    }


    private void delFileBefore() {
        if (dir.exists()) {
            String needDelFiel = this.FileFormat.format(getDateBefore());
            File[] files = dir.listFiles();
            if (files != null && files.length != 0) {
                int length = files.length;
                for (int i = 0; i < length; ++i) {
                    File f = files[i];
                    if (f.getName().compareTo(needDelFiel + "SLog.txt") <= 0 && f.getName().endsWith(".txt")) {
                        f.delete();
                    }
                }

            }
        }
    }

    private Date getDateBefore() {
        Calendar cld = Calendar.getInstance();
        cld.setTime(new Date());
        cld.set(Calendar.DATE, cld.get(Calendar.DATE) - saveDay);
        return cld.getTime();
    }



    @Override
    public boolean isLoggable(int priority) {
        return priority>=this.priority;
    }

    @Override
    public void log(int priority, @Nullable final String tag, @NonNull final String message) {
        Log.println(priority,tag,message);
        if (priority>Log.DEBUG){
            //
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long nowtime = System.currentTimeMillis();
                    if (!TextUtils.equals(FileFormat.format(nowtime), fileDate)) init();
                    String msg = timeFormat.format(nowtime) + " " + tag + ":" + message;
                    try {
                        if (encrypt){
                          msg =  new String(xor(msg.getBytes("UTF-8")));
                        }
                        bufWriter.write(msg);
                        bufWriter.newLine();
                        bufWriter.flush();
                    } catch (Exception e) {
                        try {
                            if (bufWriter != null) bufWriter.close();
                            init();
                        } catch (IOException ioe) {
                            e.printStackTrace();
                        }
                    }

                    if (nowtime - clock > Hours) {
                        clock = nowtime;
                        delFileBefore();
                    }

                }
            });
        }
    }
}

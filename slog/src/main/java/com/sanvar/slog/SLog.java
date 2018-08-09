package com.sanvar.slog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class SLog {
    private static final int JSON_INDENT = 2;
    private static final int CHUNK_SIZE = 4000;


   private static Builder builder;

    public static void init(Builder b){
        builder = b;
    }

    private static boolean checkNotNull(Builder builder){
        if (builder==null || builder.printer ==null) return false;
        return true;
    }



    public static void v(@NonNull Object o) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (builder.printer.isLoggable(Log.VERBOSE)) {
            log(Log.VERBOSE,Utils.toString(o));
        }
    }

    public static void d(@NonNull Object o) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (builder.printer.isLoggable(Log.DEBUG)) {
            log(Log.DEBUG,Utils.toString(o));
        }
    }

    public static void i(@NonNull Object o) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (builder.printer.isLoggable(Log.INFO)) {
            log(Log.INFO,Utils.toString(o));
        }
    }

    public static void w(@NonNull Object o) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (builder.printer.isLoggable(Log.WARN)) {
            log(Log.WARN,Utils.toString(o));
        }
    }

    public static void w(@NonNull Object o, Throwable throwable) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (builder.printer.isLoggable(Log.WARN)) {
            log(Log.WARN,Utils.toString(o)+"\n"+Utils.getStackTraceString(throwable));
        }
    }

    public static void e(@NonNull Object o) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (builder.printer.isLoggable(Log.ERROR)) {
            log(Log.ERROR,Utils.toString(o));
        }
    }

    public static void e(@NonNull Object o, Throwable throwable) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (builder.printer.isLoggable(Log.ERROR)) {
            log(Log.ERROR,Utils.toString(o)+"\n"+Utils.getStackTraceString(throwable));
        }
    }


    public static void json(@NonNull String json) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (!builder.printer.isLoggable(Log.DEBUG)) return;

        if (Utils.isEmpty(json)) {
            w("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
                return;
            }
            e("Invalid Json");
        } catch (JSONException e) {
            e("Invalid Json");
        }
    }

    public static void xml(@NonNull String xml) {
        if (!checkNotNull(builder)) {
            return;
        }
        if (!builder.printer.isLoggable(Log.DEBUG)) {
            return;
        }

        if (Utils.isEmpty(xml)) {
            d("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e("Invalid xml");
        }
    }


    private static int getStackOffset(StackTraceElement[] trace){
        for (int i = 0; i < trace.length; i++) {
            if (trace[i].getClassName().equals(SLog.class.getName())){
               return i+2;
            }
        }
        return 3;
    }


    private static void log(int priority,String msg) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int index = getStackOffset(trace);
        if (builder.showThread) {
            sb.append(Thread.currentThread().getName()+ " ");
        }
        if (builder.showFuntion){
            sb.append(trace[index].getMethodName()+" ");
        }
        sb.append("(").append(trace[index].getFileName()).append(":").append(trace[index].getLineNumber()).append(") ");

        String tag;
        if (TextUtils.isEmpty(builder.TAG)){
            String s =trace[index].getClassName();
            tag = s.substring(s.lastIndexOf(".")+1);
        }else {
            tag = builder.TAG;
        }

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = msg.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            sb.append(msg);
            logContent(priority, tag, sb.toString());
            return;
        }
        String header = sb.toString();
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(priority, tag, header + new String(bytes, i, count));
        }
    }



    private static void logContent(int logType, @Nullable String tag, @NonNull String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            builder.printer.log(logType,tag,line);
        }
    }





}

package com.sanvar.slogsimple;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sanvar.slog.Builder;
import com.sanvar.slog.FilePrinter;
import com.sanvar.slog.Printer;
import com.sanvar.slog.SLog;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements Printer {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        FilePrinter filePrinter = new FilePrinter(
                Log.INFO,           // 只将大于 info 级别的日志写入文件。
                path,               // 文件保存路径
                2,          //保存天数
                false);     // 是否要加密
        Builder b = new Builder();
        b.setTAG(TAG);           // set print tag .
        b.setPrinter(this);     // set printer  / filePrinter
        b.showFunctionInfo(true);// show func
        b.showThreadInfo(true);
        SLog.init(b);
        SLog.d("print debug message");
        SLog.d("-----------------------------------");
    }

    @Override
    protected void onStart() {
        super.onStart();
        SLog.i("print info message");
        SLog.i("-----------------------------------");
    }

    @Override
    protected void onResume() {
        super.onResume();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("age", 18);
            SLog.json(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SLog.i("-----------------------------------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SLog.w("print warn message");
        SLog.w("-----------------------------------");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SLog.e("print error meeeage");
        SLog.e("-----------------------------------");
    }

    @Override
    public boolean isLoggable(int priority) {
        return true;
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        // 不能在这里调用slog ，否则会死循环的。
        Log.println(priority, tag, message);
    }
}

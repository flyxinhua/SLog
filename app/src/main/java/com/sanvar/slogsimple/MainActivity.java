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

public class MainActivity extends Activity implements Printer{

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       String path=  Environment.getExternalStorageDirectory().getAbsolutePath();
        Builder builder = new Builder(
//                this
                new FilePrinter(Log.INFO,path,2,false)
                ,true,"",false);
        SLog.init(builder);

        SLog.i("zhesfdsafdsafdsfds");
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean isLoggable(int priority) {
        return true;
    }

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {

        /// 不能在这里调用slog ，否则会死循环的。
       Log.println(priority,tag,message);
    }
}

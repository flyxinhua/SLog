package com.sanvar.slog;


public  class Builder{
    Printer printer;
    String TAG;
    boolean showThread;
    boolean showFuntion;
    public  Builder setPrinter(Printer printer) {
        this.printer = printer;
        return this;
    }


    public  Builder setTAG(String tag){
        this.TAG = tag;
        return this;
    }

    public  Builder showThreadInfo(boolean show){
        this.showThread = show;
        return this;
    }

    public Builder showFuntionInfo(boolean showFuntion) {
        this.showFuntion = showFuntion;
        return this;
    }

    public Builder() {
    }

    /**
     *  日志工具类配置
     * @param printer  最终的打印类
     * @param showThread  是否显示线程信息
     * @param TAG   TAG ，如果没有TAG，默认使用类名
     * @param showFuntion 是否显示方法名。
     */
    public Builder(Printer printer, boolean showThread, String TAG,boolean showFuntion) {
        this.printer = printer;
        this.showThread = showThread;
        this.TAG = TAG;
        this.showFuntion = showFuntion;
    }
}

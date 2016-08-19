package cn.bingoogolapple.basenote.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.Iterator;
import java.util.Stack;

import cn.bingoogolapple.basenote.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/21 上午1:25
 * 描述:
 */
public class AppManager implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = AppManager.class.getSimpleName();
    private static AppManager sInstance;
    private int mActivityStartedCount = 0;
    private long mLastPressBackKeyTime;
    private Stack<Activity> mActivityStack = new Stack<>();
    private Context mContext;

    private AppManager() {
    }

    public static final AppManager getInstance() {
        if (sInstance == null) {
            synchronized (AppManager.class) {
                if (sInstance == null) {
                    sInstance = new AppManager();
                }
            }
        }
        return sInstance;
    }

    public AppManager init(Context context) {
        mContext = context.getApplicationContext();

        CrashHandler.getInstance().init(mContext);

        return this;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityStack.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (mActivityStartedCount == 0) {
            onEnterFrontStage();
        }
        mActivityStartedCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mActivityStartedCount--;
        if (mActivityStartedCount == 0) {
            onEnterBackStage();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.remove(activity);
    }

    public Activity currentActivity() {
        Activity activity = null;
        if (!mActivityStack.empty()) {
            activity = mActivityStack.lastElement();
        }
        return activity;
    }

    public void popOneActivity(Activity activity) {
        if (activity == null || mActivityStack.isEmpty()) {
            return;
        }
        if (!activity.isFinishing()) {
            activity.finish();
        }
        mActivityStack.remove(activity);
    }

    /**
     * 应用场景：支付完后，关闭 MainActivity 之外的其他页面
     *
     * @param activityClass
     */
    public void popOthersActivity(Class<Activity> activityClass) {
        if (activityClass == null || mActivityStack.isEmpty()) {
            return;
        }

        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (!activity.getClass().equals(activityClass)) {
                activity.finish();
                iterator.remove();
            }
        }
    }

    /**
     * 1500毫秒内，连续点击两次按返回键时退出应用程序
     */
    public void exitWithDoubleClick() {
        if (System.currentTimeMillis() - mLastPressBackKeyTime <= 1500) {
            exit();
        } else {
            mLastPressBackKeyTime = System.currentTimeMillis();
            ToastUtil.show(R.string.toast_exit_tip);
        }
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popOneActivity(activity);
        }
    }

    /**
     * 获取当前版本名称
     *
     * @return
     */
    public String getCurrentVersionName() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (Exception e) {
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            return "";
        }
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    public int getCurrentVersionCode() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            // 利用系统api getPackageName()得到的包名，这个异常根本不可能发生
            return 0;
        }
    }

    /**
     * 获取渠道号
     *
     * @return
     */
    private String getChannel() {
        try {
            ApplicationInfo appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            return "";
        }
    }

    private void onEnterFrontStage() {
        Logger.i(TAG, "进入前台状态");
    }

    private void onEnterBackStage() {
        Logger.i(TAG, "进入后台状态");
    }
}
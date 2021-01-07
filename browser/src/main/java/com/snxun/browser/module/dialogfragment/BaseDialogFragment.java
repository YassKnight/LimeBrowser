package com.snxun.browser.module.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.snxun.browser.R;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.android.FragmentEvent;
import com.trello.rxlifecycle4.components.support.RxDialogFragment;

/**
 * @ProjectName : LimeBrowser
 * @Author : Yangjw
 * @Time : 2021/1/5
 * @Description :
 */
public abstract class BaseDialogFragment extends RxDialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startCreate();
        findViews(view, savedInstanceState);
        setListeners(view);
        initData(view);
        endCreate();
    }

    protected void startCreate() {
    }

    protected void findViews(View view, Bundle savedInstanceState) {
    }

    protected void setListeners(View view) {
    }

    protected void initData(View view) {
    }

    protected void endCreate() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getContext(), R.style.BaseDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window wd = getDialog().getWindow();
        if (wd != null) {
            setWindowAnimations(wd);
            configDialogWindow(wd);
        }
    }

    /**
     * 设置弹窗动画
     *
     * @param window
     */
    private void setWindowAnimations(Window window) {
        int animation = configAnimations();
        if (animation != -1) {
            window.setWindowAnimations(animation);//设置窗口弹出动画
        }
    }

    /**
     * 配置弹窗动画
     *
     * @return
     */
    @StyleRes
    protected int configAnimations() {
        return -1;
    }

    /**
     * 配置Dialog的windows参数
     *
     * @param window
     */
    protected void configDialogWindow(Window window) {
    }

    @Nullable
    @Override
    public Context getContext() {
        Context ctx = super.getContext();
        if (ctx != null) {
            return ctx;
        }
        return requireContext();
    }

    /**
     * 绑定fragment的destroyView生命周期
     *
     * @param <T>
     * @return
     */
    protected <T> LifecycleTransformer<T> bindDestroyViewEvent() {
        return bindUntilEvent(FragmentEvent.DESTROY);
    }

    protected <Any> LifecycleTransformer<Any> bindAnyDestroyViewEvent() {
        return bindUntilEvent(FragmentEvent.DESTROY);
    }


}

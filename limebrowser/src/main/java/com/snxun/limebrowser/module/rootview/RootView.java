package com.snxun.limebrowser.module.rootview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.snxun.limebrowser.R;

import java.util.ArrayList;
import java.util.List;

import static androidx.customview.widget.ViewDragHelper.INVALID_POINTER;


public class RootView extends RelativeLayout {
    private static final String TAG = "RootView";
    //    public final static int SCROLL_HORIZONTALLY = 5;
    public final static int SCROLL_VERTICALLY = 6;
    public final static int SCROLL_NONE = 0;
    private int mTouchSlop;
    private float mPagingTouchSlop;
    private float mLastMotionY;
    private float mLastMotionX;
    private float mTotalMotionX;
    protected float mTotalMotionY;
    private Context mContext;
    private List<ScrollStateListener> mListeners = new ArrayList<>();
    private boolean mIsScrolling;
    private VelocityTracker mVelocityTracker;
    private int mFinalDistanceY;
    private int mFinalDistanceX;
    private int mDirection;
    private boolean mScrollHEnable = true;
    private boolean mScrollVEnable = true;

    private int mActivePointerId;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private OverScroller mScroller;
    private boolean mIsOverScroll;
    private float mCurrentVelocity;
    private int mDuration;
    ObjectAnimator mScrollAnimator;
    private Interpolator mLinearOutSlowInInterpolator;
    private float mRate;
    private boolean mStartedScroll = false;
    private boolean mIsAnimating = false;

    public RootView(@NonNull Context context) {
        this(context, null);
    }

    public RootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        init();
    }

    private void init() {
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mScroller = new OverScroller(mContext);
        mDuration = 400;
        mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(mContext, R.anim.linear_out_show_in);
    }

    /**
     * 回收速度追踪器
     */
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 重置触摸状态
     */
    private void resetTouchState() {
        mIsScrolling = false;
        mDirection = SCROLL_NONE;
        mCurrentVelocity = 0;
        mStartedScroll = false;
        recycleVelocityTracker();
    }

    private void onStartScroll() {
        mStartedScroll = true;
        for (ScrollStateListener listener : mListeners) {
            listener.onStartScroll(mDirection);
        }
    }

    private void doScroll() {
        onScroll(mRate);
        invalidate();
    }

    private void onScroll(float rate) {
        for (ScrollStateListener listener : mListeners) {
            listener.onScroll(rate);
        }
    }

    private void move(float x, float y) {
        for (ScrollStateListener listener : mListeners) {
            listener.move(x, y);
        }
    }

    private void onEndScroll() {
        mStartedScroll = false;
        for (ScrollStateListener listener : mListeners) {
            listener.onEndScroll();
        }
    }

    public RootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getChildCount() < 0) {
            return super.onInterceptTouchEvent(ev);
        }
        final int action = ev.getAction();
        boolean wasScrolling = mIsScrolling ||
                (mScrollAnimator != null && mScrollAnimator.isRunning());
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                stopScroller();
                mLastMotionY = ev.getY();
                mLastMotionX = ev.getX();
                mActivePointerId = ev.getPointerId(0);
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                initVelocityTrackerIfNotExists();
                mVelocityTracker.addMovement(ev);
                int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex < 0) {
                    mActivePointerId = INVALID_POINTER;
                    break;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                // Animate the doScroll back if we've cancelled
                if (wasScrolling) {
                    scrollToPositivePosition();
                }
                break;
            }
        }
        return wasScrolling;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getChildCount() <= 0) return super.onTouchEvent(ev);
        initVelocityTrackerIfNotExists();
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                stopScroller();
                Log.e(TAG, "onTouchEvent :: ACTION_DOWN");
                mLastMotionY = ev.getY();
                mLastMotionX = ev.getX();
                mActivePointerId = ev.getPointerId(0);
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = ev.getActionIndex();
                mActivePointerId = ev.getPointerId(index);
                mLastMotionX = (int) ev.getX(index);
                mLastMotionY = (int) ev.getY(index);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) break;
                mVelocityTracker.addMovement(ev);
                int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                int x = (int) ev.getX(activePointerIndex);
                int y = (int) ev.getY(activePointerIndex);
                move(x, y);
                if (mIsScrolling) {
                    float delta = 0f;
                    if (!attachToFinal()) {
                        if (mDirection == SCROLL_VERTICALLY) {
                            delta = y - mLastMotionY;
                            mTotalMotionY += delta;
                            mRate = mTotalMotionY / mFinalDistanceY;
                            doScroll();
                        }
                    }
                    mLastMotionY = y;
                    mLastMotionX = x;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = ev.getActionIndex();
                int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // Select a new active pointer id and reset the motion state
                    final int newPointerIndex = (pointerIndex == 0) ? 1 : 0;
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                    mLastMotionX = (int) ev.getX(newPointerIndex);
                    mLastMotionY = (int) ev.getY(newPointerIndex);
                    mVelocityTracker.clear();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                if (mDirection == SCROLL_VERTICALLY) {
                    mCurrentVelocity = (int) mVelocityTracker.getYVelocity(mActivePointerId);
                }
                checkPoint();
                break;
            }
        }
        return true;
    }

    public boolean isAnimating() {
        return mScrollAnimator != null && mScrollAnimator.isRunning() || mIsAnimating;
    }

    private boolean attachToFinal() {
        if (mDirection == SCROLL_VERTICALLY) {
            return mRate <= -1.0f;
        } else {
            return true;
        }
    }

    void stopScroller() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
            mScroller.abortAnimation();
        }
    }

    void animateScroll(float curScroll, float newScroll, final Runnable postRunnable) {
        // Finish any current scrolling animations
        stopScroller();
        if (mScrollAnimator != null) {
            if (mScrollAnimator.isRunning()) {
                mScrollAnimator.cancel();
            }
        }
        mScrollAnimator = ObjectAnimator.ofFloat(this, "rate", curScroll, newScroll);
        mScrollAnimator.setDuration(mDuration);
        mScrollAnimator.setInterpolator(mLinearOutSlowInInterpolator);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                setRate((Float) valueAnimator.getAnimatedValue());
            }
        });
        mScrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (postRunnable != null) {
                    postRunnable.run();
                }
                mScrollAnimator.removeAllListeners();
            }
        });
        mScrollAnimator.start();
    }

    private void scrollToPositivePosition() {
        float curRate = getRate();
        float positiveRate = 0.0f;
        if (Float.compare(curRate, positiveRate) != 0) {
            animateScroll(curRate, positiveRate, new Runnable() {
                @Override
                public void run() {
                    checkPoint();
                }
            });
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (attachToFinal()) {
            endScroll();
        }
        if (mScroller.computeScrollOffset()) {
            if (!attachToFinal()) {
                if (mScroller.isFinished()) {
                    scrollToPositivePosition();
                } else {
                    if (mDirection == SCROLL_VERTICALLY) {
                        mTotalMotionY = mScroller.getCurrY();
                        mRate = mTotalMotionY / mFinalDistanceY;
                        doScroll();
                    }
                }
            }
        }
        super.computeScroll();
    }

    private void endScroll() {
        setRate(0.0f);
        onEndScroll();
        resetTouchState();
        stopScroller();
    }

    private void checkPoint() {
        if (!mIsScrolling) {
            return;
        }
        if (!attachToFinal()) {
            onStartScroll();
            scrollToPositivePosition();
        } else {
            endScroll();
        }
    }


    public void setRate(float rate) {
        if (!mStartedScroll) {
            return;
        }
        if (mDirection == SCROLL_VERTICALLY) {
            mTotalMotionY = mFinalDistanceY * rate;
        }
        mRate = rate;
        doScroll();
    }

    public float getRate() {
        return mRate;
    }


    public interface ScrollStateListener {
        void onStartScroll(int direction);

        void onScroll(float rate);

        void onEndScroll();

        void move(float x, float y);//手指位置
    }
}

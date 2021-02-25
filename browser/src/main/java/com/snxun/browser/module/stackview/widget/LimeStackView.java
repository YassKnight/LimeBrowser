package com.snxun.browser.module.stackview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.database.Observable;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.snxun.browser.R;
import com.snxun.browser.module.stackview.adapter.StackAdapter;
import com.snxun.browser.module.stackview.util.SwipeHelper;

import java.util.ArrayList;
import java.util.List;


public class LimeStackView extends FrameLayout implements SwipeHelper.Callback {
    private static final String TAG = "UCStackView";
    private static final int INVALID_POINTER = -1;
    public static final int INVALID_POSITION = -1;
    private static final int LAYOUT_ALL = 0;
    private static final int LAYOUT_PRE_ACTIVE = -1;
    public static final int LAYOUT_AFTER_ACTIVE = 2;
    public static final int LAYOUT_SINGLE_ACTIVE = 1;
    public static final float PROGRESS_STEP = 0.20f; // 每个页面的进度差
    public static final float BASE_MAX_SCROLL_P = 0.72f; // 用于标记参考页最大能滑动的进度
    public static final float BASE_MIN_SCROLL_P = 0.2f; // 用于标记参考页最小能滑动的进度
    public static final float PROGRESS_START = 0.45f; // 规定一个定值，用于计算选择页初始进度
    public static final float DEFAULT_VIEW_MAX_SCALE = 0.9f;
    public static final float DEFAULT_VIEW_MIN_SCALE = 0.7f;
    private StackAdapter mStackAdapter;
    private int mSelectTab = 0;
    private List<ViewHolder> mViewHolders;
    private int mDuration;
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mActivePointerId;
    private boolean mScrollEnable;
    private Context mContext;
    private Rect mChildTouchRect[]; // 用于存储view 的边际（点击范围）
    private int mScreenWidth;
    private int mScreenHeight;
    private float mViewMinTop;
    private float mViewMaxTop;
    private float mViewMinScale;
    private float mViewMaxScale;
    private float mViewMinAlpha;
    private float mViewMaxAlpha;
    private float mMinScrollP;
    private float mMaxScrollP;
    private float mMinPositiveScrollP;
    private float mMaxPositiveScrollP;
    private float mScrollProgress; // 滑动进度
    private final ViewDataObserver mObserver = new ViewDataObserver();
    private boolean mInterceptedBySwipeHelper;
    private SwipeHelper mSwipeHelper;
    private float mLastMotionY;
    private float mTotalMotionY;
    private float mInitialMotionX;
    private float mLastMotionX;
    private float mInitialMotionY;
    private boolean mIsScrolling;
    private boolean mIsFling;
    private boolean mIsOverScroll = false;
    ObjectAnimator mScrollAnimator;
    private int mActivePager; // 标记横向滑动时的拖拽页
    boolean mIsFirstLayout = true;
    private int mLayoutState = LAYOUT_ALL;
    private Interpolator mLinearOutSlowInInterpolator;
    private View mPreviousView;
    private View mTargetView;
    private boolean mIsAnimating = false;
    private OnChildDismissedListener mListener;
    private boolean mLockLayout = false;

    public LimeStackView(@NonNull Context context) {
        this(context, null);
    }

    public LimeStackView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LimeStackView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mViewHolders = new ArrayList<ViewHolder>();
        // 初始化 scroller
        mScroller = new OverScroller(mContext);
        // 摩擦力设置
        mScroller.setFriction(0.02f);
        ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();
        Resources resources = mContext.getResources();
        mScreenWidth = getScreenSize(mContext).x;
        mScreenHeight = getScreenSize(mContext).y;
        mViewMinTop = 0;
        mViewMaxTop = mScreenHeight;
        mViewMinScale = DEFAULT_VIEW_MIN_SCALE;
        mViewMaxScale = DEFAULT_VIEW_MAX_SCALE;
        mDuration = 600;
        float densityScale = resources.getDisplayMetrics().density;
        // 初始化 SwipeHelper 检测方向 X
        mSwipeHelper = new SwipeHelper(mContext, SwipeHelper.X, this, densityScale, mTouchSlop);
        mSwipeHelper.setMinAlpha(1f);
        mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(mContext, R.anim.linear_out_show_in);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // 如果首次构建view的时候我们需要先初始化滑动进度
        super.onLayout(changed, left, top, right, bottom);
        if (mIsFirstLayout) {
            calculateInitialScrollP();
            mIsFirstLayout = false;
        }
        layoutChildren();
    }

    public void setOnChildDismissedListener(OnChildDismissedListener listener) {
        mListener = listener;
    }

    /**
     * 这个是核心方法，在这里我们根据每个view的位置，设置大小，transY ，以及点击范围
     */
    private void layoutChildren() {
        if (mLockLayout) {
            return;
        }
        int childCount = getChildCount();
        float progress;
        float transY;
        View child;
        mChildTouchRect = new Rect[childCount];// 子view的触控范围
        for (int i = 0; i < childCount; i++) {
            child = getChildAt(i);
            Rect rect = new Rect();
            child.getHitRect(rect);
            mChildTouchRect[i] = rect;

            // 设置点击范围

            // 根据 mLayoutState 决定要更新哪些view的属性，在删除页面时用到
            switch (mLayoutState) {
                case LAYOUT_PRE_ACTIVE:
                    if (i > mActivePager) {
                        continue;
                    }
                    break;
                case LAYOUT_AFTER_ACTIVE:
                    if (i < mActivePager) {
                        continue;
                    }
                    break;
                case LAYOUT_SINGLE_ACTIVE:
                    if (i != mSelectTab) {
                        continue;
                    }
                    break;
            }
            progress = getScrollP();
            float scale = calculateProgress2Scale(i, progress);
            if (scale < mViewMinScale) {
                scaleView(mViewMinScale, child);
                translateViewY(mViewMinTop, child, i);
                child.setVisibility(GONE);
                Log.e(TAG, "滤过第" + i + "个child" + mIsScrolling);
                continue;
            }
            if (scale > mViewMaxScale) {
                scaleView(mViewMaxScale, child);
                translateViewY(mViewMaxTop, child, i);
                child.setVisibility(GONE);
                Log.e(TAG, "滤过第" + i + "个child" + mIsScrolling);
                continue;
            }
            if (child.getVisibility() == GONE) {
                child.setVisibility(VISIBLE);
            }
            transY = calculateProgress2TransY(i, progress);
            Log.e(TAG, "layoutChildren :: progress =:" + progress + ",transY =:" + transY);
            translateViewY(transY, child, i);
            scaleView(scale, child);
        }
        invalidate();
    }

    // 适配器
    public void setAdapter(StackAdapter adapter) {
        mStackAdapter = adapter;
        mStackAdapter.registerObserver(mObserver);
        refreshViews();
    }

    // 速度追踪器
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

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    // 如果调用adapte的notifyDataChanged方法会调用这个方法
    private void refreshViews() {
        removeAllViews();
        mViewHolders.clear();
        int count = mStackAdapter.getItemCount();
        for (int i = 0; i < count; i++) {
            ViewHolder holder = getViewHolder(i);
            holder.position = i;
            addView(holder.itemView);
            mStackAdapter.bindViewHolder(holder, i);
        }
        requestLayout();
    }

    // 更细view的x
    private void translateViewX(float transX, View view) {
        view.setTranslationX(transX);
    }

    // 更新view的y
    private void translateViewY(float transY, View view, int index) {
        if (index == 0)
            view.setTranslationY(transY);
        else
            view.setTranslationY(transY * 2.5f);
    }

    // 更新view的z
    private void translateViewZ(float transZ, View view) {
        view.setTranslationZ(transZ);
    }

    private void scaleView(float scale, View view) {
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    private void alphaView(float alpha, View view) {
        view.setAlpha(alpha);
    }


    /**
     * 计算view的TransY，首先根据参照进度，来算出各个view的偏移进度，然后偏移进度4次方来扩大差异
     * 最后在得出目标TransY
     * mViewMinTop 为view最高能滑动到的地方
     * mViewMaxTop 为view最低能滑动到的地方
     *
     * @param i        view 的索引值
     * @param progress 参考进度
     */
    int calculateProgress2TransY(int i, float progress) {
        return (int) (mViewMinTop +
                Math.pow(calculateViewProgress(i, progress), 4) * (mViewMaxTop - mViewMinTop));
    }

    int calculateProgress2TransZ(float progress) {
        return (int) (mViewMinTop + Math.pow(progress, 3) * (200));
    }

    /**
     * 计算scale
     * mViewMaxScale 为view最大scale
     * mViewMinScale 为view最小的scale
     *
     * @param i        view 的位置
     * @param progress 参考进度
     */
    float calculateProgress2Scale(int i, float progress) {
        float scaleRange = (mViewMaxScale - mViewMinScale);
        return mViewMinScale + (calculateViewProgress(i, progress) * scaleRange);
    }

    /**
     * calculate from the progress along the curve to a scale.
     */
    float calculateProgress2Alpha(float progress) {
        if (progress < 0) return mViewMinScale;
        if (progress > 1) return mViewMaxScale;
        float scaleRange = (mViewMaxScale - mViewMinScale);
        float scale = mViewMinScale + (progress * scaleRange);
        return scale;
    }

    /**
     * 当点击某一页面时，代表选择了这个页面，然后把UCRootView动画展示出来
     *
     * @param index 页面的ID
     */
    public void selectTab(int index, Runnable onComplete, boolean isShowAnimating) {
        mSelectTab = index;
        if (isShowAnimating)
            animateShow(mSelectTab, mPreviousView, mTargetView, false, onComplete);
    }

    /**
     * 点击“X”按钮时关闭这个页面
     *
     * @param index
     */
    public void closeTab(int index) {
        mSwipeHelper.dismissChildByClick(getChildAt(index));
        mIsAnimating = true;
    }

    /**
     * 计算阻尼，当超过我们设定的位置时，让用户在滑动的时候感到“吃力”
     *
     * @return
     */
    private float calculateDamping() {
        float damping = (1.0f - Math.abs(mScrollProgress - getPositiveScrollP()) * 5);
        Log.d(TAG, "calculateDamping :: damping = :" + damping);
        return damping;
    }

    /**
     * 进度的计算方式也是移动的距离和目标距离的比
     *
     * @return
     */
    private float getScrollRate() {
        float topSpace = mViewMaxTop;
        return mTotalMotionY / topSpace;
    }

    /**
     * 进入到页面管理界面时的动画
     *
     * @param selectPager         ：用户选择的那个页面
     * @param from                ：前一个view ：UCRootView
     * @param to                  ：页面管理页
     * @param show：展示或隐藏这个界面
     * @param onCompletedRunnable ：动画执行完后如果有这个Runnable，则run一下
     */
    public void animateShow(
            int selectPager,
            final View from,
            final View to,
            final boolean show,
            final Runnable onCompletedRunnable) {
        if (mIsAnimating) {
            return;
        }
        mIsAnimating = true;
        int duration = 350;
        int startDelay = 40;
        mSelectTab = selectPager;
        mPreviousView = from;
        mTargetView = to;
        //如果是显示这个页面，先要更新滑动的进度然后获取selected view的各种属性才是我们想要的
        mLayoutState = LAYOUT_ALL;
        if (show) {
            calculateInitialScrollP();
            layoutChildren();
        }
        mLayoutState = LAYOUT_SINGLE_ACTIVE;
        final View selectChild = getChildAt(selectPager);
        float nextChildEndTransY = 0;
        float nextChildStartTransY = 0;
        int endRange = show ? Math.min(selectPager + 3, getChildCount()) : getChildCount();
        Log.e(TAG, "animateShow :: selectTab =:" + selectPager + ",endRange =:" + endRange);
        // 如果选择的页面不是最后一页，我们在进入此界面时，会看到选择页以下的view有个上移动画
        for (int i = selectPager; i < endRange; i++) {
            View nextChild = getChildAt(i);
            nextChildStartTransY = show ? getScreenSize(getContext()).y : nextChild.getTranslationY();
            nextChildEndTransY = show ? nextChild.getTranslationY() : getScreenSize(getContext()).y;
            ObjectAnimator nextChildAnimator = ObjectAnimator.ofFloat(
                    nextChild, "translationY", nextChildStartTransY, nextChildEndTransY);
            nextChildAnimator.setDuration(duration);
            if (show) {
                nextChildAnimator.setStartDelay(startDelay);
            }
            nextChildAnimator.start();
            Log.e(TAG, "animateShow :: transY = :" + nextChildStartTransY + " ,nextChildEndTransY =:" + nextChildEndTransY);
        }

        float transY = selectChild.getTranslationY();
        float scaleX = selectChild.getScaleX();
        float scaleY = selectChild.getScaleY();
        float startScaleX = show ? 1.0f : scaleX;
        float startScaleY = show ? 1.0f : scaleY;
        float startTransY = show ? 0 : transY;
        float endScaleX = show ? scaleX : 1.0f;
        float endScaleY = show ? scaleY : 1.0f;
        float endTransY = show ? transY : 0;
        /*
        final float dis = Math.abs(endScaleX - startScaleX);
        from.setScaleX(startScaleX);
        from.setScaleY(startScaleY);
        from.setTranslationY(startTransY);
        */
        selectChild.setScaleX(startScaleX);
        selectChild.setScaleY(startScaleY);
        selectChild.setTranslationY(startTransY);
        if (show) {
            to.setAlpha(0);
        }
        //定义属性动画
        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", startScaleX, endScaleX);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", startScaleY, endScaleY);
        PropertyValuesHolder transYHolder = PropertyValuesHolder.ofFloat("translationY", startTransY, endTransY);
        ObjectAnimator showAnimator = ObjectAnimator.ofPropertyValuesHolder(selectChild, scaleXHolder, scaleYHolder, transYHolder);
        showAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                /*
                float value = ((Float) valueAnimator.getAnimatedValue()).floatValue();

                float fromViewAlpha = (1.0f - (1.f - value) / dis * 1.2f);
                Log.e(TAG,"value =:" + fromViewAlpha);
                float toViewAlpha = 1.0f - fromViewAlpha;
                from.setAlpha(fromViewAlpha);
                to.setAlpha(toViewAlpha > 1.0f ? 1.0f : toViewAlpha);
                */
            }
        });
        showAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onCompletedRunnable != null) {
                    onCompletedRunnable.run();
                }
                mLockLayout = false;
                mLayoutState = LAYOUT_ALL;
                layoutChildren();
                mIsAnimating = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mLockLayout = true;
                if (show) {
                    mPreviousView.setVisibility(GONE);
                }
                mTargetView.setVisibility(VISIBLE);
                selectChild.setVisibility(VISIBLE);
                to.setAlpha(1.0f);
            }
        });
        if (show) {
            showAnimator.setStartDelay(startDelay);
        }
        showAnimator.setDuration(duration);
        showAnimator.start();
    }

    /**
     * 用于计算每个view的滑动进度
     *
     * @param index    view的位置
     * @param progress 参考进度
     * @return
     */
    private float calculateViewProgress(int index, float progress) {
        return PROGRESS_STEP * index + progress;
    }

    /**
     * 删除页面后，我们会更新滑动的范围
     */
    private void updateScrollProgressRange() {
        mMinScrollP = BASE_MIN_SCROLL_P - (getChildCount() - 2) * PROGRESS_STEP;
        mMaxScrollP = BASE_MAX_SCROLL_P;
        mMinPositiveScrollP = mMinScrollP + PROGRESS_STEP * 0.25f;
        mMaxPositiveScrollP = mMaxScrollP - PROGRESS_STEP * 0.75f * 1.5f;
    }

    /**
     * 初始化滑动进度
     */
    private void calculateInitialScrollP() {
        updateScrollProgressRange();
        mScrollProgress = PROGRESS_START - mSelectTab * PROGRESS_STEP;
        mTotalMotionY = mScrollProgress * mViewMaxTop;
    }

    /**
     * 根据我们的参考进度还原滑动的距离
     *
     * @param progress
     * @return
     */
    private float calculateProgress2Y(float progress) {
        return progress * mViewMaxTop;
    }


    private ViewHolder getViewHolder(int position) {
        if (position == INVALID_POSITION) {
            return null;
        }
        ViewHolder viewHolder;
        if (mViewHolders.size() <= position ||
                mViewHolders.get(position).itemViewType != mStackAdapter.getItemViewType(position)) {
            viewHolder = mStackAdapter.createView(this, mStackAdapter.getItemViewType(position));
            mViewHolders.add(viewHolder);
        } else {
            viewHolder = mViewHolders.get(position);
        }
        return viewHolder;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getChildCount() <= 0 || isAnimating()) {
            return false;
        }
        int action = ev.getAction();
        // 如果是横向滑动，交由SwipeHelper处理
        mInterceptedBySwipeHelper = mSwipeHelper.onInterceptTouchEvent(ev);
        if (mInterceptedBySwipeHelper) {
            return true;
        }
        // 如果我们的滑动动画在执行，标记为滑动
        boolean wasScrolling = mIsScrolling ||
                (mScrollAnimator != null && mScrollAnimator.isRunning());
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                // 记录初始触摸点
                mInitialMotionX = mLastMotionX = (int) ev.getX();
                mInitialMotionY = mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                // 如果已经在滚动，停止他
                stopScroller();
                // 初始化速度追踪器
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) break;
                // Initialize the velocity tracker if necessary
                initVelocityTrackerIfNotExists();
                mVelocityTracker.addMovement(ev);
                // 处理多指情况
                int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex < 0) {
                    Log.d(TAG, "findPointerIndex failed");
                    mActivePointerId = INVALID_POINTER;
                    break;
                }
                int y = (int) ev.getY(activePointerIndex);
                int x = (int) ev.getX(activePointerIndex);
                if (Math.abs(y - mInitialMotionY) > mTouchSlop) {
                    // Save the move move info
                    mIsScrolling = true;
                }

                mLastMotionX = x;
                mLastMotionY = y;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = ev.getActionIndex();
                int pointerId = ev.getPointerId(pointerIndex);
                Log.d(TAG, "Ignore multi-move "
                        + pointerIndex + "(" + pointerId + ")");
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                // Animate the doScroll back if we've cancelled
                if (wasScrolling) {
                    scrollToPositivePosition();
                }
                // Reset the drag state and the velocity tracker
                break;
            }
        }
        if (wasScrolling || mIsScrolling) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getChildCount() <= 0 || isAnimating()) {
            return false;
        }
        int action = ev.getAction();
        // Pass through to swipe helper if we are swiping
        if (mInterceptedBySwipeHelper && mSwipeHelper.onTouchEvent(ev)) {
            return true;
        }
        // Update the velocity tracker
        initVelocityTrackerIfNotExists();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                // 记录初始触摸点
                mInitialMotionX = mLastMotionX = (int) ev.getX();
                mInitialMotionY = mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                // 如果已经在滚动，停止他
                stopScroller();
                // 初始化速度追踪器
                initOrResetVelocityTracker();
                mVelocityTracker.addMovement(ev);
                // Disallow parents from intercepting move events
                break;
            }
            //处理多指
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
                int yTotal = Math.abs(y - (int) mInitialMotionY);
                float deltaP = mLastMotionY - y;
                if (!mIsScrolling) {
                    if (yTotal > mTouchSlop) {
                        mIsScrolling = true;
                    }
                }
                if (mIsScrolling) {
                    // mTotalMotionY 就是我们滑动的总距离
                    if (isOverPositiveScrollP()) {
                        // calculateDamping() 为计算阻尼的方法，即当overscroll时，实现越来越难滑的效果
                        mTotalMotionY -= deltaP * (calculateDamping());
                    } else {
                        mTotalMotionY -= deltaP;
                    }
                    // 更新view
                    doScroll();
                }

                mLastMotionX = x;
                mLastMotionY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocity = (int) mVelocityTracker.getYVelocity(mActivePointerId);
                // 速度很大时，执行scroller.fling()方法，让界面跑一会儿
                if (mIsScrolling && (Math.abs(velocity) > mMinimumVelocity)) {
                    fling(velocity);
                } else {
                    // 滑动到目标位置
                    scrollToPositivePosition();
                }
                // 重置滑动状态
                resetTouchState();
                Log.e(TAG, "onTouchEvent ACTION_UP :: mIsOverScroll =:" + mIsOverScroll);
                break;
            }
            // 更新触控信息
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
            case MotionEvent.ACTION_CANCEL: {
                scrollToPositivePosition();
                resetTouchState();
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * @return 是否超过规定位置
     */
    private boolean computeScrollProgress() {
        if (getChildCount() <= 0) {
            return false;
        }
        mIsOverScroll = false;
        mScrollProgress = getScrollRate();
        mIsOverScroll = (mScrollProgress > mMaxScrollP || mScrollProgress < mMinScrollP);
        return mIsOverScroll;
    }

    public void setScrollP(float progress) {
        Log.e(TAG, "rate =:" + progress);
        mTotalMotionY = calculateProgress2Y(progress);// 将progress转化为移动距离
        mScrollProgress = progress;
        layoutChildren();
    }

    public float getScrollP() {
        return mScrollProgress;
    }

    /**
     * 根据滑动的进度来判断手指释放后需要自动回滚的目标进度
     */
    float getPositiveScrollP() {
        if (mScrollProgress < mMinPositiveScrollP) {
            return mMinPositiveScrollP;
        } else if (mScrollProgress > mMaxPositiveScrollP) {
            return mMaxPositiveScrollP;
        }
        return mScrollProgress;
    }

    boolean isOverPositiveScrollP() {
        return (mScrollProgress > mMaxPositiveScrollP || mScrollProgress < mMinPositiveScrollP);
    }

    void stopScroller() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        mIsScrolling = false;
    }

    public boolean isAnimating() {
        return mScrollAnimator != null && mScrollAnimator.isRunning() || mIsAnimating;
    }

    /**
     * 手指释放后，如果滑动到的位置不是我们的期望位置（比如滑过了），需要自动回滚
     *
     * @param curScroll    当前进度
     * @param newScroll    目标进度
     * @param postRunnable 滚到目标位置后需要执行的动作
     */
    void animateScroll(float curScroll, float newScroll, final Runnable postRunnable) {
        // Finish any current scrolling animations
        stopScroller();
        // 根据属性“scrollP”定义滑动动画
        mScrollAnimator = ObjectAnimator.ofFloat(this, "scrollP", curScroll, newScroll);
        //  动画时间
        mScrollAnimator.setDuration(mDuration);
        // 插值器
        mScrollAnimator.setInterpolator(mLinearOutSlowInInterpolator);
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                // 更新 progress
                setScrollP((Float) valueAnimator.getAnimatedValue());
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

    /**
     * 手指离开屏幕后滚到目标位置
     */
    private void scrollToPositivePosition() {
        Log.e(TAG, "scrollToPositivePosition mScrollProgress =:" + mScrollProgress);
        float curScroll = getScrollP();
        float positiveScrollP = getPositiveScrollP();
        // 当前progress和目标progress不一样时执行
        if (Float.compare(curScroll, positiveScrollP) != 0) {
            animateScroll(curScroll, getPositiveScrollP(), new Runnable() {
                @Override
                public void run() {
                    // 动画结束后重置滑动状态
                    resetTouchState();
                }
            });
            invalidate();
        }
    }

    /**
     * 执行滚动
     */
    private void doScroll() {
        computeScrollProgress(); // 判断是否overScroll
        layoutChildren(); // 改变每个view的属性
    }

    private void resetTouchState() {
        mActivePointerId = INVALID_POINTER;
        mIsScrolling = false;
        recycleVelocityTracker();
    }

    @Override
    public void computeScroll() {
        Log.e(TAG, "computeScroll :: mIsOverScroll :" + mIsOverScroll + ".mScroller.computeScrollOffset() =:" + mScroller.computeScrollOffset());
        if (mScroller.computeScrollOffset()) {
            if (mIsOverScroll) {
                // 如果 overscroll 滑动到指定位置
                scrollToPositivePosition();
            } else {
                if (mScroller.isFinished()) {
                    scrollToPositivePosition();
                }
                mTotalMotionY = mScroller.getCurrY();
                doScroll();
            }
        }
        super.computeScroll();
    }

    /**
     * 如果我们手指离开屏幕时滑动速度很快，让view飞一会，X方向忽略
     *
     * @param velocity
     */
    public void fling(int velocity) {
        mScroller.fling(
                0,
                (int) mTotalMotionY,
                0,
                velocity,
                0,
                0,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE);
        invalidate();
    }

    private boolean mIsDisallowIntercept = false;

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // keep the info about if the innerViews do
        // requestDisallowInterceptTouchEvent
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // the incorrect array size will only happen in the multi-touch
        // scenario.
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    /**
     * 更具手指的位置判断触摸的是哪个view
     *
     * @param event
     * @return
     */
    private View findChildAtPosition(MotionEvent event) {
        if (event.getPointerCount() < 1) {
            return null;
        }
        int activePointId = event.getPointerId(0);
        if (activePointId == INVALID_POINTER) {
            return null;
        }
        int activePointerIndex = event.findPointerIndex(activePointId);
        int x = (int) event.getX(activePointerIndex);
        int y = (int) event.getY(activePointerIndex);
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            if (mChildTouchRect[i].contains(x, y)) {
                mActivePager = i;
                return getChildAt(i);
            }
        }
        return null;
    }

    public View getSelectedChild() {
        if (mSelectTab < 0 || mSelectTab >= getChildCount()) {
            return null;
        }
        return getChildAt(mSelectTab);
    }

    @Override
    public View getChildAtPosition(MotionEvent ev) {
        return findChildAtPosition(ev);
    }

    @Override
    public boolean canChildBeDismissed(View v) {
        return false;
    }

    @Override
    public void onBeginDrag(View v) {
        Log.d(TAG, "onBeginDrag :: v =:" + v);
    }

    @Override
    public void onSwipeChanged(View v, float delta) {
        if (v == null) {
            return;
        }
        float alpha = 1.f - Math.abs(delta) / mScreenWidth * 0.5f;
        alphaView(alpha, v);
        Log.d(TAG, "onSwipeChanged :: delta =:" + delta);
    }

    @Override
    public void onChildDismissed(final View v) {
        final float curProgress = getScrollP();
        float newProgress = curProgress + PROGRESS_STEP;
        mLayoutState = LAYOUT_PRE_ACTIVE;
        if (newProgress > mMaxPositiveScrollP) {
            newProgress = curProgress - PROGRESS_STEP;
            mLayoutState = LAYOUT_AFTER_ACTIVE;
        } else if (newProgress < mMinPositiveScrollP) {
            newProgress = curProgress + PROGRESS_STEP;
            mLayoutState = LAYOUT_PRE_ACTIVE;
        }
        animateScroll(curProgress, newProgress, new Runnable() {
            @Override
            public void run() {
                removeView(v);
                if (mLayoutState == LAYOUT_AFTER_ACTIVE) {
                    setScrollP(curProgress);
                }
                mLayoutState = LAYOUT_ALL;
                updateScrollProgressRange();
                if (mListener != null) {
                    mListener.onChildDismissed(mActivePager);
                }
                Log.e(TAG, "onChildDismissed ---------------------- mActivePager =:" + mActivePager);
                mActivePager = INVALID_POSITION;
                mIsAnimating = false;
            }
        });
    }

    @Override
    public void onSnapBackCompleted(View v) {
        mIsAnimating = false;
    }

    @Override
    public void onDragCancelled(View v) {
        mIsAnimating = false;
    }

    @Override
    public void onChildFling(View v) {
        mIsAnimating = true;
    }

    public Point getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point realSize = new Point();
        display.getRealSize(realSize);
        return realSize;
    }

    public static abstract class Adapter<VH extends ViewHolder> {
        // 被观察者
        private final AdapterDataObservable observable = new AdapterDataObservable();

        // 创建view
        public VH createView(ViewGroup parent, int viewType) {
            VH holder = onCreateView(parent, viewType);
            holder.itemViewType = viewType;
            return holder;
        }

        protected abstract VH onCreateView(ViewGroup parent, int viewType);

        // 绑定view
        public void bindViewHolder(VH holder, int position) {
            onBindViewHolder(holder, position);
        }

        protected abstract void onBindViewHolder(VH holder, int position);

        // 获取item count
        public abstract int getItemCount();

        public final void notifyDataSetChanged() {
            observable.notifyDataChanged();
        }

        public int getItemViewType(int position) {
            return 0;
        }

        // 注册观察者
        public void registerObserver(AdapterDataObserver observer) {
            observable.registerObserver(observer);
        }

    }

    public static class AdapterDataObservable extends Observable<AdapterDataObserver> {
        // mObservers 观察者集合
        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        // 通知各位观察者
        public void notifyDataChanged() {
            for (AdapterDataObserver observer : mObservers) {
                observer.onChanged();
            }
        }
    }

    public static abstract class ViewHolder {
        public View itemView;
        public int itemViewType;
        int position;

        public ViewHolder(View view) {
            itemView = view;
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }

    public static abstract class AdapterDataObserver {
        public void onChanged() {

        }
    }

    private class ViewDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            refreshViews();
        }
    }

    public interface OnChildDismissedListener {
        void onChildDismissed(int index);
    }
}

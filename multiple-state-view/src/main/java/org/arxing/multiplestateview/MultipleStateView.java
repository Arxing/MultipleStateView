package org.arxing.multiplestateview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ViewAnimator;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by arxing on 2017/8/16.
 */
@SuppressLint("DefaultLocale")
public class MultipleStateView extends ViewAnimator {
    /// properties
    private int transDuration;
    private boolean isAnimating;
    private int selectIndex;
    private boolean firstEnterAnimation;

    private Map<String, Integer> regMap = new HashMap<>();
    private String selectName;
    private boolean useIndex;
    private Animation inAnim, outAnim;

    public MultipleStateView(Context context) {
        super(context);
        initAttrs(null);
        init();
    }

    public MultipleStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) {
            transDuration = 100;
            selectIndex = 0;
            useIndex = true;
            firstEnterAnimation = false;
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MultipleStateView);
            transDuration = ta.getInt(R.styleable.MultipleStateView_msv_transDuration, 100);
            try {
                selectIndex = ta.getInt(R.styleable.MultipleStateView_msv_select, 0);
                useIndex = true;
            } catch (Exception e) {
                selectName = ta.getString(R.styleable.MultipleStateView_msv_select);
                useIndex = false;
            }
            firstEnterAnimation = ta.getBoolean(R.styleable.MultipleStateView_msv_first_enter_anim, false);
            ta.recycle();
        }
    }

    private void init() {
        setAnimateFirstView(firstEnterAnimation);
        if (!isInEditMode()) {
            inAnim = new AlphaAnimation(0, 1);
            inAnim.setInterpolator(new LinearInterpolator());
            inAnim.setDuration(transDuration);
            inAnim.setAnimationListener(animListener);
            setInAnimation(inAnim);

            outAnim = new AlphaAnimation(1, 0);
            outAnim.setInterpolator(new LinearInterpolator());
            outAnim.setDuration(transDuration);
            outAnim.setAnimationListener(animListener);
            setOutAnimation(outAnim);
        }
    }

    @Override public void setDisplayedChild(int whichChild) {
        if (isAnimating)
            return;
        if (whichChild == getDisplayedChild())
            return;
        if (whichChild > getChildCount() || whichChild < 0)
            throw new ArrayIndexOutOfBoundsException();
        selectIndex = whichChild;
        useIndex = true;
        super.setDisplayedChild(whichChild);
    }

    /**
     * Measured self width and height in this method. The width value followed with self measure spec,
     * but the height value followed with current child height.
     * Finally, adjust margin and padding.
     *
     * exact value:
     *      set size with child height value, mode is exact.
     * wrap_content:
     *      measure child height first, then set size with child measured height, mode is exact.
     * match_parent:
     *      set size with self size value, mode is exact.
     */
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int thisHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int thisHeightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int newHeightSpecSize;
        int newHeightSpecMode;

        View targetChild = getChildAt(selectIndex);
        int targetLayoutH = targetChild.getLayoutParams().height;

        switch (targetLayoutH) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                targetChild.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(thisHeightSpecSize, MeasureSpec.AT_MOST));
                newHeightSpecSize = targetChild.getMeasuredHeight();
                newHeightSpecMode = MeasureSpec.EXACTLY;
                break;
            case ViewGroup.LayoutParams.MATCH_PARENT:
                newHeightSpecSize = thisHeightSpecSize;
                newHeightSpecMode = MeasureSpec.EXACTLY;
                break;
            default:
                newHeightSpecSize = targetChild.getMeasuredHeight();
                newHeightSpecMode = MeasureSpec.EXACTLY;
                break;
        }
        ViewGroup.LayoutParams layoutParams = targetChild.getLayoutParams();
        if (layoutParams instanceof MarginLayoutParams) {
            int topMargin = ((MarginLayoutParams) layoutParams).topMargin;
            int bottomMargin = ((MarginLayoutParams) layoutParams).bottomMargin;
            newHeightSpecSize += topMargin;
            newHeightSpecSize += bottomMargin;
        }
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        newHeightSpecSize += paddingTop;
        newHeightSpecSize += paddingBottom;

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeightSpecSize, newHeightSpecMode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setDisplayedChild(String regName) {
        if (!regMap.containsKey(regName))
            throw new IllegalStateException("name " + regName + " not exist.");
        useIndex = false;
        setDisplayedChild(regMap.get(regName));
    }

    public void setDisplayedChildWithoutAnimation(int whichChild) {
        if (whichChild == getDisplayedChild())
            return;
        if (whichChild > getChildCount() || whichChild < 0)
            throw new ArrayIndexOutOfBoundsException();
        selectIndex = whichChild;
        useIndex = true;
        hideAnimation();
        super.setDisplayedChild(whichChild);
        restoreAnimation();
    }

    public void setDisplayedChildWithoutAnimation(String regName) {
        if (!regMap.containsKey(regName))
            throw new IllegalStateException("name " + regName + " not exist.");
        useIndex = false;
        setDisplayedChildWithoutAnimation(regMap.get(regName));
    }

    public void turnNextChild() {
        if (getChildCount() == 0 || getChildCount() == 1)
            return;
        if (selectIndex < getChildCount() - 1)
            setDisplayedChild(selectIndex + 1);
        else
            setDisplayedChild(0);
    }

    public void turnPreviousChild() {
        if (getChildCount() == 0 || getChildCount() == 1)
            return;
        if (selectIndex > 0)
            setDisplayedChild(selectIndex - 1);
        else
            setDisplayedChild(getChildCount() - 1);
    }

    public void turnNextChildWithoutAnimation() {
        if (getChildCount() == 0 || getChildCount() == 1)
            return;
        if (selectIndex < getChildCount() - 1)
            setDisplayedChildWithoutAnimation(selectIndex + 1);
        else
            setDisplayedChildWithoutAnimation(0);
    }

    public void turnPreviousChildWithoutAnimation() {
        if (getChildCount() == 0 || getChildCount() == 1)
            return;
        if (selectIndex > 0)
            setDisplayedChildWithoutAnimation(selectIndex - 1);
        else
            setDisplayedChildWithoutAnimation(getChildCount() - 1);
    }

    private void setDisplayedChildIgnoreException(int whichChild) {
        if (whichChild > getChildCount() || whichChild < 0)
            return;
        selectIndex = whichChild;
        super.setDisplayedChild(whichChild);
    }

    private void setDisplayedChildIgnoreException(String regName) {
        if (!regMap.containsKey(regName))
            return;
        selectIndex = regMap.get(regName);
        super.setDisplayedChild(selectIndex);
    }

    private void hideAnimation() {
        setInAnimation(null);
        setOutAnimation(null);
    }

    private void restoreAnimation() {
        setInAnimation(inAnim);
        setOutAnimation(outAnim);
    }

    private void test(String format, Object... objs) {
        Log.d("tag", String.format(format, objs));
    }

    @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (useIndex)
            setDisplayedChildIgnoreException(selectIndex);
        else
            setDisplayedChildIgnoreException(selectName);
        if (params instanceof MultipleStateView.LayoutParams) {
            String name = ((LayoutParams) params).registerName;
            if (name == null || name.isEmpty())
                throw new NullPointerException("Every child must set name.");
            register(name, getChildCount() - 1);
        } else {
            throw new IllegalStateException("Child's LayoutParams only be instanceof MultipleStateView.LayoutParams.");
        }
    }

    public void register(String name, int select) {
        if (regMap.containsKey(name))
            throw new IllegalStateException("Duplicate names exist.");
        if (select < 0 || select > getChildCount())
            throw new ArrayIndexOutOfBoundsException("Invalid Index.");
        regMap.put(name, select);
    }

    public View getChildAt(String regName) {
        if (!regMap.containsKey(regName))
            return null;
        return getChildAt(regMap.get(regName));
    }

    @Override public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MultipleStateView.LayoutParams(getContext(), attrs);
    }

    private Animation.AnimationListener animListener = new Animation.AnimationListener() {
        @Override public void onAnimationStart(Animation animation) {
            isAnimating = true;
        }

        @Override public void onAnimationEnd(Animation animation) {
            isAnimating = false;
        }

        @Override public void onAnimationRepeat(Animation animation) {

        }
    };

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public String registerName;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.MultipleStateView_Layout);
            registerName = ta.getString(R.styleable.MultipleStateView_Layout_layout_regName);
            ta.recycle();
        }
    }

}

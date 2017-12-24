package org.arxing.multiplestateview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
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
    private Map<String, Integer> regMap = new HashMap<>();
    private int selectIndex;
    private String selectName;
    private boolean useIndex;
    private Animation inAnim, outAnim;


    public MultipleStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context, attrs);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {

        } else {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MultipleStateView);
            transDuration = ta.getInt(R.styleable.MultipleStateView_msv_transDuration, 100);
            try {
                selectIndex = ta.getInt(R.styleable.MultipleStateView_msv_select, 0);
                useIndex = true;
            } catch (Exception e) {
                selectName = ta.getString(R.styleable.MultipleStateView_msv_select);
                useIndex = false;
            }
            ta.recycle();
        }
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

    public void setDisplayedChild(String regName) {
        if (!regMap.containsKey(regName))
            throw new IllegalStateException("name " + regName + " not exist.");
        useIndex = false;
        setDisplayedChild(regMap.get(regName));
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

    private void setDisplayedChildWithoutAnimation(int whichChild){
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

    private void setDisplayedChildWithoutAnimation(String regName){
        if (!regMap.containsKey(regName))
            throw new IllegalStateException("name " + regName + " not exist.");
        useIndex = false;
        setDisplayedChildWithoutAnimation(regMap.get(regName));
    }

    private void hideAnimation(){
        setInAnimation(null);
        setOutAnimation(null);
    }

    private void restoreAnimation(){
        setInAnimation(inAnim);
        setOutAnimation(outAnim);
    }

    @Override public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if(useIndex)
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

    public View getChildAt(String regName){
        if(!regMap.containsKey(regName))
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

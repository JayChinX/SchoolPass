
package com.yuanding.schoolpass.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年4月18日 下午6:25:56 
 * 跑马灯效果
 */
public class AlwaysMarqueeTextView extends TextView {

    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}

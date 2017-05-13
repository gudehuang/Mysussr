package com.example.hzg.mysussr.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hzg.mysussr.R;


/**
 * Created by hzg on 2017/5/10.
 */

public class TabLinearLayout extends LinearLayout implements View.OnClickListener {
    private SparseArray<View> mTabs;
    private int mPosition=0;
    private TabonClickListener listener;

    public TabLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTabs = new SparseArray<>();
        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.TabLinearLayout);
        if (types != null) {
            int count = types.getInteger(R.styleable.TabLinearLayout_tabCount, 0);
            int arrayId = types.getResourceId(R.styleable.TabLinearLayout_tabTitleArrayRes, 0);

            if (arrayId != 0) {
                String[] titles = context.getResources().getStringArray(arrayId);
                for (int i = 0; i < count; i++) {
                    View view = LayoutInflater.from(context).inflate(R.layout.tablinearlayout, null);
                    TextView tabText = (TextView) view.findViewById(R.id.tab_text);
                    tabText.setText(titles[i]);
                    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1);
                    view.setLayoutParams(params);
                    view.setTag(i);
                    view.setOnClickListener(this);
                    mTabs.put(i, view);
                    addView(view);
                }
            }
        }
        types.recycle();
        setSelected(0);


    }

    public void setListener(TabonClickListener listener) {
        this.listener = listener;
    }

    public void setSelected(int position) {
        mTabs.get(mPosition).setSelected(false);
        mPosition = position;
        mTabs.get(mPosition).setSelected(true);
        if (listener!=null)
        {
            listener.onClick(position);
        }
    }

    @Override
    public void onClick(View v) {
         Log.d("111","onClick");
        int positon = 0;
        try {
            positon = (int) v.getTag();
        } catch (Exception e) {
            Log.d("",e.getMessage());
            return;
        }
         setSelected(positon);

    }

    public interface TabonClickListener {
        void onClick(int position);
    }
}

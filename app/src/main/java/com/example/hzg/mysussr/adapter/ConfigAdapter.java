package com.example.hzg.mysussr.adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.hzg.mysussr.R;
import com.example.hzg.mysussr.listener.OnViewHolderItemClickListener;

import java.util.ArrayList;

/**
 * Created by hzg on 2017/5/12.
 */

 public   class ConfigAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private ArrayList<String[]> datalist;
    private String[] data;
    private String[] header;
    private OnViewHolderItemClickListener listener;

    public ConfigAdapter(ArrayList<String[]> datalist, String[] header, int position) {
        this.datalist = datalist;
        this.header=header;
        setDatePosition(position);
    }

    public void setListener(OnViewHolderItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = null;
        switch (viewType) {
            case 0:
                root = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_viewholder, parent, false);
                break;
            case 1:
                root = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_viewholder1, parent, false);
                break;
            case 2:
                root = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_viewholder2, parent, false);
                break;
        }

        MyViewHolder holder = new MyViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (holder.getItemViewType() == 0) {
            TextView title = holder.getView(R.id.activity_main_viewholder_title);
            title.setText(header[position]);
            TextView content = holder.getView(R.id.activity_main_viewholder_content);
            content.setText(data[position % data.length]);
            if (position > 2 && position < 5) {
                content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                content.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null)
                    listener.onClick(position, data[position]);
                }
            });
        } else if (holder.getItemViewType() == 1) {
            TextView title = holder.getView(R.id.activity_main_viewholder1_title);
            title.setText(header[position]);
            RadioGroup content = holder.getView(R.id.activity_main_viewholder1_content);
            int id = -1;
            switch (data[position]) {
                case "0":
                    id = R.id.activity_main_viewholder1_content_0;
                    break;
                case "1":
                    id = R.id.activity_main_viewholder1_content_1;
                    break;
                case "2":
                    id = R.id.activity_main_viewholder1_content_2;
                    break;
            }
            content.check(id);
            content.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    switch (checkedId) {
                        case R.id.activity_main_viewholder1_content_0:
                            data[position] = "0";
                            break;
                        case R.id.activity_main_viewholder1_content_1:
                            data[position] = "1";
                            break;
                        case R.id.activity_main_viewholder1_content_2:
                            data[position] = "2";
                            break;
                    }
                }
            });
        } else {
            TextView title = holder.getView(R.id.activity_main_viewholder2_title);
            title.setText(header[position]);
            Switch content = holder.getView(R.id.activity_main_viewholder2_content);

            content.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener!=null)
                    listener.onCheckedChange(position, isChecked);
                }
            });
            content.setChecked(data[position].equals("0") ? false : true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 10)
            return 1;
        else if (position > 10)
            return 2;
        return 0;
    }

    @Override
    public int getItemCount() {
        return header.length;
    }

    public String[] getData() {
        return data;
    }

    public String[] getHeader() {
        return header;
    }

    public void setDatePosition(int position) {
        data = datalist.get(position);
        notifyDataSetChanged();
    }
}

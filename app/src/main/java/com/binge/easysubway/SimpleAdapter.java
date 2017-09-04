package com.binge.easysubway;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/9/4.
 */

public abstract class SimpleAdapter<T> extends RecyclerView.Adapter {

    private int mResourceId;
    private List<T> mDataList;
    private OnItemClickListener mOnItemClickListener;

    public SimpleAdapter(int resourceId, List<T> dataList) {
        mResourceId = resourceId;
        mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(mResourceId, parent, false)) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final T t = mDataList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(t);
                }
            }
        });

        onBindViewData(holder, t);
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T t);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public abstract void onBindViewData(RecyclerView.ViewHolder holder, T t);

    public void updateData(List<T> list) {
        if (mDataList != null) {
            mDataList.clear();
            mDataList.addAll(list);
        } else {
            mDataList = list;
        }
        notifyDataSetChanged();
    }
}

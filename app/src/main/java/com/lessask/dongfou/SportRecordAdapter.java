package com.lessask.dongfou;

/**
 * Created by JHuang on 2016/3/26.
 */

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by huangji on 2016/2/19.
 */
public class SportRecordAdapter extends BaseRecyclerAdapter<SportRecord,SportRecordAdapter.ViewHolder>{

    private Context context;
    public SportRecordAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SportRecord sportRecord = getItem(position);
        holder.name.setText(sportRecord.getName());
        if(position%2==0)
            holder.circle.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        else
            holder.circle.setBackgroundColor(context.getResources().getColor(R.color.main_color));

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView name;
        CircleView circle;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            circle = (CircleView) itemView.findViewById(R.id.circle);
        }
    }
}

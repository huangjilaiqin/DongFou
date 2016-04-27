package com.lessask.dongfou;

import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.dongfou.net.VolleyHelper;

import java.util.Iterator;

/**
 * Created by huangji on 2016/2/19.
 */
public class WeightAdapter extends BaseRecyclerAdapter<Sport,WeightAdapter.ViewHolder>{
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;
    private int[] bgColors = {R.color.purple_fab,R.color.white_bg_font};
    private int[] textColors = {R.color.white,R.color.black};
    public WeightAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void updateStatus(int sportid){
        Iterator<Sport> iterator = getList().iterator();
        while (iterator.hasNext()){
            Sport sport = iterator.next();
            if(sport.getId()==sportid)
                sport.setStatus(1);
            else
                sport.setStatus(0);
        }
    }

    public void updateStatusByPosition(int pos){
        Iterator<Sport> iterator = getList().iterator();
        while (iterator.hasNext()){
            Sport sport = iterator.next();
            sport.setStatus(0);
        }
        getList().get(pos).setStatus(1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Sport sport = getItem(position);
        holder.img_selected.setText(sport.getName());
        holder.img.setText(sport.getName());

        if(sport.getStatus()==1) {
            holder.img_selected.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.INVISIBLE);
        }else {
            holder.img_selected.setVisibility(View.INVISIBLE);
            holder.img.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemLongClickListener != null) {
                    Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(10);
                    onItemLongClickListener.onItemLongClick(view, position);
                }
                return false;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        CircleTextView img_selected;
        CircleTextView img;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            //img = (ImageView) itemView.findViewById(R.id.img);
            img_selected=(CircleTextView)itemView.findViewById(R.id.img_selected);
            img_selected.setBackgroundColor(context.getResources().getColor(bgColors[0]));
            img_selected.setTextColor(context.getResources().getColor(textColors[0]));
            img=(CircleTextView)itemView.findViewById(R.id.img);
            img.setBackgroundColor(context.getResources().getColor(bgColors[1]));
            img.setTextColor(context.getResources().getColor(textColors[1]));
        }
    }
}

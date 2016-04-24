package com.lessask.dongfou;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.dongfou.net.VolleyHelper;

/**
 * Created by huangji on 2016/2/19.
 */
public class WeightAdapter extends BaseRecyclerAdapter<Sport,WeightAdapter.ViewHolder>{
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;
    public WeightAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weight_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Sport sport = getItem(position);
        String headImgUrl = Config.imagePrefix+sport.getImage();
        ImageLoader.ImageListener headImgListener = ImageLoader.getImageListener(holder.img, R.drawable.dongfou, R.drawable.dongfou);
        VolleyHelper.getInstance().getImageLoader().get(headImgUrl, headImgListener, 100, 100);

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

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }
}

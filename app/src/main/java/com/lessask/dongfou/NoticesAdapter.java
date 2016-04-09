package com.lessask.dongfou;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.lessask.dongfou.model.Notice;
import com.lessask.dongfou.net.VolleyHelper;
import com.lessask.dongfou.util.TimeHelper;

/**
 * Created by huangji on 2016/2/19.
 */
public class NoticesAdapter extends BaseRecyclerAdapter<Notice,NoticesAdapter.ViewHolder>{
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private Context context;
    public NoticesAdapter(Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Notice notice = getItem(position);
        holder.title.setText(notice.getTitle());
        holder.time.setText(TimeHelper.date2Chat(notice.getTime()));

        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"url:"+notice.getUrl(),Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse(notice.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        TextView title;
        TextView time;
        Button action;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            time =(TextView)itemView.findViewById(R.id.time);
            action = (Button)itemView.findViewById(R.id.action);
        }
    }
}

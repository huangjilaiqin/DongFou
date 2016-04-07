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

import com.lessask.dongfou.util.TimeHelper;

import java.util.Map;


/**
 * Created by huangji on 2016/2/19.
 */
public class SportRecordAdapter extends BaseRecyclerAdapter<SportRecord,SportRecordAdapter.ViewHolder>{

    private Context context;
    private Map<Integer, Sport> sportMap;
    private OnItemLongClickListener onItemLongClickListener;
    public SportRecordAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_record, parent, false);
        return new ViewHolder(view);
    }

    public void setSportMap(Map<Integer, Sport> sportMap) {
        this.sportMap = sportMap;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SportRecord sportRecord = getItem(position);
        //Sport sport = sportMap.get(sportRecord.getSportid());
        Sport sport = DbDataHelper.loadSportFromDb(context, sportRecord.getSportid());
        holder.name.setText(sport.getName());
        StringBuilder detail = new StringBuilder();
        int kind = sport.getKind();
        if(kind==1){
            String unit = sport.getUnit();
            if(unit.equals("次"))
                detail.append((int)sportRecord.getArg1());
            else
                detail.append(sportRecord.getArg1());
            detail.append(unit);
        }else if(kind==2){
            String unit = sport.getUnit();
            if(unit.equals("次"))
                detail.append((int)sportRecord.getArg1());
            else
                detail.append(sportRecord.getArg1());
            detail.append(unit);
            detail.append(" ");
            detail.append(sportRecord.getArg2());
            detail.append(sport.getUnit2());
            detail.append("/");
            detail.append(sport.getUnit());
        }
        holder.detail.setText(detail.toString());
        holder.time.setText(TimeHelper.date2Chat(sportRecord.getTime()));
        holder.circle.setBackgroundColor(context.getResources().getColor(R.color.colorAccent1));

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
        TextView name;
        TextView detail;
        TextView time;
        CircleView circle;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            detail=(TextView) itemView.findViewById(R.id.detail);
            time=(TextView)itemView.findViewById(R.id.time);
            circle = (CircleView) itemView.findViewById(R.id.circle);
        }
    }
}

package com.service.iscon.vcr.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.service.iscon.vcr.Model.SessionModel;
import com.service.iscon.vcr.R;

import java.util.List;

/**
 * Created by priyanka on 25-03-2017.
 */

public class SessionHistoryAdapter  extends RecyclerView.Adapter<SessionHistoryAdapter.MyViewHolder> {

    private List<SessionModel> SessionList;
    private Context mContext;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, round, beads;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            //icon = (ImageView) view.findViewById(R.id.iv_icon);
            date = (TextView) view.findViewById(R.id.tv_date);
            round = (TextView) view.findViewById(R.id.tv_round);
            beads = (TextView) view.findViewById(R.id.tv_beads);
        }
    }


    public SessionHistoryAdapter(Context con, List<SessionModel> sList) {
        this.SessionList = sList;
        this.mContext= con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_session_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SessionModel sessionModel = SessionList.get(position);
        //holder.date.setText(""+sessionModel.getDateInIST());
        holder.date.setText(""+sessionModel.getDisplayDate());
        // holder.title.setText(""+sessionModel.getDateInISTFormat2());
//        holder.genre.setText(sessionModel.getStartTimeInIST());
        holder.beads.setText("Beads: "+String.valueOf(sessionModel.getBeads()));
        int r=sessionModel.getBeads()/108;
        if(r>0) {
            holder.round.setVisibility(View.VISIBLE);
            holder.round.setText("Rounds: " + String.valueOf(r));
        }else {
            holder.round.setVisibility(View.GONE);
        }
  /*      switch(position % 4){
            case 0:holder.icon.setColorFilter(ContextCompat.getColor(mContext,R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 1:holder.icon.setColorFilter(ContextCompat.getColor(mContext,R.color.colorGray), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 2: holder.icon.setColorFilter(ContextCompat.getColor(mContext,R.color.colorGreen), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 3:  holder.icon.setColorFilter(ContextCompat.getColor(mContext,R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
                break;
            case 4: break;
            case 5: break;
            case 6: break;
            case 7: break;
        }*/

    }

    @Override
    public int getItemCount() {
        return SessionList.size();
    }
}
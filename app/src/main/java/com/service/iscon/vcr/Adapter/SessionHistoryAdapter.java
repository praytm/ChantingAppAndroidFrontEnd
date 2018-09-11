package com.service.iscon.vcr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.service.iscon.vcr.model.SessionModel;
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
        holder.date.setText(""+sessionModel.getDateInIST());
        holder.beads.setText("Beads: "+String.valueOf(sessionModel.getBeads()));
        holder.round.setText("Rounds: "+String.valueOf(sessionModel.getBeads()/108));


    }

    @Override
    public int getItemCount() {
        return SessionList.size();
    }
}
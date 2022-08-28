package com.example.kyubi.ui.calendario;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyubi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder>{

    Context context;
    ArrayList<com.example.kyubi.ui.calendario.Events> arrayList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public EventRecyclerAdapter(Context context, ArrayList<com.example.kyubi.ui.calendario.Events> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_rowlayout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final com.example.kyubi.ui.calendario.Events events = arrayList.get(position);
        holder.Event.setText(events.getEVENT());
        holder.DateTxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());
        if(user != null && (user.getEmail().equals("a14xensargim@inspedralbes.cat") ||
                user.getEmail().equals("xeniaippon@hotmail.es"))){
            holder.delete.setVisibility(View.VISIBLE);
        }else{
            holder.delete.setVisibility(View.INVISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.kyubi.ui.calendario.DBOpenHelper dbOpenHelper = new com.example.kyubi.ui.calendario.DBOpenHelper();

                System.out.println("HOLAAAAAAAAAAAAAAAAAAAAAAAAAAa");

                dbOpenHelper.DeleteEvent(events.getEVENT(), events.getDATE(), events.getTIME(), this::onEventsDeleted);
                arrayList.remove(events);
            }

            private void onEventsDeleted() {
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView DateTxt, Event, Time;
        Button delete;

        public MyViewHolder(@NonNull View itemViem){
            super(itemViem);
            DateTxt = itemViem.findViewById(R.id.eventdate);
            Event = itemViem.findViewById(R.id.eventname);
            Time = itemViem.findViewById(R.id.eventtime);
            delete = itemViem.findViewById(R.id.delete);
        }
    }
}

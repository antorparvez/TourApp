package com.example.mahmud.travelmate.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mahmud.travelmate.EventFragment;
import com.example.mahmud.travelmate.Interface.ListItemClickListener;
import com.example.mahmud.travelmate.POJO.Event;
import com.example.mahmud.travelmate.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.EventVH>{
    private Context context;
    private ArrayList<Event> events;
    //private EventDeleteListener menuOperation;
    //private MenuEditOperation menuEditOperation;
    private ListItemClickListener listItemClickListener;

    public RvAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;
        //menuOperation = fragment;
        //menuEditOperation = (MenuEditOperation) context;
        listItemClickListener = (ListItemClickListener) context;

    }

    @NonNull
    @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_event_layout,viewGroup,false);
        return new EventVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventVH eventVH, final int i) {
        long daysLeft = 0;
        eventVH.eventName.setText(events.get(i).getEventName());
        eventVH.eventDepartureDate.setText("Starts : "+events.get(i).getDepartureDate());
        eventVH.eventCreationDate.setText("Created : "+events.get(i).getCurrnetDate());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date departureDate = format.parse(events.get(i).getDepartureDate());
            Date newDate = new Date();
            long dateDiff = departureDate.getTime() - newDate.getTime();
            daysLeft = TimeUnit.DAYS.convert(dateDiff,TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (daysLeft < 0){
            eventVH.eventTimeLeft.setText("Completed");
        } else {
            String displayDaysLeft = String.valueOf(daysLeft)+" Days Left";
            eventVH.eventTimeLeft.setText(displayDaysLeft);
        }

        /*eventVH.eventMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context,v);
                menu.getMenuInflater().inflate(R.menu.menu_layout,menu.getMenu());
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_delete){
                            menuOperation.onDeleteOperation(events.get(i).getId());
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                        if (item.getItemId() == R.id.menu_edit){
                            menuEditOperation.onEditOperation(events.get(i));
                        }
                        return false;
                    }
                });
            }
        });*/
        eventVH.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemClickListener.onListItemClickListener(events.get(i));
                //Toast.makeText(context, "Position "+i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
    public void updateRV(ArrayList<Event> events){
        this.events.clear();
        this.events = events;
        notifyDataSetChanged();
    }
    class EventVH extends RecyclerView.ViewHolder{
        private TextView eventName,eventCreationDate,eventDepartureDate,eventTimeLeft;
        private TextView eventMenu;
        public EventVH(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name_single);
            eventCreationDate = itemView.findViewById(R.id.event_creation_date_single);
            eventDepartureDate = itemView.findViewById(R.id.event_departure_date_single);
            eventTimeLeft = itemView.findViewById(R.id.event_time_left_single);
            //eventMenu = itemView.findViewById(R.id.menu_single);
        }
    }
}

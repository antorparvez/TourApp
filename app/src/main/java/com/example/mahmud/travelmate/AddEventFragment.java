package com.example.mahmud.travelmate;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mahmud.travelmate.Interface.BackToEventFromAEF;
import com.example.mahmud.travelmate.Interface.EventAddedAction;
import com.example.mahmud.travelmate.POJO.Event;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEventFragment extends Fragment {
    private static final int REQUEST_AUTO_PLACEMENT_CODE = 10;
    private EditText mEventNameInputET,mEventStartLocInputET,mEventDestinationInputET,
            mEventBudgetInputET;
    private Button mCreateEventBT,mUpdateEventBT;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mUserEventRef;
    private EventAddedAction eventAddedAction;
    private Button eventDepartureDateBT;
    private BackToEventFromAEF backAction;

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,dayOfMonth);
            String date = sdf.format(calendar.getTime());
            eventDepartureDateBT.setText(date);
        }
    };


    public AddEventFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        eventAddedAction = (EventAddedAction) context;
        backAction = (BackToEventFromAEF) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        mEventNameInputET = v.findViewById(R.id.event_name_in_aef);
        mEventStartLocInputET = v.findViewById(R.id.event_start_location_in_aef);
        mEventDestinationInputET = v.findViewById(R.id.event_destination_in_aef);
        mEventBudgetInputET = v.findViewById(R.id.event_budget_in_aef);
        mCreateEventBT = v.findViewById(R.id.create_event_btn_aef);
        eventDepartureDateBT = v.findViewById(R.id.event_departure_date_in_bt_aef);
        mUpdateEventBT = v.findViewById(R.id.update_event_btn_aef);


        mEventDestinationInputET.setFocusable(true);
        mEventDestinationInputET.setClickable(true);
//        mEventDestinationInputET.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = null;
//                try {
//                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
//                    startActivityForResult(intent,REQUEST_AUTO_PLACEMENT_CODE);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//            }
//        });




        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("addressline")){
            String addressline = bundle.getString("addressline","");
            mEventStartLocInputET.setText(addressline);
        }


        eventDepartureDateBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog  dialog = new DatePickerDialog(context,dateSetListener,year,month,dayOfMonth);
                dialog.show();
            }
        });

        Bundle argument = getArguments();
        if (argument != null && argument.containsKey("Event")){
            final Event event = (Event) getArguments().getSerializable("Event");
            mEventNameInputET.setText(event.getEventName());
            mEventStartLocInputET.setText(event.getStartLocation());
            mEventDestinationInputET.setText(event.getDestination());
            mEventBudgetInputET.setText(String.valueOf(event.getBudget()));
            eventDepartureDateBT.setText(event.getDepartureDate());
            mUpdateEventBT.setVisibility(View.VISIBLE);
            mCreateEventBT.setVisibility(View.GONE);

            mUpdateEventBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String eventName,eventStartLoc,eventDestination,eventDepartureDate,eventBudget;
                    eventName = mEventNameInputET.getText().toString();
                    eventStartLoc = mEventStartLocInputET.getText().toString();
                    eventDestination = mEventDestinationInputET.getText().toString();
                    eventDepartureDate = eventDepartureDateBT.getText().toString();
                    eventBudget = mEventBudgetInputET.getText().toString();
                    if (eventName.isEmpty()){
                        Toast.makeText(context, "Plz give the event a name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (eventStartLoc.isEmpty()) eventStartLoc = "";
                    if (eventDestination.isEmpty()) eventDestination = "";
                    if (eventDepartureDate.isEmpty()) eventDepartureDate = "";
                    if (eventBudget.isEmpty()) eventBudget = "0";



                    mUserEventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(mUser.getUid());
                    mUserEventRef.child(event.getId()).removeValue();
                    Event eventEdited = new Event(event.getId(),eventName,eventStartLoc,eventDestination,eventDepartureDate,
                            Double.parseDouble(eventBudget),event.getCurrnetDate());
                    mUserEventRef.child(event.getId()).setValue(eventEdited);
                    Toast.makeText(context, "Event Updated", Toast.LENGTH_SHORT).show();
                    eventAddedAction.onEventAdded();

                }
            });

            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                        backAction.backAction();
                    }
                    return false;
                }
            });


            return v;
        }

        mCreateEventBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName,eventStartLoc,eventDestination,eventDepartureDate,eventBudget;
                eventName = mEventNameInputET.getText().toString();
                eventStartLoc = mEventStartLocInputET.getText().toString();
                eventDestination = mEventDestinationInputET.getText().toString();
                eventDepartureDate = eventDepartureDateBT.getText().toString();
                eventBudget = mEventBudgetInputET.getText().toString();
                if (eventName.isEmpty()){
                    Toast.makeText(context, "Plz give the event a name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (eventStartLoc.isEmpty()) eventStartLoc = "";
                if (eventDestination.isEmpty()) eventDestination = "";
                if (eventDepartureDate.isEmpty()) eventDepartureDate = "";
                if (eventBudget.isEmpty()) eventBudget = "0";

                
                
                mUserEventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(mUser.getUid());
                String eventKeyId = mUserEventRef.push().getKey();
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                Event event = new Event(eventKeyId,eventName,eventStartLoc,eventDestination,eventDepartureDate,
                        Double.parseDouble(eventBudget),date);
                mUserEventRef.child(eventKeyId).setValue(event);
                Toast.makeText(context, "Event Added", Toast.LENGTH_SHORT).show();
                eventAddedAction.onEventAdded();


            }
        });


        /*v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                    backAction.backAction();
                }
                return false;
            }
        });*/

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AUTO_PLACEMENT_CODE && resultCode == Activity.RESULT_OK){
            Place place = PlaceAutocomplete.getPlace(getContext(),data);
            mEventDestinationInputET.setText(place.getName());
        }
    }
}

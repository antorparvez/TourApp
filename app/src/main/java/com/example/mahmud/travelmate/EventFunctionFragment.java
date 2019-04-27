package com.example.mahmud.travelmate;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmud.travelmate.Adapter.ExpandableListViewCustomAdapter;
import com.example.mahmud.travelmate.Adapter.ViewExpenseRvAdapter;
import com.example.mahmud.travelmate.Interface.EventAddedAction;
import com.example.mahmud.travelmate.Interface.ImageFunctionListener;
import com.example.mahmud.travelmate.Interface.MenuEditOperation;
import com.example.mahmud.travelmate.POJO.Event;
import com.example.mahmud.travelmate.POJO.EventBudget;
import com.example.mahmud.travelmate.POJO.Expense;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EventFunctionFragment extends Fragment {
    private ArrayList<String> listHeaders;
    private HashMap<String, ArrayList<String>> listAllChilds;
    private ExpandableListView expandableListView;
    private ExpandableListViewCustomAdapter customAdapter;
    private Context context;
    private TextView mEventNameTV,mEventBudgetTV,mEventBudgetUsedStatusTV,mEventBudgetTotalStatusTV;
    private ProgressBar mBudgetProgressBar;
    private DatabaseReference mExpenseUserEventRef;
    private DatabaseReference mEventUserRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private double usedBudget = 0;
    private double eventBudget;
    private String mCurrentEventId;
    private MenuEditOperation editOperation;
    private EventAddedAction addOrDeleteAction;
    private DatabaseReference mExpenseUserRef;
    private Event event = new Event();
    private ImageFunctionListener imageFunctionListener;
    private FloatingActionButton mAddExpenseFAB;
    public EventFunctionFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        editOperation = (MenuEditOperation) context;
        addOrDeleteAction = (EventAddedAction) context;
        imageFunctionListener = (ImageFunctionListener) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_function, container, false);
        expandableListView = v.findViewById(R.id.exp_list_view);
        mEventNameTV = v.findViewById(R.id.event_name_eff);
        mEventBudgetTV = v.findViewById(R.id.event_budget_info_eff);
        mEventBudgetUsedStatusTV = v.findViewById(R.id.budget_used_status_tv_eff);
        mEventBudgetTotalStatusTV = v.findViewById(R.id.budget_total_status_tv_eff);
        mBudgetProgressBar = v.findViewById(R.id.budget_progress_bar);
        mExpenseUserRef = FirebaseDatabase.getInstance().getReference().child("Expenses").child(mUser.getUid());
        mAddExpenseFAB = v.findViewById(R.id.add_expense_fab_eff);


        createExpandableList();

        Bundle argument = getArguments();
        if (argument != null && argument.containsKey("Event")){
            event = (Event) getArguments().getSerializable("Event");
            mCurrentEventId = event.getId();
            eventBudget = event.getBudget();
            mEventNameTV.setText(event.getEventName());
            mEventUserRef = FirebaseDatabase.getInstance().getReference().child("Events").
                    child(mUser.getUid());
            if (mUser == null){
                Toast.makeText(context, "User Null", Toast.LENGTH_SHORT).show();
            }
            mExpenseUserEventRef = FirebaseDatabase.getInstance().getReference().child("Expenses").
                    child(mUser.getUid()).child(event.getId());



            mExpenseUserEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()){
                        Expense expense = new Expense();
                        expense = d.getValue(Expense.class);
                        usedBudget = usedBudget + expense.getCost();
                        //Log.e("------------","Cost is inner"+usedBudget);
                    }
                    mEventBudgetTV.setText("Budget Status ("+usedBudget+"/"+event.getBudget()+")");
                    if (eventBudget!=0){
                        double part = usedBudget/eventBudget;
                        int percentage = (int) (part*100);
                        mEventBudgetUsedStatusTV.setText(""+percentage+"%");
                        mBudgetProgressBar.setProgress(percentage);
                    }
                    else {
                        mEventBudgetTV.setText("No Budget Set");
                        mBudgetProgressBar.setProgress(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //Log.e("------------","Cost is outer "+usedBudget);
            //mEventBudgetTV.setText("Budget Status ("+usedBudget+"/"+event.getBudget()+")");



        }




        //mEventNameTV.setText("");
        customAdapter = new ExpandableListViewCustomAdapter(context,listHeaders,listAllChilds);
        expandableListView.setAdapter(customAdapter);


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == 0 && childPosition == 0){
                    addExpense();
                    /*AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                    dlg.setTitle("Input New Expense");
                    final View view = LayoutInflater.from(context).inflate(R.layout.new_expense_dialogue_layout,null);
                    dlg.setView(view);
                    dlg.setNegativeButton("cancel",null)
                       .setPositiveButton("save", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText inputExpenseName = view.findViewById(R.id.input_expense_name_dlg);
                            EditText inputExpense = view.findViewById(R.id.input_expense_dlg);
                            String expenseName,expense;
                            expenseName = inputExpenseName.getText().toString();
                            expense = inputExpense.getText().toString();
                            if (expenseName.isEmpty() && expense.isEmpty()){
                                return;
                            }
                            if (expenseName.isEmpty()){expenseName = "-----";}
                            if (expense.isEmpty()){expense = "0";}


                            double resultedBudget = usedBudget + Double.parseDouble(expense);
                            if (resultedBudget > eventBudget){
                                Toast.makeText(context, "Your expenses have crossed your budget", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            String expenseKeyId = mExpenseUserEventRef.push().getKey();
                            Expense expense1 = new Expense(expenseKeyId,expenseName,Double.parseDouble(expense));
                            mExpenseUserEventRef.child(expenseKeyId).setValue(expense1);




                            usedBudget = usedBudget + Double.parseDouble(expense);
                            mEventBudgetTV.setText("Budget Status ("+usedBudget+"/"+eventBudget+")");

                            if (eventBudget!=0){
                                double part = usedBudget/eventBudget;
                                int percentage = (int) (part*100);
                                mEventBudgetUsedStatusTV.setText(""+percentage+"%");
                                mBudgetProgressBar.setProgress(percentage);
                            }
                            else {
                                mEventBudgetTV.setText("No Budget Set");
                                mBudgetProgressBar.setProgress(0);
                            }
                            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dlg.show();
                    Toast.makeText(context, "You Clicked for Add expense", Toast.LENGTH_SHORT).show();*/
                }




                if (groupPosition == 0 && childPosition == 1){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                    dlg.setTitle("View All Expenses");
                    View view = LayoutInflater.from(context).inflate(R.layout.view_all_expenses_dlg_layout,null);
                    final RecyclerView recyclerView = view.findViewById(R.id.expenses_rv_dlg);

                    mExpenseUserEventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        ArrayList<Expense> expenses = new ArrayList<>();
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            expenses.clear();
                            for (DataSnapshot d: dataSnapshot.getChildren()){
                                Expense expense = new Expense();
                                expense = d.getValue(Expense.class);
                                expenses.add(expense);
                            }
                            //Log.e("--------------","Size of List is "+expenses.size());
                            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            ViewExpenseRvAdapter rvAdapter = new ViewExpenseRvAdapter(context,expenses);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(rvAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    dlg.setView(view);
                    dlg.show();

                }



                if (groupPosition == 0 && childPosition == 2){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                    View view = LayoutInflater.from(context).inflate(R.layout.add_budget_dlg_layout,null);
                    final EditText addBudgetET = view.findViewById(R.id.add_budgt_dlg);
                    dlg.setView(view);
                    dlg.setNegativeButton("Cancel",null).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String addBudget = addBudgetET.getText().toString();
                            if (addBudget.isEmpty()){
                                Toast.makeText(context, "Nothing Changed", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            eventBudget = eventBudget+Double.parseDouble(addBudget);
                            double part = usedBudget/eventBudget;
                            int percentage = (int) (part*100);
                            //EventBudget eventBudgetClass = new EventBudget(eventBudget);

                            event.setBudget(eventBudget);
                            mEventUserRef.child(mCurrentEventId).removeValue();


                            mEventUserRef.child(mCurrentEventId).setValue(event);

                            mEventBudgetUsedStatusTV.setText(""+percentage+"%");
                            mBudgetProgressBar.setProgress(percentage);
                            mEventBudgetTV.setText("Budget Status ("+usedBudget+"/"+eventBudget+")");
                            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();

                            /*Map<String , Double> budget = new HashMap<>();
                            budget.put("Budget", eventBudget);
                            mExpenseUserEventRef.child(mCurrentEventId).updateChildren(budget);*/

                        }
                    });
                    dlg.show();
                }
                if (groupPosition == 2&& childPosition == 0){
                    editOperation.onEditOperation(event);
                }
                if (groupPosition == 2 && childPosition == 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Are you sure to delete?");
                    builder.setNegativeButton("No",null);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mExpenseUserRef.child(event.getId()).removeValue();
                            mEventUserRef.child(event.getId()).removeValue();
                            addOrDeleteAction.onEventAdded();
                        }
                    });
                    builder.show();

                }
                //-----------------------------------------------------------------------------------------------------
                if (groupPosition == 1 && childPosition == 0){
                    imageFunctionListener.onTakeSnapshot(mCurrentEventId);
                }
                if (groupPosition == 1 && childPosition == 1){
                    imageFunctionListener.onGalleryView(mCurrentEventId);
                }


                return false;
            }
        });

        mAddExpenseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });
        return v;
    }

    private void addExpense() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg.setTitle("Input New Expense");
        final View view = LayoutInflater.from(context).inflate(R.layout.new_expense_dialogue_layout,null);
        dlg.setView(view);
        dlg.setNegativeButton("cancel",null)
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText inputExpenseName = view.findViewById(R.id.input_expense_name_dlg);
                        EditText inputExpense = view.findViewById(R.id.input_expense_dlg);
                        String expenseName,expense;
                        expenseName = inputExpenseName.getText().toString();
                        expense = inputExpense.getText().toString();
                        if (expenseName.isEmpty() && expense.isEmpty()){
                            return;
                        }
                        if (expenseName.isEmpty()){expenseName = "-----";}
                        if (expense.isEmpty()){expense = "0";}


                        double resultedBudget = usedBudget + Double.parseDouble(expense);
                        if (resultedBudget > eventBudget){
                            Toast.makeText(context, "Your expenses have crossed your budget", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        String expenseKeyId = mExpenseUserEventRef.push().getKey();
                        Expense expense1 = new Expense(expenseKeyId,expenseName,Double.parseDouble(expense));
                        mExpenseUserEventRef.child(expenseKeyId).setValue(expense1);




                        usedBudget = usedBudget + Double.parseDouble(expense);
                        mEventBudgetTV.setText("Budget Status ("+usedBudget+"/"+eventBudget+")");

                        if (eventBudget!=0){
                            double part = usedBudget/eventBudget;
                            int percentage = (int) (part*100);
                            mEventBudgetUsedStatusTV.setText(""+percentage+"%");
                            mBudgetProgressBar.setProgress(percentage);
                        }
                        else {
                            mEventBudgetTV.setText("No Budget Set");
                            mBudgetProgressBar.setProgress(0);
                        }
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                    }
                });
        dlg.show();
        Toast.makeText(context, "You Clicked for Add expense", Toast.LENGTH_SHORT).show();
    }

    private void createExpandableList() {
        listHeaders = new ArrayList<>();
        listAllChilds = new HashMap<>();

        listHeaders.add("Expenditure");
        listHeaders.add("Moments");
        //listHeaders.add("More on Event");

        ArrayList<String> expenditure = new ArrayList<>();
        expenditure.add("Add New Expense");
        expenditure.add("View All Expense");
        expenditure.add("Add More Budget");

        ArrayList<String> moments = new ArrayList<>();
        moments.add("Take a Photo");
        moments.add("View Gallery");
        moments.add("View All Moments");

       /* ArrayList<String> moreOnEvent = new ArrayList<>();
        moreOnEvent.add("Edit Event");
        moreOnEvent.add("Delete Event");
*/
        listAllChilds.put(listHeaders.get(0),expenditure);
        listAllChilds.put(listHeaders.get(1),moments);
        //listAllChilds.put(listHeaders.get(2),moreOnEvent);
    }

}

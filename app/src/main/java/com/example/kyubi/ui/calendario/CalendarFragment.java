package com.example.kyubi.ui.calendario;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyubi.R;
import com.example.kyubi.databinding.FragmentCalendarioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class CalendarFragment extends Fragment {

    private com.example.kyubi.ui.calendario.CalendarViewModel calendarViewModel;
    private FragmentCalendarioBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = new CustomCalendarView(this.getContext(), container);

        binding = FragmentCalendarioBinding.inflate(inflater, container, false);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class CustomCalendarView extends LinearLayout {
        ImageButton nextBtn, previousBtn;
        TextView currentDate;
        GridView gridView;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ProgressBar progressBar;

        Calendar monthCalendar;

        private static final int MAX_CALENDAR_DAYS = 42;
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        Context context;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        SimpleDateFormat eventDateFormate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        com.example.kyubi.ui.calendario.MyGridAdapter myGridAdapter;
        AlertDialog alertDialog;
        List<Date> dates = new ArrayList<>();
        ArrayList<com.example.kyubi.ui.calendario.Events> eventsList = new ArrayList<>();

        public CustomCalendarView(Context context, ViewGroup container){
            super(context);


            this.context = context;
            InitializeLayout(container);
            SetUpCalendar();

            previousBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    calendar.add(Calendar.MONTH, -1);
                    SetUpCalendar();
                }
            });

            nextBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    calendar.add(Calendar.MONTH, 1);
                    SetUpCalendar();
                }
            });

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(user != null && (user.getEmail().equals("a14xensargim@inspedralbes.cat") ||
                            user.getEmail().equals("xeniaippon@hotmail.es"))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(true);
                        final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout, null);
                        final EditText EventName = addView.findViewById(R.id.eventname);
                        final TextView EventTime = addView.findViewById(R.id.eventtime);
                        ImageButton SetTime = addView.findViewById(R.id.seteventtime);
                        Button AddEvent = addView.findViewById(R.id.addevent);
                        SetTime.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Calendar calendar = Calendar.getInstance();
                                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                                int minuts = calendar.get(Calendar.MINUTE);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext()
                                        , R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        c.set(Calendar.MINUTE, minute);
                                        c.setTimeZone(TimeZone.getDefault());
                                        SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.ENGLISH);
                                        String event_Time = hformate.format(c.getTime());
                                        EventTime.setText(event_Time);
                                    }
                                }, hours, minuts, false);
                                timePickerDialog.show();
                            }
                        });

                        final String date = eventDateFormate.format(dates.get(position));
                        final String month = monthFormat.format(dates.get(position));
                        final String year = yearFormat.format(dates.get(position));

                        AddEvent.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SaveEvent(EventName.getText().toString(), EventTime.getText().toString(), date, month, year);
                                SetUpCalendar();
                                alertDialog.dismiss();
                            }
                        });

                        builder.setView(addView);
                        alertDialog = builder.create();
                        alertDialog.show();
                    }else{
                        Toast.makeText(getActivity(),"Debes ser administrador para crear un evento.",
                                Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(),"Si quieres ver un evento, debe ser un toque más largo.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String date = eventDateFormate.format(dates.get(position));

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout, null);

                    RecyclerView recyclerView = showView.findViewById(R.id.EventsRV);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                    recyclerView .setLayoutManager(layoutManager);
                    recyclerView.setHasFixedSize(true);

                    com.example.kyubi.ui.calendario.EventRecyclerAdapter eventRecyclerAdapter = new com.example.kyubi.ui.calendario.EventRecyclerAdapter(showView.getContext(),
                            CollectEventByDate(date));
                    recyclerView.setAdapter(eventRecyclerAdapter);
                    eventRecyclerAdapter.notifyDataSetChanged();

                    builder.setView(showView);
                    alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            SetUpCalendar();
                        }
                    });

                    return true;
                }
            });
        }

        private ArrayList<com.example.kyubi.ui.calendario.Events> CollectEventByDate(String date){
            ArrayList<com.example.kyubi.ui.calendario.Events> arrayList = new ArrayList<>();
            for(com.example.kyubi.ui.calendario.Events i : eventsList){
                if(i.getDATE().equals(date)){
                    arrayList.add(i);
                }
            }

            return arrayList;
        }

        private void SaveEvent(String event, String time, String date, String month, String year){
            com.example.kyubi.ui.calendario.DBOpenHelper dbOpenHelper = new com.example.kyubi.ui.calendario.DBOpenHelper();
            progressBar.setVisibility(View.VISIBLE);
            dbOpenHelper.saveEvent(event, time, date, month, year);

            Toast.makeText(context, "Event Saved", Toast.LENGTH_SHORT).show();
        }

        public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
            super(context, attrs, defStyleAttr);
        }

        private void InitializeLayout(ViewGroup container){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_calendario, this);
            nextBtn = view.findViewById(R.id.nextBtn);
            previousBtn = view.findViewById(R.id.previousBtn);
            currentDate = view.findViewById(R.id.current_date);
            gridView = view.findViewById(R.id.gridView);
            progressBar = findViewById(R.id.progressBar2);

            System.out.println("INICIALIZAR");
        }

        private void SetUpCalendar(){
            String currwntDate = dateFormat.format(calendar.getTime());
            currentDate.setText(currwntDate);
            dates.clear();
            monthCalendar = (Calendar) calendar.clone();
            monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
            int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) -1;
            monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);
            System.out.println("ABANS");
            CollectEventsPerMonth(monthFormat.format(calendar.getTime()),
                    yearFormat.format(calendar.getTime()));
            System.out.println("DESPRES");
        }

        private void CollectEventsPerMonth(String month, String year){
            com.example.kyubi.ui.calendario.DBOpenHelper dbOpenHelper = new com.example.kyubi.ui.calendario.DBOpenHelper();

            dbOpenHelper.ReadEventsPerMonth(month, year, this::onEventsReceived);
        }


        private void onEventsReceived(List<com.example.kyubi.ui.calendario.Events> events) {
            eventsList.clear();
            eventsList.addAll(events);
            while(dates.size() < MAX_CALENDAR_DAYS){
                dates.add(monthCalendar.getTime());
                monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            myGridAdapter = new com.example.kyubi.ui.calendario.MyGridAdapter(context, dates, calendar, eventsList);
            gridView.setAdapter(myGridAdapter);

            progressBar.setVisibility(View.GONE);
        }
    }

}
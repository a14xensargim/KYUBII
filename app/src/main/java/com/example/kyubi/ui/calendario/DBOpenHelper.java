package com.example.kyubi.ui.calendario;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBOpenHelper{

    public void saveEvent(String event, String time, String date, String month, String year){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> evento = new HashMap<>();

        evento.put("event", event);
        evento.put("time", time);
        evento.put("date", date);
        evento.put("month", month);
        evento.put("year", year);

        System.out.println("GUARDAAAAAAAAAAA");

        db.collection("events").document(String.valueOf(date + time + event)).set(evento);
    }

    public String ReadEvents(String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").document(date).get();
        Task<DocumentSnapshot> task = null;
        DocumentSnapshot document = task.getResult();

        return document.get("event").toString();
    }

    public void ReadEventsPerMonth(String month, String year, EventsReceivedListener listener){
        List<Events> events = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .whereEqualTo("month", month).whereEqualTo("year", year)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Events eventos = new Events(document.getString("event"),
                                        document.getString("time"),
                                        document.getString("date"),
                                        document.getString("month"),
                                        document.getString("year"));

                                events.add(eventos);
                            }

                            listener.onEventsReceived(events);
                        } else {
                            System.out.println("ELSEEEEEEEEEEEEEEEe");
                        }
                    }
                });

        System.out.println(events.size() + "return");
    }

    public void DeleteEvent(String event, String date, String time, EventsDeleteListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    System.out.println("HOLAHOLAADELETE");
                    if (!queryDocumentSnapshots.isEmpty()) {
                        System.out.println("SIZE: " + queryDocumentSnapshots.size());
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            Events eventRecuperat = document.toObject(Events.class);

                            System.out.println("DATE: " + eventRecuperat.getDATE() + " -----> " + date);
                            System.out.println("TIME: " + eventRecuperat.getTIME() + " -----> " + time);
                            System.out.println("EVENT " + eventRecuperat.getEVENT() + " -----> " + event);


                            if (eventRecuperat.getDATE().equals(date) &&
                                    eventRecuperat.getTIME().equals(time) &&
                                    eventRecuperat.getEVENT().equals(event)) {
                                System.out.println("DOCUMENT ID: " + document.getId());

                                db.collection("events").document(document.getId()).delete();

                            }

                        }

                        listener.onEventsDeleted();
                    } else {
                        System.out.println("NO S'HAN RECUPERAT DOCUMENTS");
                    }
                });
    }

    public interface EventsDeleteListener{
        void onEventsDeleted();
    }

    public interface EventsReceivedListener {
        void onEventsReceived(List<Events> events);
    }
}

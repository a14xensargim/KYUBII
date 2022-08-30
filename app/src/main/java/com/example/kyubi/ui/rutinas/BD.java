package com.example.kyubi.ui.rutinas;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BD {
    /**
     * Mètode que llegeix totes les rutines.
     * @param listener Objecte que escolta fins que la tasca de buscar
     *                 rutines acaba
     */
    public void ReadRutinas(RutinasReceivedListener listener){
        List<Rutinas> rutinas = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("rutinas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Rutinas rutina = new Rutinas(document.getString("imatge"),
                                        document.getString("fecha"),
                                        document.getString("nombre"),
                                        document.getString("apellido"),
                                        document.getString("correo"));

                                System.out.println("IMATGE --> " + document.getString("imatge"));
                                System.out.println("FECHA --> " + document.getString("fecha"));
                                System.out.println("NOMBRE --> " + document.getString("nombre"));
                                System.out.println("APELLIDO --> " + document.getString("apellido"));
                                System.out.println("CORREO --> " + document.getString("correo"));

                                rutinas.add(rutina);
                            }
                            listener.onNewsReceived(rutinas);
                        } else {

                        }
                    }
                });
    }

    public void saveEvent(String titol, String descripcio, String url){
        System.out.println("HOLAAA");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> noticia = new HashMap<>();

        noticia.put("titol", titol);
        noticia.put("descripcion", descripcio);
        noticia.put("imatge", url);

        System.out.println("GUARDAAAAAAAAAAA");

        db.collection("noticias").document(String.valueOf(titol)).set(noticia);
    }

    public void DeleteEvent(String titol, RutinasDeletedListener deletedListener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("noticias").document(titol).delete().
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            deletedListener.onNewsDeleted(true);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            deletedListener.onNewsDeleted(false);
                        }
                    });
    }

    public interface RutinasDeletedListener {
        void onNewsDeleted(Boolean esborrat);
    }

    public interface RutinasReceivedListener {
        void onNewsReceived(List<Rutinas> noticies);
    }
}

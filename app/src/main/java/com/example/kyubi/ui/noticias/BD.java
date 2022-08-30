package com.example.kyubi.ui.noticias;

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
    public void ReadNews(NewsReceivedListener listener){
        List<com.example.kyubi.ui.noticias.News> noticies = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("noticias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                com.example.kyubi.ui.noticias.News noticia = new com.example.kyubi.ui.noticias.News(document.getString("titol"),
                                        document.getString("descripcion"),
                                        document.getString("imatge"));

                                System.out.println("DESCRIPCIO --> " + document.getString("descripcion"));
                                System.out.println("TITOL --> " + document.getString("titol"));
                                System.out.println("IMATGE --> " + document.getString("imatge"));

                                noticies.add(noticia);
                            }
                            listener.onNewsReceived(noticies);
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

    public void DeleteEvent(String titol, NewsDeletedListener deletedListener){
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

    public interface NewsDeletedListener {
        void onNewsDeleted(Boolean esborrat);
    }

    public interface NewsReceivedListener {
        void onNewsReceived(List<com.example.kyubi.ui.noticias.News> noticies);
    }
}

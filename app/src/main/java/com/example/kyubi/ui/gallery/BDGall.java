package com.example.kyubi.ui.gallery;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BDGall {

    public void savePhotos(String url){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> imatge = new HashMap<>();

        imatge.put("url", url);

        String[] urlsplit = url.split("token=");

        db.collection("imatge").document(String.valueOf(urlsplit[1])).set(imatge);
    }

    public void ReadPhotos(PhotosReceivedListener listener){
        ArrayList<String> fotos = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("imatge")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                fotos.add(document.getString("url"));
                            }
                            listener.onPhotosReceived(fotos);
                        } else {

                        }
                    }
                });
    }

    public interface PhotosReceivedListener {
        void onPhotosReceived(ArrayList<String> fotos);
    }
}

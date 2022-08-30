package com.example.kyubi.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.kyubi.databinding.FragmentGaleriaBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private static final int RESULT_OK = -1;
    private FragmentGaleriaBinding binding;
    Gallery simpleGallery;
    Adapter2 customGalleryAdapter;
    ImageView selectedImageView;
    Button afegir;
    FirebaseUser user;
    String url2;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PHOTO = 1;

    ArrayList<String> images = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGaleriaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        simpleGallery = binding.simpleGallery;
        selectedImageView = binding.selectedImageView;

        cargarImagenes();

        return root;
    }

    private void cargarImagenes(){
        BDGall db = new BDGall();

        db.ReadPhotos(this::onPhotosReceived);
    }


    private void onPhotosReceived(ArrayList<String> fotos) {
        images.clear();
        images.addAll(fotos);
        customGalleryAdapter = new Adapter2(getContext(), images);
        simpleGallery.setAdapter(customGalleryAdapter);
        new DownloadImageTask(selectedImageView).execute(images.get(0));
        afegir = binding.afegir;

        storage = FirebaseStorage.getInstance();

        simpleGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new DownloadImageTask(selectedImageView).execute(images.get(position));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null && (user.getEmail().equals("a14xensargim@inspedralbes.cat") ||
                user.getEmail().equals("xeniaippon@hotmail.es"))){
            afegir.setVisibility(View.VISIBLE);
        }else{
            afegir.setVisibility(View.INVISIBLE);
        }

        afegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO);

                System.out.println(url2);
            }
        });

        verifyStoragePermissions(getActivity());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO && resultCode == RESULT_OK) {
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_gallery");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fotoReferencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url2 = uri.toString();
                            System.out.println("ONACTRES --> " + url2);
                            BDGall bd = new BDGall();
                            bd.savePhotos(url2);

                            Toast.makeText(getContext(), "Imagen añadida!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public boolean verifyStoragePermissions(Activity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
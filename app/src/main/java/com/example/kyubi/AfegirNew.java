package com.example.kyubi;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.kyubi.ui.noticias.BD;
import com.example.kyubi.ui.noticias.NewsFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AfegirNew extends AppCompatActivity {
    private static final int PHOTO = 1;

    ProgressBar progressBar;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Task<Uri> url = null;
    String url2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_noticia);

        ImageView fotoPerfil = findViewById(R.id.fotoperfil);
        EditText titol = findViewById(R.id.introducirTitulo);
        EditText descripcio = findViewById(R.id.enterDescripcio);
        Button afegir = findViewById(R.id.añadir);
        progressBar = findViewById(R.id.progressBarN);

        storage = FirebaseStorage.getInstance();

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO);
            }
        });

        afegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(titol.getText().toString() != null && descripcio.getText().toString()
                        != null && url2 != null){
                    progressBar.setVisibility(View.VISIBLE);
                    BD bd = new BD();

                    bd.saveEvent(titol.getText().toString(), descripcio.getText().toString(), url2);

                    Toast.makeText(AfegirNew.this, "Noticia añadida!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(AfegirNew.this, NewsFragment.class);
                    startActivity(intent);
                }
            }
        });

        verifyStoragePermissions(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO && resultCode == RESULT_OK) {
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_noticia");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fotoReferencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url2 = uri.toString();
                        }
                    });
                }
            });
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
}

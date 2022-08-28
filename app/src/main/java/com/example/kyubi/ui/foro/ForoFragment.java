package com.example.kyubi.ui.foro;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kyubi.Login;
import com.example.kyubi.databinding.FragmentForoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForoFragment extends Fragment {
    private ForoViewModel foroViewModel;
    private FragmentForoBinding binding;

    private CircleImageView fotoPerfil;
    private String url2;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;
    private ImageButton btnEnviarFoto;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;
    private String fotoPerfilCadena;

    private FirebaseAuth mAuth;
    private String NOMBRE_USUARIO;
    private String FOTO_PERFIL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding = FragmentForoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(user != null) {
            fotoPerfil = binding.fotoPerfil;
            nombre = binding.nombre;
            rvMensajes = binding.rvMensaje;
            txtMensaje = binding.txtMensaje;
            btnEnviar = binding.btnEnvi;
            btnEnviarFoto = binding.btnEnviarFoto;

            database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference("chatV2");//Sala de chat (nombre) version 2
            storage = FirebaseStorage.getInstance();
            mAuth = FirebaseAuth.getInstance();

            adapter = new AdapterMensajes(getContext());
            LinearLayoutManager l = new LinearLayoutManager(getContext());
            rvMensajes.setLayoutManager(l);
            rvMensajes.setAdapter(adapter);

            btnEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseReference.push().setValue(new MensajeEnviar(txtMensaje.getText().toString(),
                            NOMBRE_USUARIO, FOTO_PERFIL, "1", ServerValue.TIMESTAMP));
                    txtMensaje.setText("");
                }
            });

            btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/jpeg");
                    i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO_SEND);
                }
            });

            fotoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/jpeg");
                    i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO_PERFIL);
                }
            });

            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    setScrollbar();
                }
            });

            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MensajeRecibir m = dataSnapshot.getValue(MensajeRecibir.class);
                    adapter.addMensaje(m);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            verifyStoragePermissions(getActivity());
        }else{
            Intent intent = new Intent(getActivity(), Login.class);
            getActivity().startActivity(intent);

            Toast.makeText(getContext(), "Para entrar al foro, debes tener una sesión creada", Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    private void setScrollbar() {
        rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_SEND && resultCode == getActivity().RESULT_OK) {
            Uri u = data.getData();
            storageReference = storage.getReference("imagenes_chat");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fotoReferencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url2 = uri.toString();
                            MensajeEnviar m = new MensajeEnviar(NOMBRE_USUARIO + " te ha enviado una foto", url2, NOMBRE_USUARIO, fotoPerfilCadena, "2", ServerValue.TIMESTAMP);
                            databaseReference.push().setValue(m);
                        }
                    });
                }
            });
        } else if (requestCode == PHOTO_PERFIL && resultCode == getActivity().RESULT_OK) {
            Uri u = data.getData();
            storageReference = storage.getReference("foto_perfil");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());
            fotoReferencia.putFile(u).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fotoReferencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url2 = uri.toString();
                            fotoPerfilCadena = u.toString();
                            MensajeEnviar m = new MensajeEnviar(NOMBRE_USUARIO + " ha actualizado su foto de perfil", url2, NOMBRE_USUARIO, fotoPerfilCadena, "2", ServerValue.TIMESTAMP);
                            databaseReference.push().setValue(m);
                            Glide.with(getActivity()).load(url2).into(fotoPerfil);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(url2))
                                    .build();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            btnEnviar.setEnabled(true);
            DatabaseReference reference = database.getReference("Usuarios/" + currentUser.getUid());
            System.out.println(reference.toString());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    NOMBRE_USUARIO = currentUser.getEmail();
                    if(currentUser.getPhotoUrl() != null) {
                        FOTO_PERFIL = currentUser.getPhotoUrl().toString();
                        Glide.with(getActivity()).load(FOTO_PERFIL).into(fotoPerfil);
                    }
                    nombre.setText(NOMBRE_USUARIO);
                    btnEnviar.setEnabled(true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            btnEnviar.setEnabled(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
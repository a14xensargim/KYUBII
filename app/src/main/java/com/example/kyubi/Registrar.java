package com.example.kyubi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registrar extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText usuario;
    private EditText pasw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registrar);
        usuario = findViewById(R.id.username);
        pasw = findViewById(R.id.password);
        Button registrar = findViewById(R.id.registrarbtn);

        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // El email a validar
                String email = usuario.getText().toString();

                Matcher mather = pattern.matcher(email);

                if(pasw.length() < 6 || !mather.find()){
                    if(pasw.length() < 6) {
                        Toast.makeText(Registrar.this, "La contraseña debe tener mínimo 6 carácteres",
                                Toast.LENGTH_SHORT).show();
                    }else if(!mather.find()){
                        Toast.makeText(Registrar.this, "El correo no es válido",
                                Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(Registrar.this, Registrar.class);
                    startActivity(intent);
                }else {
                    crearCuenta();
                }
            }
        });
    }

    private void crearCuenta() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(usuario.getText().toString(),
                pasw.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(Registrar.this, "Registro correcto",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Registrar.this, MainActivity.class);
                        startActivity(intent);

                        if (!task.isSuccessful()) {
                            //Toast.makeText(this, "Authentication failed.",  Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

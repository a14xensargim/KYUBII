package com.example.kyubi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);

        TextView forgotPsw = findViewById(R.id.forgotPass);
        Button registrar = findViewById(R.id.registrar);

        MaterialButton loginbtn = findViewById(R.id.loginbtn);

        forgotPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("")){
                    Toast.makeText(Login.this, "Primero debes introducir tu correo " +
                                    "en el apartado <<Usuario>>",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Login.this, Login.class);
                    startActivity(intent);
                }else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = username.getText().toString();

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Mensaje enviado correctamente",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, com.example.kyubi.Registrar.class);
                startActivity(intent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Authentication correcta.",
                                            Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if(user.getEmail().equals("a14xensargim@inspedralbes.cat") ||
                                            user.getEmail().equals("xeniaippon@hotmail.es"))
                                        Toast.makeText(Login.this, "Las funciones de administrador os" +
                                                        " han sido desbloqueadas",
                                                Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Autenticación fallida.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

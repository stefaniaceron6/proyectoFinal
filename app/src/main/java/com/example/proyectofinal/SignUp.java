package com.example.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {
    private Button signup, gotoLogin;
    private EditText emailInput, passInput;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);
        signup = findViewById(R.id.SignUp);
        gotoLogin = findViewById(R.id.Login);

        firebaseAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarme();
            }
        });

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });
    }

    public void registrarme() {

        String email = emailInput.getText().toString().trim();
        String password = passInput.getText().toString();

        boolean val = validarDatos(email, password);

        if (val) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUp.this, "Te Registrastes Correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String e = task.getException().toString();
                        Toast.makeText(SignUp.this, e, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }





    public boolean validarDatos(String email, String password) {
        if (email.length() == 0) {
            emailInput.setError("Ingrese un correo");
            return false;

        } else if( password.length() == 0) {
            passInput.setError("Ingrese su contraseña");
            return false;
        }
        return true;
    }


}
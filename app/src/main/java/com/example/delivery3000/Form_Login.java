package com.example.delivery3000;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Form_Login extends AppCompatActivity {
    private TextView txtCadastro, txtErro;
    private EditText etEmail, etSenha;
    private Button btLogar;
    private ProgressBar pbLogar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        getSupportActionBar().hide();
        iniciarComponentes();

        txtCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Form_Login.this, Form_Cadastro.class);
                startActivity(i);
            }
        });
        btLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String senha = etSenha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()){
                    txtErro.setText("Preencha todos os campos");
                }else{
                    txtErro.setText("");
                    logar(email, senha);
                }
            }
        });
    }

    private void iniciarComponentes(){
        txtCadastro = findViewById(R.id.txtCriarUser);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        txtErro = findViewById(R.id.txtErro);
        btLogar = findViewById(R.id.btEntrar);
        pbLogar = findViewById(R.id.pbTelaPrincipal);
    }
    public void logar(String email, String senha){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    pbLogar.setVisibility(View.VISIBLE);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(Form_Login.this, lista_produtos.class);
                            startActivity(i);
                        }
                    }, 3000);
                }else{
                    String erro;

                    try {
                        throw task.getException();
                    }catch (Exception e){
                        erro = "Erro ao logar usuario";
                    }
                    txtErro.setText(erro);
                }
            }
        });
    }
}
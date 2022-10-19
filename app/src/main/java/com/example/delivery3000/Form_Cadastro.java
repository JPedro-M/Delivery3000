package com.example.delivery3000;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;

import de.hdodenhof.circleimageview.CircleImageView;

public class Form_Cadastro extends AppCompatActivity {
    private EditText etNome, etEmail, etSenha;
    private Button btSlcFoto, btCadastrar;
    private CircleImageView imgFoto;
    private TextView txtMsgErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);
        iniciarComponentes();
        etNome.addTextChangedListener(cadastroTW);
        etEmail.addTextChangedListener(cadastroTW);
        etSenha.addTextChangedListener(cadastroTW);

        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarUsu(view);
            }
        });
    }

    public void cadastrarUsu(View view){
        String email, senha;

        email = etEmail.getText().toString();
        senha = etSenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Snackbar sb = Snackbar.make(view, "Cadastrado com sucesso", Snackbar.LENGTH_INDEFINITE);
                    sb.show();
                }
            }
        });
    }

    private void iniciarComponentes() {
        etNome = findViewById(R.id.etCadNome);
        etEmail = findViewById(R.id.etCadEmail);
        etSenha = findViewById(R.id.etCadSenha);
        btSlcFoto = findViewById(R.id.btSelecFoto);
        btCadastrar = findViewById(R.id.btCadastrar);
        imgFoto = findViewById(R.id.fotoUsu);
        txtMsgErro = findViewById(R.id.txtErro);
    }

    TextWatcher cadastroTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nome, email, senha;
            nome = etNome.getText().toString();
            email = etEmail.getText().toString();
            senha = etSenha.getText().toString();

            if (!nome.equals("") && !email.equals("") && !senha.equals("")){
                btCadastrar.setEnabled(true);
                btCadastrar.setBackgroundColor(getResources().getColor(R.color.dark_red));
            }else{
                btCadastrar.setEnabled(false);
                btCadastrar.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
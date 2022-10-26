package com.example.delivery3000;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Form_Cadastro extends AppCompatActivity {
    private EditText etNome, etEmail, etSenha;
    private Button btSlcFoto, btCadastrar;
    private CircleImageView imgFoto;
    private TextView txtMsgErro, txtCadUsu;
    private Uri imgPerfil;
    private String usuarioId;

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
        btSlcFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecionarFotoGaleria();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imgPerfil = data.getData();

                        try {
                            imgFoto.setImageURI(imgPerfil);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    public void selecionarFotoGaleria() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        activityResultLauncher.launch(i);
    }

    public void salvarDadosUsuario() {
        String nomeArquivo = UUID.randomUUID().toString();

        final StorageReference reference = FirebaseStorage.getInstance().getReference("fotoPerfil/" + nomeArquivo);
        reference.putFile(imgPerfil).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String foto = uri.toString();

                        String nome = etNome.getText().toString();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> usuarios = new HashMap<>();
                        usuarios.put("nome", nome);
                        usuarios.put("foto", foto);

                        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);

                        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.i("db", "Sucesso ao salvar os dados");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("db", "erro ao salvar os dados" + e.toString());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void cadastrarUsu(View view) {
        String email, senha;

        email = etEmail.getText().toString();
        senha = etSenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    salvarDadosUsuario();
                    Snackbar sb = Snackbar.make(view, "Cadastrado com sucesso", Snackbar.LENGTH_INDEFINITE).setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
                    sb.show();
                } else {
                    String erro = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Coloque uma senha com mais de 6 caracteres";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Email invalido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Email já cadastrado";
                    } catch (FirebaseNetworkException e) {
                        erro = "Sem internet";
                    } catch (Exception e) {
                        erro = "Erro ao cadastrar o usuario" + e;
                    }
                    txtMsgErro.setText(erro);
                }
            }
        });
    }

    public void iniciarComponentes() {
        etNome = findViewById(R.id.etCadNome);
        etEmail = findViewById(R.id.etCadEmail);
        etSenha = findViewById(R.id.etCadSenha);
        btSlcFoto = findViewById(R.id.btSelecFoto);
        btCadastrar = findViewById(R.id.btCadastrar);
        imgFoto = findViewById(R.id.fotoUsu);
        txtMsgErro = findViewById(R.id.txtCadErro);
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

            if (!nome.equals("") && !email.equals("") && !senha.equals("")) {
                btCadastrar.setEnabled(true);
                btCadastrar.setBackgroundColor(getResources().getColor(R.color.dark_red));
            } else {
                btCadastrar.setEnabled(false);
                btCadastrar.setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
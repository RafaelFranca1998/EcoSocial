package com.example.rafael_cruz.prototipo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rafael_cruz.prototipo.R;
import com.example.rafael_cruz.prototipo.config.Base64Custom;
import com.example.rafael_cruz.prototipo.config.DAO;
import com.example.rafael_cruz.prototipo.config.Preferencias;
import com.example.rafael_cruz.prototipo.model.Usuario;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class CadastroActivity extends AppCompatActivity {

    private EditText editTextPhone;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPwd;
    private Button buttonRegister;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;


    private Usuario user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        //-----------------------------------CAMPOS DO FORM-----------------------------------------
        editTextPhone = findViewById(R.id.editText_telefone);
        editTextEmail = findViewById(R.id.editText_email);
        editTextName = findViewById(R.id.editText_nome);
        editTextLastName = findViewById(R.id.editText_sobrenome);
        editTextPwd = findViewById(R.id.editText_senha);
        buttonRegister = findViewById(R.id.bt_cadastrar);
        //-----------------------------------MASCARA DE TEXTO---------------------------------------
        SimpleMaskFormatter simpleMasktelefone = new SimpleMaskFormatter("NN(NN)NNNNN-NNNN");
        MaskTextWatcher maskTel = new MaskTextWatcher(editTextPhone, simpleMasktelefone);
        editTextPhone.addTextChangedListener(maskTel);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new Usuario();
                user.setNome(editTextName.getText().toString());
                user.setSobreNome(editTextLastName.getText().toString());
                user.setEmail(editTextEmail.getText().toString());
                user.setSenha(editTextPwd.getText().toString());
                register();
            }
        });

        //get number from user
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String phoneNumber = "";
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.READ_SMS) != PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission
                        (this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CadastroActivity.this,
                    new String[]{Manifest.permission.READ_SMS},
                    0);
            ActivityCompat.requestPermissions(CadastroActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_NUMBERS},
                    0);
            ActivityCompat.requestPermissions(CadastroActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    0);
            return;
        }
        phoneNumber = tm.getLine1Number();
        if (!phoneNumber.equals("")) {
            editTextPhone.setText(phoneNumber);
            editTextPhone.setEnabled(false);
        }
        //------------------------------------------------------------------------------------------
    }

    /**
     *Add user to Datastorage and register.
     */
    private void register(){
        database = DAO.getFireBase();
        auth = DAO.getFirebaseAutenticacao();
        user =  new Usuario();
        user.setEmail(editTextEmail.getText().toString());
        user.setNome(editTextName.getText().toString());
        user.setSobreNome(editTextLastName.getText().toString());
        user.setTelefone(editTextPhone.getText().toString());
        final String linkImg = "gs://ecossocial-2c0dc.appspot.com/images/account/"+Base64Custom.codificarBase64(user.getEmail())+"/image_account.png";
        user.setLinkImgAccount(linkImg);
        auth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPwd.getText().toString())
                .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CadastroActivity.this,"Sucesso ao register usuário",Toast.LENGTH_LONG);

                            String identificadorUsuario = Base64Custom.codificarBase64(user.getEmail());
                            FirebaseUser user =  task.getResult().getUser();
                            CadastroActivity.this.user.setId( identificadorUsuario );
                            database.child("usuarios").child(identificadorUsuario).setValue(CadastroActivity.this.user);
                            openLoggedUser();
                            Preferencias preferencias = new Preferencias(CadastroActivity.this);
                            preferencias.salvarDados(CadastroActivity.this.user.getNome(), CadastroActivity.this.user.getSobreNome(), CadastroActivity.this.user.getEmail(),
                                    editTextPwd.getText().toString(),identificadorUsuario,linkImg);
                        }else {
                            String errorException = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                errorException = "Digite uma senha mais forte, contendo letras e numeros";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                errorException = "O emais digitado é inválido, digite outro email";
                            } catch (FirebaseAuthUserCollisionException e ){
                                errorException = "já existe outra conta com este e-mail";
                            }catch (Exception e){
                                errorException = "Erro a o efetuar cadastro";
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastroActivity.this,errorException,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void openLoggedUser(){
        Intent intent =  new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}

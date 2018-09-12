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

    private EditText editTextTelefone;
    private EditText editTextNome;
    private EditText editTextEmail;
    private EditText editTextSenha;
    private Button   buttoncadastrar;
    private DatabaseReference database;
    private FirebaseAuth auth;


    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        //--------------------------CAMPOS DO FORM-----------------------------------------------------------------------------------------
        editTextTelefone    = findViewById(R.id.editText_telefone);
        editTextEmail       = findViewById(R.id.editText_email);
        editTextNome        = findViewById(R.id.editText_nome);
        editTextSenha       = findViewById(R.id.editText_senha);
        buttoncadastrar     = findViewById(R.id.bt_cadastrar);
        //-----------------------------------MASCARA DE TEXTO--------------------------------------------------------------------------
        SimpleMaskFormatter simpleMasktelefone = new SimpleMaskFormatter("NN(NN)NNNNN-NNNN");
        MaskTextWatcher maskTel = new MaskTextWatcher(editTextTelefone, simpleMasktelefone);
        editTextTelefone.addTextChangedListener(maskTel);

        buttoncadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrar();
            }
        });

        //Obtem numero do user
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_NUMBERS},PackageManager.PERMISSION_GRANTED );
            return;
        }
        String numeroDoTelefone = tm.getLine1Number();
        //-----------------------------------------------------------------------------------------------------------
        editTextTelefone.setText(numeroDoTelefone);
    }

    /**Adiciona o usuário ao banco de dados e cia login
     *
     */
    private void cadastrar(){
        database = DAO.getFireBase();
        auth = DAO.getFirebaseAutenticacao();
        usuario =  new Usuario();
        usuario.setEmail(editTextEmail.getText().toString());
        usuario.setNome(editTextNome.getText().toString());
        usuario.setTelefone(editTextTelefone.getText().toString());
        database.child("usuarios").child(editTextNome.getText().toString()).setValue(usuario);


        auth.createUserWithEmailAndPassword(editTextEmail.getText().toString(),editTextSenha.getText().toString())
                .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CadastroActivity.this,"Sucesso ao cadastrar usuário",Toast.LENGTH_LONG);

                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    FirebaseUser user =  task.getResult().getUser();
                    usuario.setId( identificadorUsuario );
 //                   usuario.salvar();
                    abrirUsuarioLogado();

//                    Preferencias preferencias = new Preferencias(CadastroActivity.this);
//                    preferencias.salvarDados( identificadorUsuario , usuario.getNome());
                }else {
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte, contendo letras e numeros";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "O emais digitado é inválido, digite outro email";
                    } catch (FirebaseAuthUserCollisionException e ){
                        erroExcecao = "já existe outra conta com este e-mail";
                    }catch (Exception e){
                        erroExcecao = "Erro a o efetuar cadastro";
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this,erroExcecao,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void abrirUsuarioLogado(){
        Intent intent =  new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}

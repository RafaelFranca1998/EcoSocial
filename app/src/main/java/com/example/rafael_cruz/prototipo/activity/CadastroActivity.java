package com.example.rafael_cruz.prototipo.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private CadastroActivity.UserLoginTask mAuthTask = null;
    private EditText editTextPhone;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPwd;
    private Button buttonRegister;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

    private View mProgressView;
    private View mLoginFormView;


    private Usuario user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        //-----------------------------------CAMPOS DO FORM-----------------------------------------
        editTextPhone       = findViewById(R.id.editText_phone);
        editTextEmail       = findViewById(R.id.editText_email);
        editTextName        = findViewById(R.id.editText_name);
        editTextLastName    = findViewById(R.id.editText_lastname);
        editTextPwd         = findViewById(R.id.editText_pwd);
        buttonRegister      = findViewById(R.id.bt_cadastrar);
        mProgressView       = findViewById(R.id.register_progress);
        mLoginFormView      = findViewById(R.id.register_form);
        //-----------------------------------MASCARA DE TEXTO---------------------------------------
        SimpleMaskFormatter simpleMaskcell = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        SimpleMaskFormatter simpleMasktelefone = new SimpleMaskFormatter("(NN)NNNN-NNNN");
        final MaskTextWatcher maskcell = new MaskTextWatcher(editTextPhone, simpleMaskcell);
        final MaskTextWatcher maskTel = new MaskTextWatcher(editTextPhone, simpleMasktelefone);

        editTextPhone.addTextChangedListener(maskTel);

        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editTextPhone.removeTextChangedListener(maskcell);
                editTextPhone.removeTextChangedListener(maskTel);
                if (editTextPhone.length() <= 13){
                    editTextPhone.addTextChangedListener(maskTel);
                } else if (editTextPhone.length() >= 14){
                    editTextPhone.addTextChangedListener(maskcell);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new Usuario();
                user.setNome(editTextName.getText().toString());
                user.setSobreNome(editTextLastName.getText().toString());
                user.setEmail(editTextEmail.getText().toString());
                user.setSenha(editTextPwd.getText().toString());
                attemptLogin();
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
        phoneNumber = phoneNumber.replaceAll("\\+|55","");
        if (!phoneNumber.equals("")) {
            editTextPhone.setText(phoneNumber);
            //editTextPhone.setEnabled(false);
        }
        //------------------------------------------------------------------------------------------
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        editTextPhone.setError(null);
        editTextName.setError(null);
        editTextLastName.setError(null);
        editTextEmail.setError(null);
        editTextPwd.setError(null);


        // Store values at the time of the login attempt.
        String email = editTextEmail.getText().toString();
        String password = editTextPwd.getText().toString();
        String phone = editTextPhone.getText().toString();
        String name = editTextName.getText().toString();
        String lastName = editTextLastName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            editTextPwd.setError(getString(R.string.error_invalid_password));
            focusView = editTextPwd;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
            editTextPwd.setError(getString(R.string.error_invalid_password));
            focusView = editTextPwd;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.error_field_required));
            focusView = editTextEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = editTextEmail;
            cancel = true;
        }

        // Check for a valid phone, if the user entered one.
        if (!TextUtils.isEmpty(phone) && !isPhoneValid(phone)) {
            editTextPhone.setError(getString(R.string.error_invalid_phone));
            focusView = editTextPhone;
            cancel = true;
        }
        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            editTextName.setError(getString(R.string.error_field_required));
            focusView = editTextName;
            cancel = true;
        }

        // Check for a valid last name.
        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError(getString(R.string.error_field_required));
            focusView = editTextLastName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new CadastroActivity.UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isPhoneValid(String phone) {
        return phone.length() > 12|| phone.length() < 14;
    }

    public void openLoggedUser(){
        showProgress(false);
        Intent intent =  new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                database = DAO.getFireBase();
                auth = DAO.getFirebaseAutenticacao();
                user =  new Usuario();
                user.setEmail(editTextEmail.getText().toString());
                user.setNome(editTextName.getText().toString());
                user.setSobreNome(editTextLastName.getText().toString());
                user.setTelefone(editTextPhone.getText().toString());
                final String linkImg = "gs://ecossocial-2c0dc.appspot.com/images/account/"+Base64Custom.codificarBase64(user.getEmail())+"/image_account.png";
                user.setLinkImgAccount(linkImg);
                auth.createUserWithEmailAndPassword(mEmail, mPassword)
                        .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(CadastroActivity.this,"Sucesso ao registrar usuário",Toast.LENGTH_LONG).show();
                                    String identificadorUsuario = Base64Custom.codificarBase64(user.getEmail());
                                    //FirebaseUser user =  task.getResult().getUser();

                                    CadastroActivity.this.user.setId( identificadorUsuario );
                                    Preferencias preferencias = new Preferencias(CadastroActivity.this);
                                    preferencias.salvarDados(CadastroActivity.this.user.getNome(), CadastroActivity.this.user.getSobreNome(), CadastroActivity.this.user.getEmail(),
                                            editTextPwd.getText().toString(),identificadorUsuario,linkImg);
                                    database.child("usuarios").child(identificadorUsuario).setValue(CadastroActivity.this.user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            openLoggedUser();
                                        }
                                    });
                                }else {
                                    String errorException;
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        errorException = "Digite uma senha mais forte, contendo letras e numeros";
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        errorException = "O email digitado é inválido, digite outro email";
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
                return true;
            } catch (Exception e) {
                return false;
            }

            // TODO: register the new account here.
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (!success) {
                editTextPwd.setError(getString(R.string.error_incorrect_password));
                editTextPwd.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

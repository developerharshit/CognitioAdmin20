package in.nitjsr.cognitioadmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public FirebaseAuth auth;
    public EditText etName,etEmail,etPassword;
    String email,name,password;
    public ProgressBar progressBar;
    SharedPreferences preferences;
    public static final String SHAREDPREFS="My Data";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        etEmail=findViewById(R.id.email);
        etPassword=findViewById(R.id.password);
        etName=findViewById(R.id.name);
        progressBar=findViewById(R.id.login_progress);
        FirebaseUser user = auth.getCurrentUser();
        proceed(user);
    }
    public void proceed(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        preferences=getSharedPreferences(SHAREDPREFS,MODE_PRIVATE);
        findViewById(R.id.signIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=etEmail.getText().toString();
                password=etPassword.getText().toString();
                name=etName.getText().toString();
                etEmail.setText("");
                etPassword.setText("");
                etName.setText("");
                progressBar.setVisibility(View.VISIBLE);
                final SharedPreferences.Editor editor=preferences.edit();
                if(!TextUtils.isEmpty(email.trim())&&!TextUtils.isEmpty(password.trim())&&!TextUtils.isEmpty(name.trim())) {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    editor.putString("email",email);
                                    editor.putString("name",name);
                                    editor.apply();
                                    proceed(authResult.getUser());
                                    Toast.makeText(LoginActivity.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Log In Failed!!!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
    }
}

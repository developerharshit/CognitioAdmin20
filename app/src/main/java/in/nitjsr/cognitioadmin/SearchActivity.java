package in.nitjsr.cognitioadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static in.nitjsr.cognitioadmin.Constants.INTENT_PARAM_SEARCH_FLAG;
import static in.nitjsr.cognitioadmin.Constants.INTENT_PARAM_SEARCH_ID;
import static in.nitjsr.cognitioadmin.Constants.SEARCH_FLAG_EMAIL;
import static in.nitjsr.cognitioadmin.Constants.SEARCH_FLAG_CG_ID;
import static in.nitjsr.cognitioadmin.Constants.SEARCH_FLAG_PAYMENT;
import static in.nitjsr.cognitioadmin.Constants.SEARCH_FLAG_QR;
import static in.nitjsr.cognitioadmin.Constants.SEARCH_FLAG_SEARCH;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mCognitioId,mEmail,mQRCode;
    private EditText etCgId, etEmail;
    private IntentIntegrator integrator;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mCognitioId =findViewById(R.id.search_by_cognitio_id);
        mEmail=findViewById(R.id.search_by_email);
        mQRCode=findViewById(R.id.btn_search_qr);
        etCgId = findViewById(R.id.et_search_cg_id);
        etEmail = findViewById(R.id.et_search_email);

        integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);

        mCognitioId.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mQRCode.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.search_by_cognitio_id) {
            String cognitioID = etCgId.getText().toString().trim();
            if (!TextUtils.isEmpty(cognitioID)) {
                openUserDetailActivity(cognitioID, SEARCH_FLAG_CG_ID);
            }
            else showMessage("Enter CognitioID");
        } else if (view.getId()==R.id.search_by_email) {
            String emailID = etEmail.getText().toString().trim();
            if (!TextUtils.isEmpty(emailID)) {
                openUserDetailActivity(emailID, SEARCH_FLAG_EMAIL);
            }
            else showMessage("Enter email ID");
        } else if (view.getId()==R.id.btn_search_qr) {
            integrator.initiateScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) showMessage("Cancelled");
            else {
                openUserDetailActivity(result.getContents(), SEARCH_FLAG_QR);
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private void showMessage(final String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void openUserDetailActivity(final String ID, final int FLAG){
        Intent intent;
        intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(INTENT_PARAM_SEARCH_ID, ID);
        intent.putExtra(INTENT_PARAM_SEARCH_FLAG, FLAG);
        startActivity(intent);
    }
}

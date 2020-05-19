package in.nitjsr.cognitioadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_COGNITIO_ID;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_COLLEGE;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_COLLEGE_REG_ID;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_DESK;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_DESK_PAYMENT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_EMAIL;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_KIT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_MOBILE;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_NAME;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PAID_AMOUNT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PAYMENT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PHOTO;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_TSHIRT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_TSHIRT_SIZE;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_USERS;
import static in.nitjsr.cognitioadmin.Constants.INTENT_PARAM_SEARCH_FLAG;
import static in.nitjsr.cognitioadmin.Constants.INTENT_PARAM_SEARCH_ID;
import static in.nitjsr.cognitioadmin.Constants.SEARCH_FLAG_CG_ID;
import static in.nitjsr.cognitioadmin.Constants.SEARCH_FLAG_EMAIL;
import static in.nitjsr.cognitioadmin.Constants.SEARCH_FLAG_QR;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName, userInstitute, userRegNo, userPaymentDetails, userCognitioID;
    private TextView userEmail, userMobile, userHash, userPaymentMode;
    private CheckBox tShirtCheckBox, kitCheckBox;
    private Spinner sizeSpinner;
    private Button btnEditProfile;
    private ProgressDialog pd;
    private String userHashID;
    private boolean trackTshirt, trackKit, trackPayment;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        pd = new ProgressDialog(this);
        pd.setMessage("Fetching User...");
        pd.setTitle("Please Wait");
        pd.setCancelable(false);

        trackTshirt = true;
        trackKit = true;
        trackPayment = true;

        ref = FirebaseDatabase.getInstance().getReference();

        userName = findViewById(R.id.edit_text_user_name);
        userName.setEnabled(false);
        userInstitute = findViewById(R.id.edit_text_user_institute);
        userInstitute.setEnabled(false);
        userRegNo = findViewById(R.id.edit_text_user_reg_no);
        userRegNo.setEnabled(false);
        userPaymentDetails = findViewById(R.id.edit_text_paid_amt);
        userPaymentDetails.setEnabled(false);
        userPaymentMode = findViewById(R.id.edit_text_payment_mode);
        userPaymentMode.setEnabled(false);
        userCognitioID = findViewById(R.id.edit_text_user_cognitio_id);
        userCognitioID.setEnabled(false);

        userEmail = findViewById(R.id.text_view_user_email);
        userMobile = findViewById(R.id.text_view_user_phone);
        userHash = findViewById(R.id.tv_user_hash);

        tShirtCheckBox = findViewById(R.id.checkbox_tshirt);
        kitCheckBox = findViewById(R.id.checkbox_kit);

        sizeSpinner = findViewById(R.id.sp_tshirt_size);

        btnEditProfile = findViewById(R.id.btn_edit_profile);

        btnEditProfile.setOnClickListener(this);
        findViewById(R.id.ib_edit_profile).setOnClickListener(this);


        Intent intent = getIntent();
        final String ID = intent.getStringExtra(INTENT_PARAM_SEARCH_ID);
        final int SEARCH_FLAG = intent.getIntExtra(INTENT_PARAM_SEARCH_FLAG, 0);
        searchUser(SEARCH_FLAG, ID);
        prohibitEdit();
    }

    private void prohibitEdit() {
        userCognitioID.setEnabled(false);
        userName.setEnabled(false);
        userInstitute.setEnabled(false);
        userRegNo.setEnabled(false);
        userPaymentDetails.setEnabled(false);
        kitCheckBox.setEnabled(false);
        tShirtCheckBox.setEnabled(false);
        sizeSpinner.setEnabled(false);
    }

    private void searchUser(int search_flag, String id) {
        pd.show();

        switch (search_flag) {
            case SEARCH_FLAG_EMAIL:
                ref.child(FIREBASE_REF_USERS).orderByChild(FIREBASE_REF_EMAIL).equalTo(id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        userHashID = child.getKey();
                                        fillData(child);
                                    }
                                } else showError();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                break;
            case SEARCH_FLAG_CG_ID:
                ref.child(FIREBASE_REF_USERS).orderByChild(FIREBASE_REF_COGNITIO_ID).equalTo(Integer.parseInt(id))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        userHashID = child.getKey();
                                        fillData(child);
                                    }
                                } else showError();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                break;
            case SEARCH_FLAG_QR:
                userHashID = id;
                ref.child(FIREBASE_REF_USERS).child(userHashID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) fillData(dataSnapshot);
                                else showError();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                showError();
                            }
                        });
                break;
        }
    }

    private void showError() {
        if (pd.isShowing()) pd.dismiss();
        Toast.makeText(this, "User Not Found!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void fillData(DataSnapshot dataSnapshot) {
        Picasso.with(this).load(dataSnapshot.child(FIREBASE_REF_PHOTO).getValue().toString()).fit().into((ImageView) findViewById(R.id.iv_user_image));
        if (dataSnapshot.child(FIREBASE_REF_COGNITIO_ID).exists())
            userCognitioID.setText(dataSnapshot.child(FIREBASE_REF_COGNITIO_ID).getValue().toString());

        userHash.setText(userHashID);
        userName.setText(dataSnapshot.child(FIREBASE_REF_NAME).getValue().toString());
        userEmail.setText(dataSnapshot.child(FIREBASE_REF_EMAIL).getValue().toString());
        userMobile.setText(dataSnapshot.child(FIREBASE_REF_MOBILE).getValue().toString());
        userInstitute.setText(dataSnapshot.child(FIREBASE_REF_COLLEGE).getValue().toString());
        userRegNo.setText(dataSnapshot.child(FIREBASE_REF_COLLEGE_REG_ID).getValue().toString());
        if (dataSnapshot.child(FIREBASE_REF_PAYMENT).exists()) {
            trackPayment = false;
            userPaymentDetails.setText(dataSnapshot.child(FIREBASE_REF_PAID_AMOUNT).getValue().toString());
        }
        if (dataSnapshot.child(FIREBASE_REF_PAYMENT).exists()) {
            if (dataSnapshot.child(FIREBASE_REF_PAYMENT).getValue().toString().equals("1")) {
                userPaymentMode.setText("Paytm Payment");
            } else if (dataSnapshot.child(FIREBASE_REF_PAYMENT).getValue().toString().equals("2")) {
                userPaymentMode.setText("Desk Payment");
            }
        }

        if (dataSnapshot.child(FIREBASE_REF_TSHIRT).exists()) {
            trackTshirt = false;
            tShirtCheckBox.setChecked(true);
        }
        if (dataSnapshot.child(FIREBASE_REF_KIT).exists()) {
            trackKit = false;
            kitCheckBox.setChecked(true);
        }
        setTshirtSize(dataSnapshot.child(FIREBASE_REF_TSHIRT_SIZE).getValue().toString());
        if (pd.isShowing()) pd.dismiss();
    }

    private void setTshirtSize(String s) {
        for (int i = 0; i < getResources().getStringArray(R.array.tshirt_size).length; i++)
            if (s.equals(getResources().getStringArray(R.array.tshirt_size)[i].split(" ")[0]))
                sizeSpinner.setSelection(i);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_edit_profile) {
            sendValueToServer();
        } else if (v.getId() == R.id.ib_edit_profile) {
            enableEdit();
        }
    }

    private void enableEdit() {
        findViewById(R.id.ib_edit_profile).setVisibility(View.GONE);
        btnEditProfile.setVisibility(View.VISIBLE);

        userName.setEnabled(true);
        userMobile.setEnabled(true);
        userInstitute.setEnabled(true);
        userRegNo.setEnabled(true);
        if (trackKit) kitCheckBox.setEnabled(true);
        if (trackTshirt) {
            tShirtCheckBox.setEnabled(true);
            sizeSpinner.setEnabled(true);
        }
        if (trackPayment) {
            userPaymentDetails.setEnabled(true);
        }
    }

    private void sendValueToServer() {
        DatabaseReference hashRef = ref.child(FIREBASE_REF_USERS).child(userHashID);
        hashRef.child(FIREBASE_REF_NAME).setValue(userName.getText().toString());
        hashRef.child(FIREBASE_REF_COLLEGE).setValue(userInstitute.getText().toString());
        hashRef.child(FIREBASE_REF_COLLEGE_REG_ID).setValue(userRegNo.getText().toString());
        hashRef.child(FIREBASE_REF_TSHIRT_SIZE).setValue(sizeSpinner.getSelectedItem().toString().split(" ")[0]);
        if (trackTshirt && tShirtCheckBox.isChecked()) hashRef.child(FIREBASE_REF_TSHIRT).setValue(true);
        if (trackKit && kitCheckBox.isChecked()) hashRef.child(FIREBASE_REF_KIT).setValue(true);
        if (trackPayment) {
            if (!TextUtils.isEmpty(userPaymentDetails.getText())){
                hashRef.child(FIREBASE_REF_PAYMENT).setValue(FIREBASE_REF_DESK_PAYMENT);
                hashRef.child(FIREBASE_REF_PAID_AMOUNT).setValue(userPaymentDetails.getText().toString());
                ref.child(FIREBASE_REF_PAYMENT).child(FIREBASE_REF_DESK).child(userHashID).setValue(userPaymentDetails.getText().toString());

            }
        }
        Toast.makeText(this, "Values updated!", Toast.LENGTH_SHORT).show();
        finish();
    }
}

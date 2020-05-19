package in.nitjsr.cognitioadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.service.autofill.Dataset;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_COGNITIO_ID;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_DESK;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_DESK_PAYMENT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_KIT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_NAME;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PAID_AMOUNT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PAYMENT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PAYTM;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PAYTM_PAYMENTMODE;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PAYTM_TXN_AMT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_TSHIRT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_TSHIRT_SIZE;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_USERS;
import static in.nitjsr.cognitioadmin.Constants.TSHIRT_SIZE;

public class DBInfoActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private ProgressDialog pd;
    CSVWriter writer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbinfo);

        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait");
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        ref = FirebaseDatabase.getInstance().getReference();
        runMasterQuery();

        findViewById(R.id.export_excel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                exportToExcel();
            }
        });
    }

    private void exportToExcel() {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "OjassData.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        FileWriter mFileWriter;
        try {
            if (f.exists() && !f.isDirectory()) {
                mFileWriter = new FileWriter(filePath, true);
                writer = new CSVWriter(mFileWriter);
            }
            else writer = new CSVWriter(new FileWriter(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ref.child(FIREBASE_REF_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot userData : dataSnapshot.getChildren()){
                    if (userData.child(FIREBASE_REF_PAYMENT).exists()){
                        count++;
                        String row[] = new String[2];
                        row[0] = userData.child(FIREBASE_REF_COGNITIO_ID).getValue().toString();
                        row[1] = userData.child(FIREBASE_REF_NAME).getValue().toString();
                        writer.writeNext(row);
                    }
                }
                Toast.makeText(DBInfoActivity.this, ""+count, Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void runMasterQuery() {
        ref.child(FIREBASE_REF_PAYMENT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int paidCount = 0, paytmPayment = 0, deskPayment = 0;
                int totalAmount = 0;

                if(dataSnapshot.child(FIREBASE_REF_PAYTM).exists()){
                    for(DataSnapshot data: dataSnapshot.child(FIREBASE_REF_PAYTM).getChildren()){
                        if(data.child(FIREBASE_REF_PAYTM_TXN_AMT).exists()){
                            paidCount++;
                            totalAmount += (int)Double.parseDouble(data.child(FIREBASE_REF_PAYTM_TXN_AMT).getValue().toString());
                            paytmPayment += (int)Double.parseDouble(data.child(FIREBASE_REF_PAYTM_TXN_AMT).getValue().toString());
                        }
                    }
                }
                if(dataSnapshot.child(FIREBASE_REF_DESK).exists()){
                    for(DataSnapshot data: dataSnapshot.child(FIREBASE_REF_DESK).getChildren()){
                        paidCount++;
                        totalAmount += Integer.parseInt(data.getValue().toString());
                        deskPayment += Integer.parseInt(data.getValue().toString());
                    }
                }
                ((TextView)findViewById(R.id.tv_info_total)).setText(""+paidCount);
                ((TextView)findViewById(R.id.tv_info_paid_paytm)).setText("\u20B9"+paytmPayment);
                ((TextView)findViewById(R.id.tv_info_paid_desk)).setText("\u20B9"+deskPayment);
                ((TextView)findViewById(R.id.tv_info_paid_total)).setText(paidCount + " ( \u20B9"+totalAmount+")");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child(FIREBASE_REF_USERS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int kitCount = 0, totalTshirtCount = 0;
                int[] tshirt = new int[6];
                int[] tshirtGiven = new int[6];
                for(int i = 0 ; i < 6 ; i++){
                    tshirt[i] = 0;
                }
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.child(FIREBASE_REF_TSHIRT).exists()){
                        totalTshirtCount++;
                        for (int i = 0; i<TSHIRT_SIZE.length; i++){
                            if(data.child(FIREBASE_REF_TSHIRT_SIZE).getValue().toString().equals(TSHIRT_SIZE[i])){
                                tshirt[i]++;
                            }
                        }
                    }
                    if (data.child(FIREBASE_REF_KIT).exists()) kitCount++;
                }
                for(int i = 0; i<TSHIRT_SIZE.length; i++){
                    ((TextView)findViewById(R.id.tv_info_xs)).setText(""+tshirt[0]);
                    ((TextView)findViewById(R.id.tv_info_s)).setText(""+tshirt[1]);
                    ((TextView)findViewById(R.id.tv_info_m)).setText(""+tshirt[2]);
                    ((TextView)findViewById(R.id.tv_info_l)).setText(""+tshirt[3]);
                    ((TextView)findViewById(R.id.tv_info_xl)).setText(""+tshirt[4]);
                    ((TextView)findViewById(R.id.tv_info_xxl)).setText(""+tshirt[5]);
                }

                ((TextView)findViewById(R.id.tv_info_tshirt_given)).setText(""+totalTshirtCount);

                ((TextView)findViewById(R.id.tv_info_kit_given)).setText(""+kitCount);

                if (pd.isShowing()) pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

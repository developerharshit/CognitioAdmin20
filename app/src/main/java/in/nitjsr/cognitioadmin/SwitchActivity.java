package in.nitjsr.cognitioadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static in.nitjsr.cognitioadmin.Constants.FIREBASE_REF_PAYMENT;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_SWITCH_GUEST;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_SWITCH_ITINERARY;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_SWITCH_PAYTM;
import static in.nitjsr.cognitioadmin.Constants.FIREBASE_SWITCH_SPONSORS;

public class SwitchActivity extends AppCompatActivity implements View.OnClickListener {

    Switch paytm, itinerary, guest, sponsors;
    String paytm_value, itinerary_value, guest_value, sponsors_value;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);
        ref = FirebaseDatabase.getInstance().getReference().child("switch");

        paytm = findViewById(R.id.paytmkaro);
        itinerary = findViewById(R.id.itinerary);
        guest = findViewById(R.id.guests);
        sponsors = findViewById(R.id.sponsors);

        paytm.setOnClickListener(this);
        itinerary.setOnClickListener(this);
        guest.setOnClickListener(this);
        sponsors.setOnClickListener(this);

        getCurrentStatus();
    }

    private void getCurrentStatus() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("switch");
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                paytm_value = dataSnapshot.child(FIREBASE_SWITCH_PAYTM).getValue().toString();
                itinerary_value = dataSnapshot.child(FIREBASE_SWITCH_ITINERARY).getValue().toString();
                guest_value = dataSnapshot.child(FIREBASE_SWITCH_GUEST).getValue().toString();
                sponsors_value = dataSnapshot.child(FIREBASE_SWITCH_SPONSORS).getValue().toString();

                if(paytm_value.equals("1")) paytm.setChecked(true);
                else paytm.setChecked(false);
                if(guest_value.equals("1")) guest.setChecked(true);
                else guest.setChecked(false);
                if(itinerary_value.equals("1")) itinerary.setChecked(true);
                else itinerary.setChecked(false);
                if(sponsors_value.equals("1")) sponsors.setChecked(true);
                else sponsors.setChecked(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == paytm){
            if(paytm.isChecked()){
                ref.child(FIREBASE_SWITCH_PAYTM).setValue("0");
            }
            else ref.child(FIREBASE_SWITCH_PAYTM).setValue("1");
        }
        else if(v == itinerary){
            if(paytm.isChecked()){
                ref.child(FIREBASE_SWITCH_ITINERARY).setValue("0");
            }
            else ref.child(FIREBASE_SWITCH_ITINERARY).setValue("1");
        }
        else if(v == guest){
            if(paytm.isChecked()){
                ref.child(FIREBASE_SWITCH_GUEST).setValue("0");
            }
            else ref.child(FIREBASE_SWITCH_GUEST).setValue("1");
        }
        else if(v == sponsors){
            if(paytm.isChecked()){
                ref.child(FIREBASE_SWITCH_SPONSORS).setValue("0");
            }
            else ref.child(FIREBASE_SWITCH_SPONSORS).setValue("1");
        }
    }
}

package in.nitjsr.cognitioadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static in.nitjsr.cognitioadmin.MainActivity.EMAIL;
import static in.nitjsr.cognitioadmin.MainActivity.NAME;

public class SetNotification extends AppCompatActivity {

    EditText notif_header,notif_body;
    String header,content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notification);
        notif_body=findViewById(R.id.notif_body);
        notif_header=findViewById(R.id.notif_header);

        findViewById(R.id.push).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                header=notif_header.getText().toString();
                content=notif_body.getText().toString();
                if(!(TextUtils.isEmpty(header))&&!(TextUtils.isEmpty(content))){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    String key = databaseReference.child("Notifications").push().getKey();
                    ExpandableListModal notifs = new ExpandableListModal(header.trim(),content.trim(),NAME,EMAIL);
                    Map<String, Object> postValues = notifs.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(key,postValues);
                    databaseReference.child("Notifications").updateChildren(childUpdates);
                    notif_header.setText("");
                    notif_body.setText("");
                }
                else{
                    Toast.makeText(SetNotification.this,"Fields cannot be empty!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

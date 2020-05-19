package in.nitjsr.cognitioadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static in.nitjsr.cognitioadmin.MainActivity.EMAIL;
import static in.nitjsr.cognitioadmin.MainActivity.NAME;

public class Notification extends AppCompatActivity {

    EditText notif_header,notif_body;
    String header,content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

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
                    NotificationTask notificationTask = new NotificationTask();
                    notificationTask.execute(header.trim(),content.trim());
                    notif_header.setText("");
                    notif_body.setText("");
                }
                else{
                    Toast.makeText(Notification.this,"Fields cannot be empty!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    class NotificationTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                String jsonResponse;

                URL url = new URL("https://onesignal.com/api/v1/notifications");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Authorization", "Basic NWRjNWY3MGUtNDE0MC00NTM4LTlhOWUtMTdlMzE2MTE2N2I5");
                con.setRequestMethod("POST");
                String strJsonBody = "{"
                        +   "\"app_id\": \"2305c53b-8684-4f80-a67a-5a6237db8665\","
                        +   "\"included_segments\": [\"All\"],"
                        +   "\"data\": {\"foo\": \"bar\"},"
                        +   "\"headings\": {\"en\": \"" + strings[0] +"\"},"
                        +   "\"contents\": {\"en\": \"" + strings[1] +"\"},"
                        +   "\"small_icon\":  \"icon\""
                        + "}";

                byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                con.setFixedLengthStreamingMode(sendBytes.length);

                OutputStream outputStream = con.getOutputStream();
                outputStream.write(sendBytes);

                int httpResponse = con.getResponseCode();

                if (  httpResponse >= HttpURLConnection.HTTP_OK
                        && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                    Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                    jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();
                }
                else {
                    Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                    jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();
                }
                System.out.println("jsonResponse:\n" + jsonResponse);

            } catch(Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
    }
}

package in.nitjsr.cognitioadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button search, push_notif, setNotif, dbInfo, switchButton;
    public static String NAME;
    public static String EMAIL;
    SharedPreferences preferences;
    public static final String DEFAULT="N/A" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.search);
        push_notif = findViewById(R.id.push_notif);
        setNotif = findViewById(R.id.set_notif);
        dbInfo = findViewById(R.id.dbinfo);
        switchButton  = findViewById(R.id.switch_button);

        search.setOnClickListener(this);
        push_notif.setOnClickListener(this);
        setNotif.setOnClickListener(this);
        dbInfo.setOnClickListener(this);
        switchButton.setOnClickListener(this);
        preferences=getSharedPreferences(LoginActivity.SHAREDPREFS,MODE_PRIVATE);

        NAME=preferences.getString("name",DEFAULT);
        EMAIL=preferences.getString("email",DEFAULT);
    }

    @Override
    public void onClick(View v) {
        if(v == search){
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
        }else if(v == push_notif){
            startActivity(new Intent(this, Notification.class));
        }else if(v == setNotif){
            startActivity(new Intent(this,SetNotification.class));
        }else if(v == switchButton){
            startActivity(new Intent(this, SwitchActivity.class));
        }else if(v == dbInfo){
            startActivity(new Intent(this, DBInfoActivity.class));
        }
    }
}

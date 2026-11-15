package com.example.placementbroadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // the action name. A receiver only gets broadcasts whose action matches its IntentFilter.
    static final String ACTION_PLACEMENT_UPDATE = "com.example.placementbroadcast.ACTION_PLACEMENT_UPDATE";

    EditText etCompany, etRole, etPackage;
    Button btnSendBroadcast, btnSendWrong;
    TextView tvResult;

    // BroadcastReceiver: onReceive() runs when a matching broadcast arrives
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String company = intent.getStringExtra("EXTRA_COMPANY");
            String role = intent.getStringExtra("EXTRA_ROLE");
            String pack = intent.getStringExtra("EXTRA_PACKAGE");

            if (company == null || role == null || pack == null) {
                tvResult.setText("Broadcast received but some details are missing");
            } else {
                tvResult.setText("Placement Update Received\n\nCompany: " + company
                        + "\nRole: " + role + "\nPackage: " + pack);
                Toast.makeText(context, "New placement update", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCompany = findViewById(R.id.etCompany);
        etRole = findViewById(R.id.etRole);
        etPackage = findViewById(R.id.etPackage);
        btnSendBroadcast = findViewById(R.id.btnSendBroadcast);
        btnSendWrong = findViewById(R.id.btnSendWrong);
        tvResult = findViewById(R.id.tvResult);

        btnSendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUpdate(ACTION_PLACEMENT_UPDATE);
            }
        });

        // same data but a different action, so the receiver will ignore it
        btnSendWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUpdate("com.example.placementbroadcast.SOME_OTHER_ACTION");
                Toast.makeText(MainActivity.this, "Sent with another action, receiver ignores it", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendUpdate(String action) {
        String company = etCompany.getText().toString().trim();
        String role = etRole.getText().toString().trim();
        String pack = etPackage.getText().toString().trim();

        if (company.isEmpty() || role.isEmpty() || pack.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(action);
        intent.putExtra("EXTRA_COMPANY", company);
        intent.putExtra("EXTRA_ROLE", role);
        intent.putExtra("EXTRA_PACKAGE", pack);
        intent.setPackage(getPackageName()); // only this app can receive it
        sendBroadcast(intent);
    }

    // register the receiver when the activity becomes visible
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ACTION_PLACEMENT_UPDATE);
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    // unregister it when the activity is no longer visible
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}

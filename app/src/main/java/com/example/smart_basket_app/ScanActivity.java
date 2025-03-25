package com.example.smart_basket_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScanActivity extends AppCompatActivity {

    private Button Scan_Button;
    private TextView Scan_Text_Msg;
    private ProgressBar Scan_Progress_Bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Scan_Button = findViewById(R.id.scan_btn);
        Scan_Text_Msg = findViewById(R.id.scan_text);
        Scan_Progress_Bar = findViewById(R.id.scan_progressbar);

        Scan_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scan_Progress_Bar.setVisibility(View.VISIBLE);
                Toast.makeText(ScanActivity.this,"Scan the QR Code",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ScanActivity.this,Scan_QR_Activity.class));
                Scan_Progress_Bar.setVisibility(View.GONE);
            }
        });

    }
}

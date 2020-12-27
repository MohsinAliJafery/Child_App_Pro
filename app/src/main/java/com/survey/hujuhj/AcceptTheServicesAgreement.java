package com.survey.hujuhj;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.survey.hujuhj.R;

public class AcceptTheServicesAgreement extends AppCompatActivity {

    Button mNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_the_services_agreement);

        mNext = findViewById(R.id.next);




    }
}

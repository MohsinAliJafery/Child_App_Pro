package com.survey.hujuhj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ConnectKidsPhone extends AppCompatActivity {

    LinearLayout mConnectKidsPhone, mConnectKidsWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_kids_phone);

        mConnectKidsPhone = findViewById(R.id.connect_kids_phone);
        mConnectKidsWatch = findViewById(R.id.connect_kids_watch);

        mConnectKidsPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog mBottomSheetDialog;
                mBottomSheetDialog = new BottomSheetDialog(ConnectKidsPhone.this, R.style.BottomSheetDialogTheme);

                View mBottomSheetView = LayoutInflater.from(ConnectKidsPhone.this).inflate(R.layout.accept_the_services_agreement, findViewById(R.id.bottom_sheet_accept_terms_of_the_services));

               //BottomSheetBehavior.from(mBottomSheetView).setState(BottomSheetBehavior.STATE_EXPANDED);

                mBottomSheetDialog.setContentView(mBottomSheetView);
                mBottomSheetDialog.show();

                Button mNext = mBottomSheetView.findViewById(R.id.next);
                CheckBox mCheckbox = mBottomSheetView.findViewById(R.id.checkbox);

                mCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCheckbox.isChecked()){
                            mNext.setClickable(true);
                            mNext.setBackground(ContextCompat.getDrawable(ConnectKidsPhone.this, R.drawable.rounded_corners_with_dark_pink_background_for_button));
                        }else{
                            mNext.setClickable(false);
                            mNext.setBackground(ContextCompat.getDrawable(ConnectKidsPhone.this, R.drawable.rounded_corners_with_pink_background));

                        }
                    }
                });

                mNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mIntent = new Intent(ConnectKidsPhone.this, EnterTheCodeInYourKidsGadget.class);
                        startActivity(mIntent);
                        finish();

                    }
                });

            }
        });


    }
}

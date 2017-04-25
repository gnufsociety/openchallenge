package com.gnufsociety.openchallenge;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DonateActivity extends AppCompatActivity {

    @BindView(R.id.donate_address) public TextView mDonateAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.donate_copy_btn)
    public void copyDonateAddress() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Donate to OpenChallenge",
                mDonateAddress.getText().toString());
        clipboard.setPrimaryClip(clip);
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, DonateActivity.class);
        return in;
    }
}

package com.example.soc_macmini_15.inboxlikegmail.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;


import com.example.soc_macmini_15.inboxlikegmail.R;

import static com.example.soc_macmini_15.inboxlikegmail.R.string.compose;

public class SendEmailActivity extends AppCompatActivity {

    private EditText etTo, etCc, etBcc, etSubject, etBody;
    private ImageView imgIncludeOptions;
    private LinearLayout llCc, llBcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        init();

             setTitle(compose);

        imgIncludeOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBcc.setVisibility(View.VISIBLE);
                llCc.setVisibility(View.VISIBLE);
                imgIncludeOptions.setVisibility(View.GONE);

            }
        });

        etSubject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etBcc.getText().toString().isEmpty())
                        llBcc.setVisibility(View.GONE);
                    if (etCc.getText().toString().isEmpty())
                        llCc.setVisibility(View.GONE);
                    if (!etBcc.getText().toString().isEmpty() && !etCc.getText().toString().isEmpty()) {
                        imgIncludeOptions.setVisibility(View.GONE);
                    } else
                        imgIncludeOptions.setVisibility(View.VISIBLE);
                }
            }
        });

        etBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etBcc.getText().toString().isEmpty())
                        llBcc.setVisibility(View.GONE);
                    if (etCc.getText().toString().isEmpty())
                        llCc.setVisibility(View.GONE);
                    if (!etBcc.getText().toString().isEmpty() && !etCc.getText().toString().isEmpty()) {
                        imgIncludeOptions.setVisibility(View.GONE);
                    } else
                        imgIncludeOptions.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //Initialising Views
    private void init() {
        etTo = findViewById(R.id.et_to);
        etCc = findViewById(R.id.et_cc);
        etBcc = findViewById(R.id.et_bcc);
        etSubject = findViewById(R.id.et_subject);
        etBody = findViewById(R.id.et_compose_mail);
        imgIncludeOptions = findViewById(R.id.img_show_more);
        llBcc = findViewById(R.id.ll_bcc);
        llCc = findViewById(R.id.ll_cc);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send_mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send) {

            send();
        }
        return super.onOptionsItemSelected(item);
    }

    private void send() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        if (!etTo.getText().toString().isEmpty())
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{etTo.getText().toString()});
        if (!etCc.getText().toString().isEmpty())
            emailIntent.putExtra(Intent.EXTRA_CC, new String[]{etCc.getText().toString()});
        if (!etBcc.getText().toString().isEmpty())
            emailIntent.putExtra(Intent.EXTRA_BCC, new String[]{etBcc.getText().toString()});
        if (!etSubject.getText().toString().isEmpty())
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, etSubject.getText().toString());
        emailIntent.setType("plain/text");
        if (!etBody.getText().toString().isEmpty())
            emailIntent.putExtra(Intent.EXTRA_TEXT, etBody.getText().toString());
        startActivity(emailIntent);

    }

}

package me.tasy5kg.cutestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;

public class ConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_confirm);
        ((MaterialTextView) findViewById(R.id.mtv_confirm_text))
                .setText(getIntent().getStringExtra("confirm_text"));
        findViewById(R.id.mb_confirm_ok).setOnClickListener(v -> {
            setResult(RESULT_OK,
                    new Intent().putExtra("result", true));
            finish();
        });
        findViewById(R.id.mb_confirm_cancel).setOnClickListener(v -> {
            setResult(RESULT_OK,
                    new Intent().putExtra("result", false));
            finish();
        });
    }
}
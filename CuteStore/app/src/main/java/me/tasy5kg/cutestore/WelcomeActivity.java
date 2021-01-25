package me.tasy5kg.cutestore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        MaterialButton mbExit = findViewById(R.id.mb_exit);
        MaterialButton mbStart = findViewById(R.id.mb_start);
        mbExit.setOnClickListener(v -> finish());
        mbStart.setOnClickListener(v ->
                ActivityCompat.requestPermissions(
                        WelcomeActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        1));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getSharedPreferences("readAbout", MODE_PRIVATE)
                    .edit().putBoolean("readAbout", true).apply();
            startActivity(new Intent(
                    WelcomeActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this,
                    getString(R.string.permission_statement_text),
                    Toast.LENGTH_LONG).show();
        }
    }
}
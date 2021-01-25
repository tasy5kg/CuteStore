package me.tasy5kg.cutestore;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DependenciesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependencies);
        findViewById(R.id.mb_dependencies_close)
                .setOnClickListener(v -> finish());
    }
}
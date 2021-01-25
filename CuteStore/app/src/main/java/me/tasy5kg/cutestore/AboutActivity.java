package me.tasy5kg.cutestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        MaterialToolbar toolbar = findViewById(R.id.material_about_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MaterialTextView) findViewById(R.id.mtv_version))
                .setText(getString(R.string.version, BuildConfig.VERSION_NAME));
        findViewById(R.id.mcv_about_rate).setOnClickListener(v -> {
            try {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName())));
                Toast.makeText(this,
                        getString(R.string.please_give_five_stars),
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.mcv_about_feedback).setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse("mailto:"
                                + getString(R.string.tasy5kg_email_address))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.mcv_about_view_source_code).setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.cute_store_github_link))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.mcv_about_view_dependencies).setOnClickListener(v ->
                startActivity(new Intent(AboutActivity.this,
                        DependenciesActivity.class)));
        findViewById(R.id.mcv_about_donate).setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.donate_link))));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, getString(R.string.thank_you),
                    Toast.LENGTH_LONG).show();
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
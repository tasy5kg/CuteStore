package me.tasy5kg.cutestore;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.litepal.LitePal;

import java.util.List;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class MainActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {
    public static final int REQUEST_CODE_CONFIRM_DELETE = 1;
    private ZBarScannerView mScannerView;
    private ViewGroup scannerView;
    private String barcode;
    private MaterialTextView mtvBarcode;
    private TextInputEditText tietProductName;
    private TextInputEditText tietPrice;
    private MaterialButton mbDelete;
    private MaterialButton mbSave;
    private boolean productExist = false;

    private void verifySaveButtonState() {
        mbSave.setEnabled(!tietProductName.getText().toString().equals("")
                && !tietPrice.getText().toString().equals(""));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_light) {
            mScannerView.setFlash(!mScannerView.getFlash());
        } else if (item.getItemId() == R.id.item_manage_products) {
            startActivity(new Intent(MainActivity.this
                    , ManageProductsActivity.class));
        } else if (item.getItemId() == R.id.item_about) {
            startActivity(new Intent(MainActivity.this
                    , AboutActivity.class));
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getSharedPreferences("readAbout", MODE_PRIVATE)
                .getBoolean("readAbout", false)
                || ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            startActivity(new Intent(MainActivity.this,
                    WelcomeActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.material_main_toolbar);
        setSupportActionBar(toolbar);
        mtvBarcode = findViewById(R.id.mtv_barcode);
        tietProductName = findViewById(R.id.tiet_product_name);
        tietProductName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                verifySaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tietPrice = findViewById(R.id.tiet_price);
        tietPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                verifySaveButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mScannerView = new ZBarScannerView(this);
        scannerView = findViewById(R.id.scanner_view);
        scannerView.addView(mScannerView);
        scannerView.requestLayout();
        mScannerView.setAutoFocus(true);
        mScannerView.setBorderColor(getColor(R.color.orange));
        mScannerView.setLaserColor(getColor(R.color.orange));
        mbSave = findViewById(R.id.mb_save);
        mbSave.setOnClickListener(v -> {
            Product product = new Product(barcode, tietProductName.getText().toString(), tietPrice.getText().toString());
            product.saveOrUpdate("barcode=?", barcode);
            productExist = true;
            mbDelete.setEnabled(true);
            mbSave.setEnabled(false);
            Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
        });
        mbDelete = findViewById(R.id.mb_delete);
        mbDelete.setOnClickListener(v -> startActivityForResult(
                new Intent(MainActivity.this,
                        ConfirmActivity.class)
                        .putExtra("confirm_text", getString(
                                R.string.confirm_text_delete_this_product))
                , REQUEST_CODE_CONFIRM_DELETE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONFIRM_DELETE
                && resultCode == RESULT_OK
                && data != null
                && data.getBooleanExtra("result", false)) {
            tietProductName.setText("");
            tietPrice.setText("");
            mbDelete.setEnabled(false);
            LitePal.deleteAll(Product.class, "barcode=?", barcode);
            Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_LONG).show();
            productExist = false;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            scannerView.getLayoutParams().height =
                    (int) (scannerView.getWidth() / 2.5);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(@NonNull Result rawResult) {
        barcode = rawResult.getContents();
        if (!barcode.equals(mtvBarcode.getText().toString())
                || mbSave.isEnabled()) {
            //noinspection deprecation
            ((Vibrator) getSystemService(Service.VIBRATOR_SERVICE))
                    .vibrate(50);
            mtvBarcode.setText(barcode);
            List<Product> product = LitePal
                    .where("barcode=?", barcode)
                    .find(Product.class);
            productExist = product.size() != 0;
            tietProductName.setText(productExist ?
                    product.get(0).getProductName() : "");
            tietPrice.setText(productExist ?
                    product.get(0).getPrice() : "");
            mbSave.setEnabled(false);
            mbDelete.setEnabled(productExist);
        }
        mScannerView.setFlash(false);
        mScannerView.resumeCameraPreview(this);
    }
}
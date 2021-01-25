package me.tasy5kg.cutestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_IMPORT = 2;
    public static final int REQUEST_CODE_DELETE = 3;
    private static final int REQUEST_CODE_EXPORT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);
        findViewById(R.id.mb_mp_close).setOnClickListener(v -> finish());
        findViewById(R.id.mcv_mp_delete).setOnClickListener(v ->
                startActivityForResult(new Intent(
                                ManageProductsActivity.this,
                                ConfirmActivity.class)
                                .putExtra("confirm_text", getString(
                                        R.string.confirm_text_delete_all_product))
                        , REQUEST_CODE_DELETE));
        findViewById(R.id.mcv_mp_export).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name_) + ".csv");
            startActivityForResult(intent, REQUEST_CODE_EXPORT);
        });
        findViewById(R.id.mcv_mp_import).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/comma-separated-values");
            startActivityForResult(intent, REQUEST_CODE_IMPORT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_EXPORT:
                    List<Product> allProducts = LitePal.findAll(Product.class);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Product product : allProducts) {
                        stringBuilder.append(product.getBarcode()).append(",")
                                .append(product.getProductName()).append(",")
                                .append(product.getPrice()).append("\n");
                    }
                    try {
                        FileOutputStream fileOutputStream = (FileOutputStream)
                                getContentResolver()
                                        .openOutputStream(data.getData());
                        BufferedWriter bufferedWriter = new BufferedWriter(
                                new OutputStreamWriter(fileOutputStream));
                        bufferedWriter.write(stringBuilder.toString());
                        bufferedWriter.close();
                        fileOutputStream.close();
                        Toast.makeText(this,
                                getString(R.string.export_successfully),
                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(this,
                                getString(R.string.export_failed),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_CODE_IMPORT:
                    if (data.getData().getPath().endsWith(".csv")) {
                        try {
                            FileInputStream fileInputStream = (FileInputStream)
                                    getContentResolver()
                                            .openInputStream(data.getData());
                            BufferedReader bufferedReader = new BufferedReader(
                                    new InputStreamReader(fileInputStream));
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                String[] split = line.split(",");
                                if (split.length >= 3) {
                                    Product product = new Product(
                                            split[0], split[1], split[2]);
                                    product.saveOrUpdate(
                                            "barcode=?",
                                            product.getBarcode());
                                }
                            }
                            Toast.makeText(this,
                                    getString(R.string.import_successfully),
                                    Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(this,
                                    getString(R.string.import_failed),
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this,
                                getString(R.string.import_failed_please_csv),
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case REQUEST_CODE_DELETE:
                    if (data.getBooleanExtra("result",
                            false)) {
                        LitePal.deleteAll(Product.class);
                        Toast.makeText(getBaseContext(),
                                "All product data has been deleted.",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

    }
}
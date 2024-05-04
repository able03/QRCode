package com.example.qrcode;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity
{

    private ImageView iv_generated_qr, iv_displaY_qr;
    private EditText et_generate, et_display;
    private MaterialButton btn_display_qr, btn_generate_qr, btn_save_qr;
    private DBHelper dbHelper;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        initValues();

        setListener();



        dbHelper.close();

    }

    private void initValues()
    {
        iv_generated_qr = findViewById(R.id.ivQRImage);
        iv_displaY_qr = findViewById(R.id.ivQRDisplay);

        et_generate = findViewById(R.id.etInputQR);
        et_display = findViewById(R.id.etCheckQR);

        btn_display_qr = findViewById(R.id.btnDisplayQR);
        btn_generate_qr = findViewById(R.id.btnGenerateyQR);
        btn_save_qr = findViewById(R.id.btnSaveQR);

        dbHelper = new DBHelper(this);
    }

    private void setListener()
    {
        btn_generate_qr.setOnClickListener(generateQR -> {


            String generate = et_generate.getText().toString().trim();

            if(generate.length() > 0)
            {
                bitmap = encodeAsBitmap(generate);
                iv_generated_qr.setImageBitmap(bitmap);
            }

        });




        btn_save_qr.setOnClickListener(saveQR -> {


            String save_qr = et_generate.getText().toString().trim();

            // bitmap to bytes array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();

            if(save_qr.length() > 0)
            {
                if(dbHelper.addData(save_qr, bytes))
                {
                    Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
                }
            }


        });



        btn_display_qr.setOnClickListener(displayQR -> {

            String name = et_display.getText().toString().trim();

            if(name.length() > 0)
            {
                Cursor cursor = dbHelper.findQRByName(name);

                if(cursor.moveToFirst())
                {
                    // blob to bytes array to bitmap
                    byte[] bb = cursor.getBlob(2);
                    iv_displaY_qr.setImageBitmap(BitmapFactory.decodeByteArray(bb, 0, bb.length));
                }
            }


        });
    }

    public Bitmap encodeAsBitmap(String str)
    {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = null;
        try
        {
            bitMatrix = writer.encode(str, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e)
        {
            throw new RuntimeException(e);
        }

        int w = bitMatrix.getWidth();
        int h = bitMatrix.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                pixels[y * w + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }


}
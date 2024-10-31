package com.example.doc_message;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.TextView;

import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 1;
    private TextView tvMessages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvMessages = findViewById(R.id.tvMessages);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
        } else {
            readSms();
        }
    }
    private void readSms() {
        Cursor cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder smsBuilder = new StringBuilder();
            do {
                // Lấy chỉ số cho các cột cần thiết
                int addressIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS); // Số điện thoại gửi
                int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY); // Nội dung tin nhắn
                int dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE); // Thời gian gửi

                // Kiểm tra chỉ số không bị null
                if (addressIndex != -1 && bodyIndex != -1 && dateIndex != -1) {
                    String address = cursor.getString(addressIndex);
                    String smsBody = cursor.getString(bodyIndex);
                    long dateMillis = cursor.getLong(dateIndex);
                    String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(dateMillis)); // Định dạng thời gian

                    // Thêm thông tin vào builder
                    smsBuilder.append("Gửi đến: ").append(address).append("\n")
                            .append("Nội dung: ").append(smsBody).append("\n")
                            .append("Thời gian: ").append(date).append("\n\n");
                }
            } while (cursor.moveToNext());
            tvMessages.setText(smsBuilder.toString());
            cursor.close();
        } else {
            tvMessages.setText("Không có tin nhắn nào.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            readSms();
        }
    }
}
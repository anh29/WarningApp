package com.example.warning;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.warning.UtilsService.UtilService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class RegisterActivity extends AppCompatActivity {
    private Button btnRegister;
    private TextView navLogin;
    private EditText txtName, txtPhone, txtPassword, txtAddress;
    ProgressBar progressBar;

    String name, address, phone, password;
    UtilService utilService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navLogin = findViewById(R.id.navLogin);
        txtName = findViewById(R.id.txtRegisName);
        txtAddress = findViewById(R.id.txtRegisAddress);
        txtPassword = findViewById(R.id.txtRegisPassword);
        txtPhone = findViewById(R.id.txtRegisPhone);
        utilService = new UtilService();

        btnRegister = findViewById(R.id.btnRegister);

        navLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilService.hideKeyboard(v, RegisterActivity.this);
                name = txtName.getText().toString();
                address = txtAddress.getText().toString();
                password = txtPassword.getText().toString();
                phone = txtPhone.getText().toString();

                if (validate(v)) {
                    RegisterActivity.RegisterUserTask userTask = new RegisterActivity.RegisterUserTask(name, address, phone, password);
                    userTask.execute();
                }
            }
        });
    }

    private class RegisterUserTask extends AsyncTask<Void, Void, JSONObject> {

        private String name, address, phone, password;

        public RegisterUserTask(String name, String address, String phone, String password) {
            this.name = name;
            this.address = address;
            this.phone = phone;
            this.password = password;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            // API call and response handling
            String url = "http://10.0.2.2:1713/users/register";

            // Log input data
            Log.d("RegisterUserTask", "Name: " + this.name);
            Log.d("RegisterUserTask", "Address: " + this.address);
            Log.d("RegisterUserTask", "Phone: " + this.phone);
            Log.d("RegisterUserTask", "Password: " + this.password);

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject()
                        .put("name", this.name)
                        .put("phone", this.phone)
                        .put("password", this.password)
                        .put("address", this.address);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), jsonObject.toString());

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            OkHttpClient client = new OkHttpClient();

            Call call = client.newCall(request);
            Log.e("RegisterUserTask", "Request: " + request);

            try {
                Response response = call.execute();
                Log.e("RegisterUserTask", "Response: " + response);

                // Check connection status
                if (response.isSuccessful()) {
                    // Get and parse JSON response
                    String serverResponse = response.body().string();
                    JSONObject jsonResponse = new JSONObject(serverResponse);
                    return jsonResponse;
                } else {
                    // Log error if request is not successful
                    Log.e("RegisterUserTask", "Request unsuccessful: " + response.message());
                    return null; // Handle failure
                }
            } catch (Exception e) {
                // Log exception
                e.printStackTrace();
                Log.e("RegisterUserTask", "Exception: " + e.getMessage());
                return null; // Handle failure
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if (result != null) {
                // Update UI based on successful registration
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
//                activity/\.finish(); // Close activity
            } else {
                // Handle registration failure
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validate(View view) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            utilService.showSnackBar(view, "Vui lòng nhập tên...");
            isValid = false;
        } else if (TextUtils.isEmpty(phone)) {
            utilService.showSnackBar(view, "Vui lòng nhập số điện thoại...");
            isValid = false;
        } else if (TextUtils.isEmpty(password)) {
            utilService.showSnackBar(view, "Vui lòng nhập mật khẩu...");
            isValid = false;
        } else if (TextUtils.isEmpty(address)) {
            utilService.showSnackBar(view, "Vui lòng nhập địa chỉ...");
            isValid = false;
        }

        return isValid;
    }
}
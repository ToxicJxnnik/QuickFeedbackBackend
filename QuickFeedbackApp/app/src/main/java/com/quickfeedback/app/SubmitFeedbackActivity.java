package com.quickfeedback.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.quickfeedback.app.api.FeedbackApiService;
import com.quickfeedback.app.api.RetrofitClient;
import com.quickfeedback.app.model.Feedback;
import com.quickfeedback.app.model.FeedbackRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitFeedbackActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilMessage;
    private TextInputEditText etName, etEmail, etMessage;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private FeedbackApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_feedback);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        apiService = RetrofitClient.getInstance().getApiService();

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilMessage = findViewById(R.id.tilMessage);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMessage = findViewById(R.id.etMessage);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        btnSubmit.setOnClickListener(v -> submitFeedback());
    }

    private boolean validate() {
        boolean valid = true;
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String message = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";

        tilName.setError(null);
        tilEmail.setError(null);
        tilMessage.setError(null);

        if (name.isEmpty()) {
            tilName.setError("Name is required");
            valid = false;
        } else if (name.length() > 100) {
            tilName.setError("Name must be 100 characters or less");
            valid = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            valid = false;
        } else if (email.length() > 100) {
            tilEmail.setError("Email must be 100 characters or less");
            valid = false;
        }

        if (message.isEmpty()) {
            tilMessage.setError("Message is required");
            valid = false;
        } else if (message.length() > 1000) {
            tilMessage.setError("Message must be 1000 characters or less");
            valid = false;
        }

        return valid;
    }

    private void submitFeedback() {
        if (!validate()) return;

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        btnSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        FeedbackRequest request = new FeedbackRequest(name, email, message);
        apiService.createFeedback(request).enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(SubmitFeedbackActivity.this,
                            "Feedback submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(SubmitFeedbackActivity.this,
                            "Failed to submit (HTTP " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(SubmitFeedbackActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

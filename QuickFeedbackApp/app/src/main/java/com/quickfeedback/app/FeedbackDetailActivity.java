package com.quickfeedback.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.quickfeedback.app.api.FeedbackApiService;
import com.quickfeedback.app.api.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackDetailActivity extends AppCompatActivity {

    private FeedbackApiService apiService;
    private int feedbackId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        apiService = RetrofitClient.getInstance().getApiService();
        progressBar = findViewById(R.id.progressBar);

        feedbackId = getIntent().getIntExtra("feedback_id", -1);
        String name = getIntent().getStringExtra("feedback_name");
        String email = getIntent().getStringExtra("feedback_email");
        String message = getIntent().getStringExtra("feedback_message");
        String date = getIntent().getStringExtra("feedback_date");

        ((TextView) findViewById(R.id.tvName)).setText(name);
        ((TextView) findViewById(R.id.tvEmail)).setText(email);
        ((TextView) findViewById(R.id.tvMessage)).setText(message);
        ((TextView) findViewById(R.id.tvDate)).setText(formatDate(date));

        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Feedback")
                .setMessage("Are you sure you want to delete this feedback entry?")
                .setPositiveButton("Delete", (dialog, which) -> deleteFeedback())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteFeedback() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.deleteFeedback(feedbackId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(FeedbackDetailActivity.this,
                            "Feedback deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(FeedbackDetailActivity.this,
                            "Failed to delete (HTTP " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FeedbackDetailActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private String formatDate(String isoDate) {
        if (isoDate == null) return "";
        String normalized = isoDate.endsWith("Z") ? isoDate : isoDate + "Z";
        String[] formats = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                "yyyy-MM-dd'T'HH:mm:ss.SSX",
                "yyyy-MM-dd'T'HH:mm:ss.SX",
                "yyyy-MM-dd'T'HH:mm:ssX"
        };
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy  HH:mm", Locale.US);
        for (String fmt : formats) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(fmt, Locale.US);
                inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(normalized);
                if (date != null) return outputFormat.format(date);
            } catch (ParseException ignored) {
            }
        }
        return isoDate;
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

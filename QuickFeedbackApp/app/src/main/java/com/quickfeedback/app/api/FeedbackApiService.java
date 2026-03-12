package com.quickfeedback.app.api;

import com.quickfeedback.app.model.Feedback;
import com.quickfeedback.app.model.FeedbackRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FeedbackApiService {

    @GET("api/feedback")
    Call<List<Feedback>> getAllFeedback();

    @GET("api/feedback/{id}")
    Call<Feedback> getFeedbackById(@Path("id") int id);

    @POST("api/feedback")
    Call<Feedback> createFeedback(@Body FeedbackRequest request);

    @DELETE("api/feedback/{id}")
    Call<Void> deleteFeedback(@Path("id") int id);
}

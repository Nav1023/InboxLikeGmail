package com.example.soc_macmini_15.inboxlikegmail.Network;

import com.example.soc_macmini_15.inboxlikegmail.Model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("inbox.json")
    Call<List<Message>> getInbox();
    }

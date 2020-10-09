package com.aravi.youtubely;

import com.aravi.youtubely.model.SearchList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class API {

    public static final String url = "https://youtube-ly.herokuapp.com/";

    public static APIservice apIservice = null;

    public static APIservice getService() {
        if (apIservice == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apIservice = retrofit.create(APIservice.class);
        }
        return apIservice;
    }


    public interface APIservice {
        @GET
        Call<SearchList> getSearchResult(@Url String url);
    }

}

package com.example.adeen_s.quiz;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by adeen-s on 10/8/17.
 */

public class QuestionLoader extends AsyncTaskLoader<String> {

    private String requestUrl;

    public QuestionLoader(Context context, String url) {
        super(context);
        requestUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (requestUrl.length() < 1) {
            return null;
        }
        String jsonResponse = QueryUtils.getJSONResponse(requestUrl);
        if (jsonResponse == null) {
            return null;
        }
        return jsonResponse;
    }
}
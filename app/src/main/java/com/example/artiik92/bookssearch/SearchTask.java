package com.example.artiik92.bookssearch;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import com.google.common.primitives.Ints;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by artiik92 on 12.05.2018.
 */

public class SearchTask extends AsyncTask<String, Void, List<Volume>> {

    private SearchListener searchListener;

    public void setSearchListener(SearchListener searchListener) {
        this.searchListener = searchListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        searchListener.onSearching();
    }

    @Override
    protected List<Volume> doInBackground(String... params) {

        String query = params[0];


        if (Ints.tryParse(query) != null && (query.length() == 13 || query.length() == 10)) {
            query = query.concat("+isbn:" + query);
        }


        Books books = new Books.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory.getDefaultInstance(), null)
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build();

        try {
            Books.Volumes.List list = books.volumes().list(query).setProjection("LITE");
            return list.execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    protected void onPostExecute(List<Volume> volumes) {
        super.onPostExecute(volumes);
        searchListener.onResult(volumes == null ? Collections.<Volume>emptyList() : volumes);
    }

    public interface SearchListener {
        void onSearching();

        void onResult(List<Volume> volumes);
    }

}

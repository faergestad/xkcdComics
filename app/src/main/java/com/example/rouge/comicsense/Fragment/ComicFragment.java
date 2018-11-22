package com.example.rouge.comicsense.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rouge.comicsense.Adapter.ComicAdapter;
import com.example.rouge.comicsense.Model.Comic;
import com.example.rouge.comicsense.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComicFragment extends Fragment implements SearchView.OnQueryTextListener{

    private final String TAG = "ComicFragment";
    private RequestQueue requestQueue;

    private List<Comic> comicList;
    private List<Comic> result;
    private List<Comic> reset;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager linearLayoutManager;

    public ComicFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);

        comicList = new ArrayList<>();
        adapter = new ComicAdapter(getContext(), comicList);

        requestQueue = Volley.newRequestQueue(getActivity());

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        reset = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        String startUrl = "https://xkcd.com/";
        String endUrl = "/info.0.json";
        int nr = 2075;

        while(nr > 0) {
            getComics(startUrl + nr + endUrl);
            nr--;
        }

        return view;
    }

    public void getComics(String s) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, s, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Comic comic = new Comic();
                        comic.setMonth(response.optString("month"));
                        comic.setNum(response.optInt("num"));
                        comic.setLink(response.optString("link"));
                        comic.setLink(response.optString("year"));
                        comic.setNews(response.optString("news"));
                        comic.setSafeTitle(response.optString("safe_title"));
                        comic.setTranscript(response.optString("transcript"));
                        comic.setAlt(response.optString("alt"));
                        comic.setImg(response.optString("img"));
                        comic.setTitle(response.optString("title"));
                        comic.setDay(response.optString("day"));

                        comicList.add(comic);
                        reset.add(comic);

                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("volley", error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        // Could do something with this, but I like the search to evolve as I'm typing
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        result = new ArrayList<>();

        for(Comic comic : reset)
            if (comic.getTitle().toLowerCase().contains(query) || String.valueOf(comic.getNum()).equals(query)) {
                result.add(comic);
            }

        comicList.clear();
        comicList.addAll(result);

        if(query.equals("")) {
            comicList.addAll(reset);
        }
        adapter.notifyDataSetChanged();

        return false;
    }

}
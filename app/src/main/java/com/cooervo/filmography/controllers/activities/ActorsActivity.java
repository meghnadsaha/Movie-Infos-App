package com.cooervo.filmography.controllers.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.cooervo.filmography.R;
import com.cooervo.filmography.controllers.adapters.ActorsAdapter;
import com.cooervo.filmography.models.Actor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of actors/actresses related by the query we received in
 * MainActivity onclick of actor it displays all films by such actor
 */

public class ActorsActivity extends AppCompatActivity {

    private static final String TAG = ActorsActivity.class.getSimpleName();

    private List<Actor> actors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actors);

        Intent intent = getIntent();

        if (intent != null) {

            String jsonData = intent.getExtras().getString("jsonData");
            getActorsFrom(jsonData);
        }

        populateRecyclerView();

    }

    private void getActorsFrom(String rawJSON) {
        JSONObject results = null;

        try {
            results = new JSONObject(rawJSON);
            JSONArray data = results.getJSONArray("results");

            int dataSize = data.length();

            if (dataSize == 0) {
                showNotFoundNotification();
                super.onBackPressed();
            }

            actors = new ArrayList<>(dataSize);

            for (int i = 0; i < dataSize; i++) {
                JSONObject jsonActor = data.getJSONObject(i);

                Actor tempActor = new Actor();
                tempActor.setId(jsonActor.getInt("id"));
                tempActor.setName(jsonActor.getString("name"));
                tempActor.setPicturePath(jsonActor.getString("profile_path"));

                JSONArray tempArray = jsonActor.getJSONArray("known_for");
                JSONObject knownFor = tempArray.getJSONObject(0);

                if (knownFor.getString("media_type").equals("movie")) {
                    tempActor.setKnownForFilm(knownFor.getString("original_title"));

                }

                actors.add(tempActor);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    private void populateRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.actorsRecyclerView);
        ActorsAdapter adapter = new ActorsAdapter(ActorsActivity.this, actors);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ActorsActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
    }

    private void showNotFoundNotification() {
        Toast.makeText(this,
                "Sorry we couldn't find anything, please try again",
                Toast.LENGTH_SHORT).
                show();
    }
}

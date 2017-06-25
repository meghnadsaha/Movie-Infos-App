package com.cooervo.filmography.controllers.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cooervo.filmography.controllers.adapters.FilmsAdapter;
import com.cooervo.filmography.R;
import com.cooervo.filmography.controllers.transformation.RoundedTransformation;
import com.cooervo.filmography.models.Film;
import com.cooervo.filmography.models.FilmComparator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Child activity of ActorsActivity which uses a RecyclerView to
 * display as a list and sorted by date the complete
 * films by actor/actress.
 */
public class FilmsActivity extends AppCompatActivity {

    public static final String TAG = FilmsActivity.class.getSimpleName();

    //The views binded with library Butterknife
    @BindView(R.id.actorNameLabel)TextView actorLabel;
    @BindView(R.id.actorProfileImage)ImageView actorPicture;
    @BindView(R.id.filmsRecyclerView)RecyclerView filmsRecyclerView;

    //List to load the films by actor
    private List<Film> films;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null) {

            String jsonData = intent.getExtras().getString("jsonData");

            getFilmsFrom(jsonData);
        }

        populateRecyclerView();
        setTypeFaces();
    }

    private void getFilmsFrom(String jsonData) {

        JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(jsonData);

            setActorNameAndPicture(jsonResponse);

            JSONObject credits = jsonResponse.getJSONObject("credits");
            JSONArray cast = credits.getJSONArray("cast");

            int dataSize = cast.length();

            if (dataSize == 0) {
                showNotFoundNotification();
            }

            films = new ArrayList<>(dataSize);

            for (int i = 0; i < dataSize; i++) {

                JSONObject jsonFilm = cast.getJSONObject(i);

                Film film = new Film();

                String dateString = jsonFilm.getString("release_date");

                if (dateString != null && !dateString.equals("null")) {

                    film.setTitle(jsonFilm.getString("title"));
                    film.setPosterPath(jsonFilm.getString("poster_path"));
                    film.setRole(jsonFilm.getString("character"));
                    film.setFormattedDate(dateString);

                    films.add(film);
                }
            }

            Collections.sort(films, new FilmComparator());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setActorNameAndPicture(JSONObject jsonResponse) throws JSONException {
        actorLabel.setText(jsonResponse.getString("name"));
        Picasso.with(this)
                .load("https://image.tmdb.org/t/p/w185" + jsonResponse.getString("profile_path"))
                .transform(new RoundedTransformation(20, 5))
                .error(R.drawable.noprofile)
                .into(actorPicture);
    }


    private void populateRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.filmsRecyclerView);
        FilmsAdapter adapter = new FilmsAdapter(FilmsActivity.this, films);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FilmsActivity.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
    }

    private void setTypeFaces() {
        Typeface latoBlack = Typeface.createFromAsset(getAssets(), "fonts/Lato-Black.ttf");
        actorLabel.setTypeface(latoBlack);

    }

    private void showNotFoundNotification() {
        Toast.makeText(this,
                "Sorry we couldn't find anything, please try again",
                Toast.LENGTH_SHORT).
                show();
        finish();
    }

}


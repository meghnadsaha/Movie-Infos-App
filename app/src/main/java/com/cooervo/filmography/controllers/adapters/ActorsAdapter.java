package com.cooervo.filmography.controllers.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooervo.filmography.R;
import com.cooervo.filmography.controllers.activities.FilmsActivity;
import com.cooervo.filmography.controllers.http.AsyncDownloader;
import com.cooervo.filmography.controllers.http.MovieDbUrl;
import com.cooervo.filmography.controllers.transformation.RoundedTransformation;
import com.cooervo.filmography.models.Actor;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Create the basic adapter extending from RecyclerView.Adapter
 * note that we specify the custom ViewHolder which gives us access to our
 */
public class ActorsAdapter extends RecyclerView.Adapter<ActorsAdapter.ViewHolder> {

    private static List<Actor> actors;
    private Context context;

    public ActorsAdapter(Context ctx, List<Actor> actorsList) {
        context = ctx;
        actors = actorsList;
    }

    /**
     * Provides a direct reference to each of the views within a data item
     * Used to cache the views within the item layout for fast access
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView actorName;
        public TextView knownFor;
        public ImageView posterImage;


        public ViewHolder(View itemView) {
            super(itemView);
            actorName = (TextView) itemView.findViewById(R.id.relatedActorsName);
            knownFor = (TextView) itemView.findViewById(R.id.knownFor);
            posterImage = (ImageView) itemView.findViewById(R.id.actorPic);

            itemView.setOnClickListener(this); //this is necessary to setOnClickListener of RecyclerView of Hourly Button

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            int actorUniqueId = actors.get(position).getId();

            MovieDbUrl url = MovieDbUrl.getInstance();
            String getFilmographyHttpMethod = url.getFilmographyQuery(actorUniqueId);

            AsyncDownloader downloader = new AsyncDownloader(context, new FilmsActivity().getClass());
            downloader.execute(getFilmographyHttpMethod);

        }
    }

    //This method inflates a layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.actors_list_item,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    // Populates data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Actor actor = actors.get(position);

        holder.actorName.setText(actor.getName());
        holder.knownFor.setText(actor.getKnownForFilm());
        Picasso.with(context)
                .load("https://image.tmdb.org/t/p/w185" + actor.getPicturePath())
                .transform(new RoundedTransformation(20, 5))
                .error(R.drawable.noprofile)
                .into(holder.posterImage);
    }


    @Override
    public int getItemCount() {
        if (actors == null) {
            return 0;
        }
        return actors.size();
    }


}

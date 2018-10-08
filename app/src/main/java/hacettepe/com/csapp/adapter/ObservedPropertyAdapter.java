package hacettepe.com.csapp.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hacettepe.com.csapp.R;
import hacettepe.com.csapp.model.ObservedProperty;

public class ObservedPropertyAdapter extends RecyclerView.Adapter<ObservedPropertyAdapter.MyViewHolder> {

    private List<ObservedProperty> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_property_name, year, genre;

        public MyViewHolder(View view) {
            super(view);
            tv_property_name = (TextView) view.findViewById(R.id.tv_property_name);

        }
    }


    public ObservedPropertyAdapter(List<ObservedProperty> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.observed_property_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ObservedProperty movie = moviesList.get(position);
        holder.tv_property_name.setText(movie.getName());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}

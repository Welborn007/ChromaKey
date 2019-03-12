package in.kesari.chromakey;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private int[] dataSet;
    int selected_position = -1;
    Activity activity;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewIcon;
        TextView textViewName;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
        }
    }

    public CustomAdapter(int[] data, Activity activity) {
        this.dataSet = data;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        if (selected_position == listPosition) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(activity, R.color.bluegray100));
            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
            }
        }
        ((MyViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    notifyItemChanged(selected_position);
                    selected_position = listPosition;
                    notifyItemChanged(selected_position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TextView textViewName = holder.textViewName;
        ImageView imageView = holder.imageViewIcon;

        imageView.setImageResource(dataSet[listPosition]);
        //Picasso.get().load(dataSet.get(listPosition).getId()).into(imageView);
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }
}

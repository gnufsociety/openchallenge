package com.gnufsociety.openchallenge.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.R;
import com.gnufsociety.openchallenge.WinnerActivity;
import com.gnufsociety.openchallenge.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Leonardo on 15/02/2017.
 */

public class WinnerAdapter extends RecyclerView.Adapter<WinnerAdapter.WinnerHolder> {

    public List<User> users;
    FirebaseStorage storage;
    StorageReference sref;
    public WinnerActivity winnerActivity;
    public int choosen = 0;

    public WinnerAdapter(List<User> users, WinnerActivity winnerActivity){
        for (User user : users) {
            user.bronzeMedals = 0;
            user.silverMedals = 0;
            user.goldMedals = 0;
        }
        this.users = users;
        storage = FirebaseStorage.getInstance();
        sref = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        this.winnerActivity = winnerActivity;
    }


    @Override
    public WinnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.winner_row,parent,false);

        return new WinnerHolder(view);
    }

    @Override
    public void onBindViewHolder(WinnerHolder holder, int position) {
        User u = users.get(position);
        StorageReference userPic = sref.child("users/"+u.proPicLocation);

        Glide.with(holder.civ.getContext())
                .using(new FirebaseImageLoader())
                .load(userPic)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.civ);

        holder.user.setText(u.name);

        if (u.bronzeMedals != 0)
            holder.medal.setImageResource(R.drawable.bronze128);
        else if (u.silverMedals != 0)
            holder.medal.setImageResource(R.drawable.silver128);
        else if (u.goldMedals != 0)
            holder.medal.setImageResource(R.drawable.gold128);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }



    public class WinnerHolder extends RecyclerView.ViewHolder {
        public CircleImageView civ;
        public TextView user;
        public ImageView medal;
        public WinnerHolder(final View itemView) {
            super(itemView);
            civ = (CircleImageView) itemView.findViewById(R.id.winner_row_image);
            user = (TextView) itemView.findViewById(R.id.winner_row_user);
            medal = (ImageView) itemView.findViewById(R.id.winner_row_medal);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User curr = users.get(getAdapterPosition());
                    switch (choosen){
                        case 0:
                            curr.goldMedals++;
                            medal.setImageResource(R.drawable.gold128);
                            winnerActivity.winners[0] = curr;
                            Toast.makeText(v.getContext(), R.string.choose_second,Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            curr.silverMedals++;
                            winnerActivity.winners[1] = curr;
                            medal.setImageResource(R.drawable.silver128);
                            Toast.makeText(v.getContext(), R.string.choose_third,Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            curr.bronzeMedals++;
                            winnerActivity.winners[2] = curr;
                            medal.setImageResource(R.drawable.bronze128);
                            Toast.makeText(v.getContext(), R.string.click_button,Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(v.getContext(), R.string.said_click_button,Toast.LENGTH_SHORT).show();
                    }
                    choosen++;
                }
            });
        }
    }
}

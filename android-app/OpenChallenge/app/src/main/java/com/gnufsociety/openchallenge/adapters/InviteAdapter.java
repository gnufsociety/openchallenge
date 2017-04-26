package com.gnufsociety.openchallenge.adapters;

import android.os.AsyncTask;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.gnufsociety.openchallenge.ApiHelper;
import com.gnufsociety.openchallenge.R;
import com.gnufsociety.openchallenge.model.Challenge;
import com.gnufsociety.openchallenge.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by spallas on 25/04/17.
 *
 */

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.InviteHolder> {

    public ArrayList<User> users;
    public Challenge challenge;

    public InviteAdapter(ArrayList<User> users, Challenge challenge) {
        this.users = users;
        this.challenge = challenge;
    }

    @Override
    public InviteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.invite_row, parent, false);
        return new InviteAdapter.InviteHolder(view);
    }

    @Override
    public void onBindViewHolder(InviteHolder holder, int position) {
        User u = users.get(position);
        holder.user_id = u.id;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://openchallenge-81990.appspot.com");
        StorageReference userPic = storageRef.child("users/" + u.proPicLocation);
        holder.civ.setImageResource(u.resPic);

        Glide.with(holder.civ.getContext())
                .using(new FirebaseImageLoader())
                .load(userPic)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.civ);

        holder.username.setText(u.name);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class InviteHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.invite_img) CircleImageView civ;
        @BindView(R.id.invite_username) TextView username;
        @BindView(R.id.invite_btn) AppCompatButton inviteBtn;

        public String user_id;

        public InviteHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            inviteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AsyncTask<Void,Void,Void> inviteTask = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            new ApiHelper().invtite(challenge.id, user_id);
                            return null;
                        }
                    };
                    inviteTask.execute();
                }
            });
        }




    }
}

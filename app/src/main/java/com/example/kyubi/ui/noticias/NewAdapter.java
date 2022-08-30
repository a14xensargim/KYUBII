package com.example.kyubi.ui.noticias;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyubi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class NewAdapter extends RecyclerView.Adapter<NewAdapter.WordViewHolder>{

    private static final int IO_BUFFER_SIZE = 4;
    private final ArrayList<News> mWordList;
    private LayoutInflater mInflater;
    private static int pos;
    private Context context;
    public String botonPN = null;

    FirebaseUser user;
    com.example.kyubi.ui.noticias.NewsFragment newsFragment;
    StorageReference storage;

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView titol, descripcio;
        final NewAdapter mAdapter;
        public final ImageView imatge;
        Button delete;

        public WordViewHolder(View itemView, NewAdapter adapter) {
            super(itemView);
            titol = itemView.findViewById(R.id.article_title);
            descripcio = itemView.findViewById(R.id.article_description);
            imatge = itemView.findViewById(R.id.featured_image);
            delete = itemView.findViewById(R.id.borrarNew);

            user = FirebaseAuth.getInstance().getCurrentUser();

            if(user != null && (user.getEmail().equals("a14xensargim@inspedralbes.cat") ||
                    user.getEmail().equals("xeniaippon@hotmail.es")))
                delete.setVisibility(View.VISIBLE);
            else
                delete.setVisibility(View.INVISIBLE);

            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.
            pos = getLayoutPosition();
            // Use that to access the affected item in mWordList.
            News element = mWordList.get(pos);
            // Change the word in the mWordList.
            //mWordList.set(mPosition, "Clicked! " + element);
            // Notify the adapter that the data has changed so it can
            // update the RecyclerView to display the data.
            mAdapter.notifyDataSetChanged();
        }
    }

    public int getPos(){    return pos;    };

    public NewAdapter(Context context, ArrayList<News> wordList) {
        mInflater = LayoutInflater.from(context);
        this.mWordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.show_new_layout, parent, false);
        context = parent.getContext();
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        News mCurrent = mWordList.get(position);
        holder.titol.setText(mCurrent.getTITLE());
        System.out.println("TITOL --> " + mCurrent.getTITLE());
        holder.descripcio.setText(mCurrent.getDESCRIPTION());
        System.out.println("DESCRIPCIO --> " + mCurrent.getDESCRIPTION());
        new DownloadImageTask((ImageView) holder.imatge).execute(mCurrent.getURL());

        System.out.println("IMATGE --> " + mCurrent.getURL());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BD bd = new BD();

                System.out.println("HOLAAAAAAAAAAAAAAAAAAAAAAAAAAa");

                bd.DeleteEvent(mCurrent.getTITLE(), this::onNewsDeleted);

            }

            private void onNewsDeleted(Boolean esborrat) {
                if(esborrat) {
                    //Intent intent = new Intent(newsFragment.getActivity(), NewsFragment.class);
                    //newsFragment.getContext().startActivity(intent);
                }

                else
                    Toast.makeText(newsFragment.getActivity(), "No se ha podido borrar la noticia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public int getItemCount() {
        return mWordList.size();
    }
}

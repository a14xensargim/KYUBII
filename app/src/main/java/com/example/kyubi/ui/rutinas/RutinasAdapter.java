package com.example.kyubi.ui.rutinas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyubi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class RutinasAdapter extends RecyclerView.Adapter<RutinasAdapter.WordViewHolder>{

    private static final int IO_BUFFER_SIZE = 4;
    private final ArrayList<Rutinas> mWordList;
    private LayoutInflater mInflater;
    private static int pos;
    private Context context;
    public String botonPN = null;
    public String id;

    FirebaseUser user;
    RutinasFragment rutinasFragment;
    StorageReference storage;

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView fecha, nombre, apellido, correo;
        public final ImageView imatge;
        final RutinasAdapter mAdapter;
        Button delete;

        public WordViewHolder(View itemView, RutinasAdapter adapter) {
            super(itemView);
            fecha = itemView.findViewById(R.id.fecha);
            nombre = itemView.findViewById(R.id.nombre);
            apellido = itemView.findViewById(R.id.apellido);
            correo = itemView.findViewById(R.id.correo);
            imatge = itemView.findViewById(R.id.featured_image);
            delete = itemView.findViewById(R.id.borrarRutina);

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
            Rutinas element = mWordList.get(pos);
            // Change the word in the mWordList.
            //mWordList.set(mPosition, "Clicked! " + element);
            // Notify the adapter that the data has changed so it can
            // update the RecyclerView to display the data.
            mAdapter.notifyDataSetChanged();
        }
    }

    public int getPos(){    return pos;    };

    public RutinasAdapter(Context context, ArrayList<Rutinas> wordList) {
        mInflater = LayoutInflater.from(context);
        this.mWordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.show_rutina_layout, parent, false);
        context = parent.getContext();
        return new WordViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        Rutinas mCurrent = mWordList.get(position);
        holder.fecha.setText(mCurrent.getFECHA());
        System.out.println("FECHA --> " + mCurrent.getFECHA());
        holder.nombre.setText(mCurrent.getNOMBRE());
        System.out.println("NOMBRE --> " + mCurrent.getNOMBRE());
        holder.apellido.setText(mCurrent.getAPELLIDO());
        System.out.println("APELLIDO --> " + mCurrent.getAPELLIDO());
        holder.correo.setText(mCurrent.getCORREO());
        System.out.println(mCurrent.getCORREO());
        new DownloadImageTask((ImageView) holder.imatge).execute(mCurrent.getIMATGE());

        System.out.println("IMATGE --> " + mCurrent.getIMATGE());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BD bd = new BD();

                System.out.println("HOLAAAAAAAAAAAAAAAAAAAAAAAAAAa");

                id = mCurrent.getFECHA() + mCurrent.getCORREO();

                bd.DeleteEvent(mCurrent.id, this::onNewsDeleted);
            }

            private void onNewsDeleted(Boolean esborrat) {
                if(esborrat) {
                    //Intent intent = new Intent(newsFragment.getActivity(), NewsFragment.class);
                    //newsFragment.getContext().startActivity(intent);
                }

                else
                    Toast.makeText(rutinasFragment.getActivity(), "No se ha podido borrar la rutina", Toast.LENGTH_SHORT).show();
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

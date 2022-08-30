package com.example.kyubi.ui.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.util.ArrayList;

public class Adapter2 extends BaseAdapter {

    private Context context;
    private ArrayList<String> images;

    public Adapter2(Context c, ArrayList<String> images) {
        context = c;
        this.images = images;
    }


    public int getCount() {
        return images.size();
    }


    public Object getItem(int position) {
        return position;
    }


    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        new DownloadImageTask(imageView).execute(images.get(position));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null && (user.getEmail().equals("a14xensargim@inspedralbes.cat") ||
                user.getEmail().equals("xeniaippon@hotmail.es"))){
            imageView.setLayoutParams(new Gallery.LayoutParams(400, 400));
        }else{
            imageView.setLayoutParams(new Gallery.LayoutParams(500, 500));
        }
        return imageView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
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
}
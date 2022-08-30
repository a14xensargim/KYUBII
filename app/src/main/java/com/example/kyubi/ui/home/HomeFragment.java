package com.example.kyubi.ui.home;

import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.kyubi.R;
import com.example.kyubi.databinding.FragmentInicioBinding;
import com.example.kyubi.ui.gallery.BDGall;
import com.example.kyubi.ui.noticias.BD;
import com.example.kyubi.ui.noticias.News;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentInicioBinding binding;
    Display display;

    CarouselView carouselView;
    TextView bienv;
    ArrayList<String> images = new ArrayList<>();

    ImageView imatgen1;
    ImageView imatgen2;
    ImageView imatgen3;
    ImageView imatgen4;
    ImageView imatgen5;

    TextView titoln1;
    TextView descripcion1;

    TextView titoln2;
    TextView titoln3;
    TextView titoln4;
    TextView titoln5;

    ArrayList<News> noticies = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        bienv = binding.bienv;

        imatgen1 = binding.foton1;
        imatgen2 = binding.foton2;
        imatgen3 = binding.foton3;
        imatgen4 = binding.foton4;
        imatgen5 = binding.foton5;

        titoln1 = binding.titoln1;
        titoln2 = binding.titoln2;
        titoln3 = binding.titoln3;
        titoln4 = binding.titoln4;
        titoln5 = binding.titoln5;

        descripcion1 = binding.descripcionn1;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
            bienv.setText(getString(R.string.Benv) + user.getEmail());

        cargarImagenes();
        CollectNews();

        return root;
    }

    ImageListener imageListener = (position, imageView) -> Glide.with(getActivity()).load(images.get(position)).into(imageView);

    private void cargarImagenes(){
        BDGall db = new BDGall();

        db.ReadPhotos(this::onPhotosReceived);
    }


    private void onPhotosReceived(ArrayList<String> fotos) {
        images.clear();
        images.addAll(fotos);

        carouselView = binding.carouselView;
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(images.size());
    }

    private void CollectNews(){
        BD bd = new BD();

        bd.ReadNews(this::onNewsReceived);
    }


    private void onNewsReceived(List<News> news) {
        noticies.clear();
        noticies.addAll(news);

        Glide.with(getActivity()).load(noticies.get(noticies.size()-1).getURL()).into(imatgen1);
        titoln1.setText(noticies.get(noticies.size()-1).getTITLE());
        descripcion1.setText(noticies.get(noticies.size()-1).getDESCRIPTION());

        Glide.with(getActivity()).load(noticies.get(noticies.size()-2).getURL()).into(imatgen2);
        titoln2.setText(noticies.get(noticies.size()-2).getTITLE());

        Glide.with(getActivity()).load(noticies.get(noticies.size()-3).getURL()).into(imatgen3);
        titoln3.setText(noticies.get(noticies.size()-3).getTITLE());

        Glide.with(getActivity()).load(noticies.get(noticies.size()-4).getURL()).into(imatgen4);
        titoln4.setText(noticies.get(noticies.size()-4).getTITLE());

        Glide.with(getActivity()).load(noticies.get(noticies.size()-5).getURL()).into(imatgen5);
        titoln5.setText(noticies.get(noticies.size()-5).getTITLE());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
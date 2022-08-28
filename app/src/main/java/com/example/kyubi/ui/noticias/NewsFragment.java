package com.example.kyubi.ui.noticias;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyubi.AfegirNew;
import com.example.kyubi.R;
import com.example.kyubi.databinding.FragmentNoticiasBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private NewsViewModel newsViewModel;
    private FragmentNoticiasBinding binding;

    private ArrayList<News> llista = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private NewAdapter mAdapter;
    private ProgressBar progressBar;

    private Button afegir;
    FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNoticiasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        progressBar = binding.progressBar2;

        afegir = binding.afegirnew;

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null && (user.getEmail().equals("a14xensargim@inspedralbes.cat") ||
                user.getEmail().equals("xeniaippon@hotmail.es"))){
            afegir.setVisibility(View.VISIBLE);
        }else{
            afegir.setVisibility(View.INVISIBLE);
        }

        CollectNews();

        afegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AfegirNew.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void CollectNews(){
        BD bd = new BD();

        bd.ReadNews(this::onNewsReceived);
    }


    private void onNewsReceived(List<News> news) {
        llista.clear();
        llista.addAll(news);

        // Get a handle to the RecyclerView.
        mRecyclerView = getActivity().findViewById(R.id.list);
        // Create an adapter and supply the data to be displayed.
        mAdapter = new NewAdapter(getContext(), llista);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility(View.GONE);
    }
}
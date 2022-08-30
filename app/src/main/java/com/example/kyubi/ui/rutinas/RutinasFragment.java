package com.example.kyubi.ui.rutinas;

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
import com.example.kyubi.AfegirRutina;
import com.example.kyubi.R;
import com.example.kyubi.databinding.FragmentNoticiasBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class RutinasFragment extends Fragment {

    private FragmentNoticiasBinding binding;

    private ArrayList<Rutinas> llista = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RutinasAdapter mAdapter;
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

        CollectRutinas();

        afegir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AfegirRutina.class);
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

    private void CollectRutinas(){
        BD bd = new BD();

        bd.ReadRutinas(this::onRutinasReceived);
    }


    private void onRutinasReceived(List<Rutinas> rutinas) {
        llista.clear();
        llista.addAll(rutinas);

        // Get a handle to the RecyclerView.
        mRecyclerView = getActivity().findViewById(R.id.list);
        // Create an adapter and supply the data to be displayed.
        mAdapter = new RutinasAdapter(getContext(), llista);
        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility(View.GONE);
    }
}
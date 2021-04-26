package com.holzhausen.rpgworld.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.adapter.BasicInfoElementAdapter;
import com.holzhausen.rpgworld.viewmodel.CharacterCreationViewModel;


public class BasicInfoFragment extends Fragment {


    private CharacterCreationViewModel viewModel;

    private RecyclerView recyclerView;


    public BasicInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(CharacterCreationViewModel.class);
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);
        recyclerView = view.findViewById(R.id.basicInfoRecyclerView);
        BasicInfoElementAdapter adapter = new BasicInfoElementAdapter(viewModel
                .getBasicTraitInfoSubjectOFlowable(), getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
}
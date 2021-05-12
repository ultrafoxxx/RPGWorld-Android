package com.holzhausen.rpgworld.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.adapter.QuestElementAdapter;
import com.holzhausen.rpgworld.viewmodel.GameViewModel;


public class QuestFragment extends Fragment {

    private RecyclerView recyclerView;

    private GameViewModel gameViewModel;


    public QuestFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quest, container, false);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        recyclerView = view.findViewById(R.id.questsRecyclerView);
        QuestElementAdapter adapter = new QuestElementAdapter(getContext(), gameViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }
}
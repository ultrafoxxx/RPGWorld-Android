package com.holzhausen.rpgworld.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.adapter.BasicInfoElementAdapter;
import com.holzhausen.rpgworld.viewmodel.CharacterCreationViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class BasicInfoFragment extends Fragment {


    private CharacterCreationViewModel viewModel;

    private RecyclerView recyclerView;

    private TextView chooseInfo;

    private CompositeDisposable compositeDisposable;


    public BasicInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        compositeDisposable = new CompositeDisposable();
        viewModel = new ViewModelProvider(requireActivity()).get(CharacterCreationViewModel.class);
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);
        recyclerView = view.findViewById(R.id.basicInfoRecyclerView);
        BasicInfoElementAdapter adapter = new BasicInfoElementAdapter(viewModel, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chooseInfo = view.findViewById(R.id.chooseInfo);
        Disposable disposable = viewModel
                .getTitleSubjectFlowable()
                .subscribe(title -> chooseInfo.setText(title));
        compositeDisposable.add(disposable);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
package com.holzhausen.rpgworld.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.adapter.BasicInfoElementAdapter;
import com.holzhausen.rpgworld.adapter.SkillElementAdapter;
import com.holzhausen.rpgworld.model.SkillFragmentInfo;
import com.holzhausen.rpgworld.viewmodel.CharacterCreationViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class SkillFragment extends Fragment {


    private CharacterCreationViewModel viewModel;

    private RecyclerView recyclerView;

    private TextView skillsToAllocate;

    private Disposable disposable;

    private Button confirmButton;

    private TextInputLayout textInputLayout;

    private int remainingSkill;

    private SkillElementAdapter adapter;

    public SkillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skill, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(CharacterCreationViewModel.class);
        recyclerView = view.findViewById(R.id.skillRecyclerView);
        adapter = new SkillElementAdapter(getContext(), viewModel, this::updateRemainingSkill);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(this::onCharacterCreated);
        textInputLayout = view.findViewById(R.id.nameTextField);

        skillsToAllocate = view.findViewById(R.id.skills_left);

        disposable = viewModel
                .getSkillListFlowable()
                .subscribe(skills -> {
                    remainingSkill = 50 * skills.getSkills().size();
                    skillsToAllocate.setText(String.valueOf(remainingSkill));
                });

        return view;
    }

    private void onCharacterCreated(View view) {
        String name = textInputLayout.getEditText().getText().toString();
        if(name == null || name.length() <=5){
            Toast.makeText(getContext(), getString(R.string.character_info), Toast.LENGTH_SHORT).show();
            return;
        }
        SkillFragmentInfo skillFragmentInfo = new SkillFragmentInfo(name, adapter.getSkills());
        viewModel.updateSkills(skillFragmentInfo);
    }

    private boolean updateRemainingSkill(int lastProgress, int newProgress){
        int difference = newProgress - lastProgress;
        if(remainingSkill < difference) {
            return false;
        }
        remainingSkill -= difference;
        skillsToAllocate.setText(String.valueOf(remainingSkill));
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
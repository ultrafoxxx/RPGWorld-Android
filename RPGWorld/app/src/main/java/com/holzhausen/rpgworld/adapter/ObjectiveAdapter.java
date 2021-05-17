package com.holzhausen.rpgworld.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.model.Message;
import com.holzhausen.rpgworld.util.ObjectiveAdapterHelper;
import com.holzhausen.rpgworld.viewmodel.GameViewModel;
import com.holzhausen.rpgworld.viewmodel.ObjectiveViewModel;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class ObjectiveAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Message> messages;

    private final Context context;

    private final ObjectiveAdapterHelper helper;

    private final Disposable disposable;

    private final static int PLAYER_VIEW_TYPE = 1;

    private final static int NPC_VIEW_TYPE = 2;

    public ObjectiveAdapter(Context context, ObjectiveViewModel viewModel, ObjectiveAdapterHelper helper) {
        this.context = context;
        this.helper = helper;
        messages = new LinkedList<>();
        if(helper.isTalkEvent()){
            Message message = new Message();
            message.setContent(Html.fromHtml("...", Html.FROM_HTML_MODE_COMPACT));
            message.setPlayer(true);
            message.setColor(R.color.black);
            message.setDisabled(true);
            messages.add(message);
        }
        disposable = viewModel.getMessageFlowable().subscribe(message -> {
            messages.remove(0);
            messages = message;
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == PLAYER_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.right_dialog_element, parent, false);
            return new PlayerViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.left_dialog_element, parent, false);
            return new NPCViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView textView;
        if(holder instanceof PlayerViewHolder){
            textView = ((PlayerViewHolder)holder).getRightDialog();
        }
        else {
            textView = ((NPCViewHolder)holder).getLeftDialog();
        }
        textView.setText(messages.get(position).getContent());
        textView.setTextColor(messages.get(position).getColor());
        if(messages.get(position).isLast()){
            textView.setOnClickListener(view -> helper.endActivity());
        }
        else if(messages.get(position).isPlayer() && !messages.get(position).isDisabled()){
            textView.setOnClickListener(view -> {
                messages.addAll(helper.getNextMessages(messages.get(position)));
                notifyDataSetChanged();
                helper.scrollToPosition(messages.size()-1);
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isPlayer() ? PLAYER_VIEW_TYPE : NPC_VIEW_TYPE;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        disposable.dispose();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {

        private final TextView rightDialog;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            rightDialog = itemView.findViewById((R.id.right_dialog));
        }

        public TextView getRightDialog() {
            return rightDialog;
        }
    }

    public static class NPCViewHolder extends RecyclerView.ViewHolder {

        private final TextView leftDialog;

        public NPCViewHolder(@NonNull View itemView) {
            super(itemView);
            leftDialog = itemView.findViewById((R.id.left_dialog));
        }

        public TextView getLeftDialog() {
            return leftDialog;
        }
    }


}

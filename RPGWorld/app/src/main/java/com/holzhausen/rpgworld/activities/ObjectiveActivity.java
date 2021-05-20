package com.holzhausen.rpgworld.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.holzhausen.rpgworld.R;
import com.holzhausen.rpgworld.adapter.ObjectiveAdapter;
import com.holzhausen.rpgworld.api.ApiCaller;
import com.holzhausen.rpgworld.api.IObjectiveAPI;
import com.holzhausen.rpgworld.model.Attack;
import com.holzhausen.rpgworld.model.Dialog;
import com.holzhausen.rpgworld.model.Message;
import com.holzhausen.rpgworld.model.Objective;
import com.holzhausen.rpgworld.model.Player;
import com.holzhausen.rpgworld.model.Quest;
import com.holzhausen.rpgworld.util.ObjectiveAdapterHelper;
import com.holzhausen.rpgworld.viewmodel.ObjectiveViewModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;

public class ObjectiveActivity extends AppCompatActivity implements ObjectiveAdapterHelper {

    private ProgressBar playerHpProgressBar;

    private TextView playerHpInfo;

    private TextView playerLevel;

    private TextView playerName;

    private ConstraintLayout attackSection;

    private ConstraintLayout mainLayout;

    private Button attackButton;

    private Button healButton;

    private Button nextAttackButton;

    private Button previousAttackButton;

    private RecyclerView objectiveRecyclerView;

    private ProgressBar opponentHpProgressBar;

    private TextView opponentHpInfo;

    private TextView opponentLevel;

    private TextView opponentName;

    private Player player;

    private Quest quest;

    private Objective currentObjective;

    private int playerHp;

    private int opponentHp;

    private int currentAttackIndex;

    private int possibleNumberOfHealingsForPlayer;

    private int possibleNumberOfHealingsForNPC;

    private ObjectiveViewModel viewModel;

    private CompositeDisposable compositeDisposable;

    private List<Dialog> dialogs;

    private ObjectiveAdapter adapter;

    private final static int BASE_HP = 75;

    private final static int LEVEL_HP_MULTIPLIER = 25;

    private final static float MIN_DAMAGE_MULTIPLIER = 0.7f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objective);

        compositeDisposable = new CompositeDisposable();

        assignViews();
        loadIntentData();
        prepareData();
        if(currentObjective.getEventType().equals("T")){
            downloadDialogs();
        }
        else {
            addAttackSectionListeners();
        }
    }

    private void addAttackSectionListeners() {
        attackButton.setOnClickListener(this::onAttack);
        healButton.setOnClickListener(this::onHeal);
        nextAttackButton.setOnClickListener(this::onNextAttack);
        previousAttackButton.setOnClickListener(this::onPreviousAttack);
    }

    private void onPreviousAttack(View view) {
        currentAttackIndex--;
        attackButton.setText(player.getAttacks().get(currentAttackIndex).toString());
        disableOrEnableButtonsAccordingToIndex();
    }

    private void onNextAttack(View view) {
        currentAttackIndex++;
        attackButton.setText(player.getAttacks().get(currentAttackIndex).toString());
        disableOrEnableButtonsAccordingToIndex();
    }

    private void onHeal(View view) {
        Random random = new Random();
        int maxHp = getMaxHp(player.getLevel());
        int healedHp = random.nextInt(maxHp - playerHp) + 1;
        playerHp += healedHp;
        String playerHpLabel = playerHp + "/" + maxHp + "HP";
        playerHpInfo.setText(playerHpLabel);
        possibleNumberOfHealingsForPlayer--;
        String healButtonLabel = "Heal (" + possibleNumberOfHealingsForPlayer + ")";
        healButton.setText(healButtonLabel);
        if(possibleNumberOfHealingsForPlayer == 0){
            healButton.setEnabled(false);
        }
        Message message = new Message();
        message.setPlayer(true);
        message.setColor(R.color.green);
        message.setContent(Html.fromHtml("Player heals himself and gains " + healedHp
        + " HP", Html.FROM_HTML_MODE_COMPACT));
        message.setDisabled(true);
        adapter.getMessages().add(message);
        adapter.getMessages().addAll(makeNPCTurn());
        adapter.notifyDataSetChanged();
        scrollToPosition(adapter.getItemCount() - 1);
    }

    private void onAttack(View view) {
        List<Attack> attacks = player.getAttacks();
        Random random = new Random();
        int damage = random.ints((int) (MIN_DAMAGE_MULTIPLIER * attacks.get(currentAttackIndex).getMaxDamage()),
                attacks.get(currentAttackIndex).getMaxDamage()).findFirst().getAsInt();
        Message message = new Message();
        opponentHp = opponentHp > damage ? opponentHp - damage : 0;
        opponentHpProgressBar.setProgress(opponentHp);
        String label = opponentHp + "/" + getMaxHp(currentObjective.getNpc().getLevel());
        opponentHpInfo.setText(label);
        message.setContent(Html.fromHtml("Player deals " + damage + " damage to your HP",
                Html.FROM_HTML_MODE_COMPACT));
        message.setColor(R.color.design_default_color_error);
        message.setPlayer(true);
        message.setDisabled(true);
        List<Message> messages = new LinkedList<>();
        messages.add(message);
        if(opponentHp == 0){
            Message lastMessage = new Message();
            message.setContent(Html.fromHtml("You have won (click to end)", Html.FROM_HTML_MODE_COMPACT));
            message.setColor(R.color.green);
            message.setPlayer(true);
            message.setLast(true);
            messages.add(lastMessage);
            attackSection.setVisibility(View.GONE);
        }
        else {
            messages.addAll(makeNPCTurn());
        }
        adapter.getMessages().addAll(messages);
        adapter.notifyDataSetChanged();
        scrollToPosition(adapter.getItemCount() - 1);
    }

    private void disableOrEnableButtonsAccordingToIndex() {
        if(currentAttackIndex == 0){
            previousAttackButton.setEnabled(false);
        }
        else if(!previousAttackButton.isEnabled() && currentAttackIndex > 0) {
            previousAttackButton.setEnabled(true);
        }
        if(currentAttackIndex == player.getAttacks().size() - 1){
            nextAttackButton.setEnabled(false);
        }
        else if(!nextAttackButton.isEnabled() && currentAttackIndex < player.getAttacks().size() - 1) {
            nextAttackButton.setEnabled(true);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void downloadDialogs() {
        Retrofit retrofit = ApiCaller.getRetrofitClient();
        IObjectiveAPI api = retrofit.create(IObjectiveAPI.class);
        ApiCaller<Dialog> caller = new ApiCaller<>();
        Disposable disposable = caller.getListOfObjectsFromApi(
                api.getDialogsForObjective(currentObjective.getId()),
                this::onDownloadDialog, this::onError);
        compositeDisposable.add(disposable);
    }

    private void onError(Throwable throwable) {
        throwable.printStackTrace();
        Toast.makeText(this, getString(R.string.error_info), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void onDownloadDialog(List<Dialog> dialogs) {
        this.dialogs = dialogs;
        List<Message> messages = createMessagesFromDialogs("0");
        messages.addAll(getNextMessages(messages.get(messages.size() - 1)));
        viewModel.sendMessage(messages);
    }

    private List<Message> createMessagesFromDialogs(String lastOrder){
        final String[] lastOrderSplit = lastOrder.split("\\.");
        final int nextOrderFirstPart = Integer.parseInt(lastOrderSplit[0]) + 1;
        return dialogs.stream().filter(dialog -> {
            String[] dialogSplit = dialog.getOrder().split("\\.");
            if(dialogSplit.length == 1 && Integer.parseInt(dialogSplit[0]) == nextOrderFirstPart){
                return true;
            }
            else if(Integer.parseInt(dialogSplit[0]) == nextOrderFirstPart){
                boolean isNextInOrder = true;
                for(int i = 1;i < lastOrderSplit.length && isNextInOrder;i++){
                    isNextInOrder = dialogSplit[i].equals(lastOrderSplit[i]);
                }
                return isNextInOrder;
            }
            return false;
        }).map(dialog -> {
            Message message = new Message();
            message.setPlayer(dialog.isPlayer());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<b>");
            if(message.isPlayer()){
                stringBuilder.append(player.getName());
            }
            else {
                stringBuilder.append(currentObjective.getNpc().getName());
            }
            stringBuilder.append(":</b> ");
            if(dialog.getTraitGuard() != null){
                stringBuilder.append("[");
                stringBuilder.append(dialog.getTraitGuard());
                stringBuilder.append("]");
                if(!player.hasTrait(dialog.getTraitGuard())){
                    message.setDisabled(true);
                }
            }
            else if(dialog.getSkillGuard() != null){
                stringBuilder.append("[");
                stringBuilder.append(dialog.getSkillGuard());
                stringBuilder.append(" ");
                stringBuilder.append(dialog.getSkillAmount());
                stringBuilder.append("]");
                if(!player.hasEnoughSkill(dialog.getSkillGuard(), dialog.getSkillAmount())){
                    message.setDisabled(true);
                }
            }
            stringBuilder.append(dialog.getContent());
            message.setContent(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_COMPACT));
            if(message.isDisabled()) {
                message.setColor(R.color.material_on_primary_disabled);
            }
            else {
                message.setColor(R.color.black);
            }
            message.setOrder(dialog.getOrder());
            message.setLast(dialog.isLast());
            return message;
        }).collect(Collectors.toList());
    }

    private void prepareData() {
        playerHp = getMaxHp(player.getLevel());
        playerHpProgressBar.setMax(playerHp);
        playerHpProgressBar.setProgress(playerHp);
        String playerHpLabel = playerHp + "/" + playerHp + "HP";
        playerHpInfo.setText(playerHpLabel);
        String playerLevelLabel = "Level " + player.getLevel();
        playerLevel.setText(playerLevelLabel);
        playerName.setText(player.getName());
        if(currentObjective.getEventType().equals("F")){
            attackSection.setVisibility(View.VISIBLE);
            previousAttackButton.setEnabled(false);
        }
        attackButton.setText(player.getAttacks().get(currentAttackIndex).toString());
        possibleNumberOfHealingsForPlayer = getHealingsNumber(player.getLevel());
        possibleNumberOfHealingsForNPC = getHealingsNumber(currentObjective.getNpc().getLevel());
        String healButtonLabel = "Heal (" + possibleNumberOfHealingsForPlayer + ")";
        healButton.setText(healButtonLabel);
        viewModel = new ViewModelProvider(this).get(ObjectiveViewModel.class);
        viewModel.createMessageSubject();
        adapter = new ObjectiveAdapter(this, viewModel, this);
        objectiveRecyclerView.setAdapter(adapter);
        objectiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        opponentHp = getMaxHp(currentObjective.getNpc().getLevel());
        opponentHpProgressBar.setProgress(opponentHp);
        opponentHpProgressBar.setMax(opponentHp);
        String opponentHpLabel = opponentHp + "/" + opponentHp + "HP";
        opponentHpInfo.setText(opponentHpLabel);
        String opponentLevelLabel = "Level " + currentObjective.getNpc().getLevel();
        opponentLevel.setText(opponentHpLabel);
        opponentName.setText(opponentLevelLabel);
    }

    private int getHealingsNumber(int level) {
        if(level <= 5) {
            return 1;
        }
        else if(level <= 15) {
            return 2;
        }
        else {
            return 3;
        }
    }

    private int getMaxHp(int level){
        return BASE_HP + level * LEVEL_HP_MULTIPLIER;
    }

    private void loadIntentData() {
        player = (Player) getIntent().getSerializableExtra(getString(R.string.player_key));
        quest = (Quest) getIntent().getSerializableExtra(getString(R.string.quest_key));
        currentObjective = quest
                .getObjectives()
                .stream()
                .filter(objective -> objective.getId() == quest.getActiveObjective())
                .findFirst()
                .orElse(new Objective());
    }

    private void assignViews() {
        playerHpProgressBar = findViewById(R.id.player_hp);
        playerHpInfo = findViewById(R.id.player_hp_info);
        playerLevel = findViewById(R.id.player_level);
        playerName = findViewById(R.id.player_name_objective);
        attackSection = findViewById(R.id.attack_section);
        mainLayout = findViewById(R.id.mainLayout);
        attackButton = findViewById(R.id.attack_button);
        healButton = findViewById(R.id.heal_button);
        previousAttackButton = findViewById(R.id.previous_button);
        nextAttackButton = findViewById(R.id.next_button);
        objectiveRecyclerView = findViewById(R.id.objectiveRecyclerView);
        opponentHpProgressBar = findViewById(R.id.opponent_hp);
        opponentHpInfo = findViewById(R.id.opponent_hp_info);
        opponentLevel = findViewById(R.id.opponent_level);
        opponentName = findViewById(R.id.opponent);
    }


    @Override
    public List<Message> getNextMessages(Message previousMessage) {
        if(currentObjective.getEventType().equals("T")){
            List<Message> messages = new LinkedList<>();
            while (messages.isEmpty() || !messages.get(messages.size() - 1).isPlayer()){
                messages.addAll(createMessagesFromDialogs(previousMessage.getOrder()));
                previousMessage = messages.get(messages.size() - 1);
            }
            return messages;
        }
        else {
            return makeNPCTurn();
        }
    }

    private List<Message> makeNPCTurn() {
        List<Attack> attacks = currentObjective.getNpc().getAttacks();
        Random random = new Random();
        int chosenAttack = random.nextInt(attacks.size());
        int damage = random.ints((int) (MIN_DAMAGE_MULTIPLIER * attacks.get(chosenAttack).getMaxDamage()),
                attacks.get(chosenAttack).getMaxDamage()).findFirst().getAsInt();
        Message message = new Message();
        playerHp = playerHp > damage ? playerHp - damage : 0;
        playerHpProgressBar.setProgress(playerHp);
        String label = playerHp + "/" + getMaxHp(player.getLevel());
        playerHpInfo.setText(label);
        message.setContent(Html.fromHtml(currentObjective.getNpc().getName()
                + " deals " + damage + " damage to your HP", Html.FROM_HTML_MODE_COMPACT));
        message.setColor(R.color.design_default_color_error);
        List<Message> messages = new LinkedList<>();
        messages.add(message);
        if(playerHp == 0){
            Message lastMessage = new Message();
            message.setContent(Html.fromHtml("You have lost (click to end)"));
            message.setColor(R.color.design_default_color_error);
            message.setPlayer(true);
            message.setLast(true);
            messages.add(lastMessage);
            attackSection.setVisibility(View.GONE);
        }
        return messages;

    }

    @Override
    public void endActivity() {
        if(currentObjective.getEventType().equals("T") || (playerHp > 0 && opponentHp == 0)){
            Intent intent = new Intent();
            intent.putExtra(getString(R.string.objective_id), currentObjective.getId());
            setResult(RESULT_OK, intent);
        }
        else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void scrollToPosition(int position) {
        objectiveRecyclerView.scrollToPosition(position);
    }

    @Override
    public boolean isTalkEvent() {
        return currentObjective.getEventType().equals("T");
    }
}
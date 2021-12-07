package com.rammelbalagtas.rammel_diceroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private ArrayList<Integer> dieSizeList;
    private int dieSize;
    private boolean willRollTwice;
    private Die currentDie;
    private DieRolls dieRolls;

    private Spinner dieSizeSpinner;
    private ArrayAdapter<Integer> dieSizeAdapter;
    private HistoryAdapter historyAdapter;
    private Switch rollTwiceSwitch;
    private EditText inputDieSize;
    private TextView result1Text;
    private TextView result2Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate SharedPreferences
        sharedPreferences = getSharedPreferences("SharedPreferences1", Context.MODE_PRIVATE);
        // Roll results
        result1Text = findViewById(R.id.tv_result1);
        result2Text = findViewById(R.id.tv_result2);
        // Custom die size
        inputDieSize = findViewById(R.id.input_die_size);
        // Event listeners
        Button btnRoll = findViewById(R.id.btn_roll);
        btnRoll.setOnClickListener(onClickRoll);
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(onClickSave);
        Button btnDelete = findViewById(R.id.btn_clear_history);
        btnDelete.setOnClickListener(onClickDelete);
        rollTwiceSwitch = findViewById(R.id.switch_number_rolls);
        rollTwiceSwitch.setOnCheckedChangeListener(onChangeSwitch);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSharedPrefData();
        Collections.sort(dieSizeList);

        // Configure spinner element
        dieSizeSpinner = findViewById(R.id.spinner_die_size);
        dieSizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dieSizeList);
        dieSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dieSizeSpinner.setAdapter(dieSizeAdapter);
        dieSizeSpinner.setOnItemSelectedListener(onSelectDie);

        // Set values of results
        rollTwiceSwitch.setChecked(willRollTwice);
        if (currentDie == null) {
            result1Text.setText("0");
            result1Text.setText("0");
        } else {
            result1Text.setText(String.valueOf(currentDie.getResult1()));
            result2Text.setText(String.valueOf(currentDie.getResult2()));
        }

        // Set switch setting
        if (willRollTwice) {
            result2Text.setVisibility(View.VISIBLE);
        } else {
            result2Text.setVisibility(View.GONE);
        }

        // Configure recycler
        RecyclerView recycler = findViewById(R.id.roll_history);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        historyAdapter = new HistoryAdapter(dieRolls.getRollList());
        recycler.setAdapter(historyAdapter);

    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    protected void onStop(){
        saveData();
        super.onStop();
    }

    protected void onDestroy(){
        saveData();
        super.onDestroy();
    }

    /**
     * Event handler for ROLL button
     */
    private final View.OnClickListener onClickRoll = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            currentDie = new Die(dieSize, willRollTwice);
            result1Text.setText(String.valueOf(currentDie.getResult1()));
            if (willRollTwice) {
                result2Text.setText(String.valueOf(currentDie.getResult2()));
            }
            dieRolls.getRollList().add(currentDie);
            historyAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Event handler for SAVE button
     */
    private final View.OnClickListener onClickSave = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int customDieSize = Integer.parseInt(inputDieSize.getText().toString());
            if (!dieSizeList.contains(customDieSize)) {
                dieSizeAdapter.add(customDieSize);
                Collections.sort(dieSizeList);
                dieSizeAdapter.notifyDataSetChanged();
            }
            inputDieSize.setText("");
        }
    };

    /**
     * Event handler for DELETE button
     */
    private final View.OnClickListener onClickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dieRolls.getRollList().clear();
            historyAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Event handler for switch button
     */
    private final CompoundButton.OnCheckedChangeListener onChangeSwitch = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton toggleButton, boolean isToggled) {
            willRollTwice = isToggled;
            if (isToggled) {
                result2Text.setVisibility(View.VISIBLE);
                result2Text.setText("0");
            } else {
                result2Text.setVisibility(View.GONE);
            }
        }
    };

    /**
     * Event handler for spinner element
     */
    private final AdapterView.OnItemSelectedListener onSelectDie = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dieSize = dieSizeList.get(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    /**
     * Store data to SharedPreferences
     */
    public void saveData() {
        Gson gson = new Gson();
        String jsonDie = gson.toJson(currentDie);
        String jsonDieRolls = gson.toJson(dieRolls);
        String jsonDieSize = gson.toJson(dieSizeList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentDie", jsonDie);
        editor.putString("dieRolls", jsonDieRolls);
        editor.putString("dieSizeList", jsonDieSize);
        editor.putBoolean("willRollTwice", willRollTwice);
        editor.apply();
    }

    /**
     * Retrieve data from SharedPreferences
     */
    public void getSharedPrefData() {

        willRollTwice = sharedPreferences.getBoolean("willRollTwice", false);
        Gson gson = new Gson();

        String jsonDie = sharedPreferences.getString("currentDie", null);
        if (jsonDie != null) {
           currentDie = gson.fromJson(jsonDie, Die.class);
        } else {
            currentDie = null;
        }

        String jsonDieRolls = sharedPreferences.getString("dieRolls", null);
        if (jsonDieRolls != null) {
            dieRolls = gson.fromJson(jsonDieRolls, DieRolls.class);
        } else {
            dieRolls = new DieRolls();
        }

        String jsonDieSize = sharedPreferences.getString("dieSizeList", null);
        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();
        dieSizeList = gson.fromJson(jsonDieSize, type);
        // checking below if the array list is empty or not
        if (dieSizeList == null) {
            // if the array list is empty
            // creating a new array list.
            dieSizeList = new ArrayList<>();
            dieSizeList.add(4);
            dieSizeList.add(6);
            dieSizeList.add(8);
            dieSizeList.add(10);
            dieSizeList.add(12);
            dieSizeList.add(20);
        }
    }
}
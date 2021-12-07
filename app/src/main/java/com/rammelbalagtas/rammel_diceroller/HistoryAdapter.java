package com.rammelbalagtas.rammel_diceroller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    public final ArrayList<Die> dieRolls;

    public HistoryAdapter(ArrayList<Die> dieRolls) {
        this.dieRolls = dieRolls;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView dieSizeText;
        private final TextView dieResultText;

        public ViewHolder(@NonNull View view) {
            super(view);
            dieSizeText = view.findViewById(R.id.die_size_text);
            dieResultText = view.findViewById(R.id.roll_result_text);
        }

        public TextView getDieSizeText() {
            return dieSizeText;
        }

        public TextView getDieResultText() {
            return dieResultText;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.die_roll_item, parent, false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, int position) {
        Die die = dieRolls.get(position);
        viewholder.getDieSizeText().setText(String.valueOf(die.getNumberOfSides()));
        if (die.willRollTwice()) {
            viewholder.getDieResultText().setText(String.valueOf(die.getResult1())
                    + ", "
                    + String.valueOf(die.getResult2()));
        } else {
            viewholder.getDieResultText().setText(String.valueOf(die.getResult1()));
        }
    }

    @Override
    public int getItemCount() {
        return dieRolls == null ? 0 : dieRolls.size();
    }

}

package com.arrowwould.playquizearn.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.arrowwould.playquizearn.R;
import com.arrowwould.playquizearn.databinding.ItemHistoryBinding;

import java.util.ArrayList;

public class TrHistoryAdapter extends RecyclerView.Adapter<TrHistoryAdapter.videHolder> {

    Context context;
    ArrayList<TrHistoryModel>list;

    public TrHistoryAdapter(Context context, ArrayList<TrHistoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public videHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_history,parent,false);
        return new videHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull videHolder holder, int position) {

        final TrHistoryModel model = list.get(position);

        String status = model.getStatus();
        String coin = model.getCoin();
        String methode = model.getPaymentMethode();

        holder.binding.amount.setText(model.getPaymentMethode());
        holder.binding.numberHis.setText(model.getNumber());
       // holder.binding.payStatus.setText(model.getStatus());
        holder.binding.date.setText(model.getDate());

        int currentCoin = Integer.parseInt(coin);

        double earn = currentCoin * 0.02;
        holder.binding.earn.setText("("+"Rs. "+earn+""+")");


        if (status.equals("false")){

            holder.binding.payStatus.setText("pending");
        }
        else {

            holder.binding.payStatus.setText("success");
            holder.binding.payStatus.setBackgroundResource(R.drawable.sucessbtn);
        }

        if (methode.equals("Paytm")){

            holder.binding.logo.setImageResource(R.drawable.payt);
        }
        else if (methode.equals("Amazon Gift")){

            holder.binding.logo.setImageResource(R.drawable.amazon);
        }
        else if (methode.equals("Paypal")){

            holder.binding.logo.setImageResource(R.drawable.paypal);
        }
        else if (methode.equals("Google pay")){

            holder.binding.logo.setImageResource(R.drawable.googleplay);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class videHolder extends RecyclerView.ViewHolder {

        ItemHistoryBinding binding;
        public videHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemHistoryBinding.bind(itemView);
        }
    }

}

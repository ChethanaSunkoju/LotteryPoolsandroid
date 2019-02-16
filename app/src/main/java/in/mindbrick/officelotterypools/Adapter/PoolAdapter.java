package in.mindbrick.officelotterypools.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.mindbrick.officelotterypools.Activities.AddPoolGiftScreen;
import in.mindbrick.officelotterypools.Activities.LotteryPoolScreen;
import in.mindbrick.officelotterypools.Activities.MainActivity;

import in.mindbrick.officelotterypools.Models.Pool;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 12/8/2018.
 */

public class PoolAdapter extends RecyclerView.Adapter<PoolAdapter.MyViewHolder> {

    private Context context;
    private List<Pool> pool_list;
    Typeface tf;


    public PoolAdapter(Context context, List<Pool> pool_list) {
        this.context = context;
        this.pool_list = pool_list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_pool_name,tv_target,tv_game,tv_target_amount,tv_game_name,tv_total_numbers,tv_pay;
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.view);
            tv_pool_name = itemView.findViewById(R.id.tv_pool_name);
            tv_target = itemView.findViewById(R.id.tv_target);
            tv_game = itemView.findViewById(R.id.tv_game);
            tv_target_amount = itemView.findViewById(R.id.tv_target_amount);
            tv_game_name = itemView.findViewById(R.id.tv_game_name);
            tv_total_numbers = itemView.findViewById(R.id.tv_total_numbers);
            tv_pay = itemView.findViewById(R.id.tv_pay);



        }
    }


    @Override
    public PoolAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pool_item, parent, false);
        return new PoolAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PoolAdapter.MyViewHolder holder, final int position) {

        final Pool item = pool_list.get(position);

        final String Pooltype = item.getPooltype();

        tf = Typeface.createFromAsset(context.getAssets(), "fonts/Jaapokki_Regular.otf");

        holder.tv_pool_name.setText(item.getPoolname());
     //   holder.tv_pool_name.setTypeface(tf);
        holder.tv_target_amount.setText(item.getAmount());
      //  holder.tv_target_amount.setTypeface(tf);
      //  holder.tv_target.setTypeface(tf);
     //   holder.tv_game.setTypeface(tf);
    //    holder.tv_game_name.setTypeface(tf);
    //    holder.tv_total_numbers.setTypeface(tf);
    //    holder.tv_pay.setTypeface(tf);

        if(Pooltype.equalsIgnoreCase("1")){
            holder.view.setBackgroundColor(Color.parseColor("#E08CC0"));
            holder.tv_game_name.setText("Custom Pool");
        }else {
            holder.view.setBackgroundColor(Color.parseColor("#A4A2AD"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Pooltype.equalsIgnoreCase("1")) {
                    context.startActivity(new Intent(context, AddPoolGiftScreen.class)
                            .putExtra("PoolGID", item.getPoolGID()));
                }else {
                    context.startActivity(new Intent(context, LotteryPoolScreen.class)
                            .putExtra("data", "nodata"));
                }
            }
        });

        /*holder.tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, PaymentActivity.class));
            }
        });*/





    }

    @Override
    public int getItemCount() {
        return pool_list.size();
    }


}

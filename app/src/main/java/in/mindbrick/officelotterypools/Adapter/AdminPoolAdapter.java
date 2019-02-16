package in.mindbrick.officelotterypools.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.mindbrick.officelotterypools.Models.Pool;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 1/20/2019.
 */

public class AdminPoolAdapter extends RecyclerView.Adapter<AdminPoolAdapter.MyViewHolder> {

    private Context context;
    private List<Pool> contact_list;





    public AdminPoolAdapter(Context context, List<Pool> contact_list) {
        this.context = context;
        this.contact_list = contact_list;
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
    public AdminPoolAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pool_item, parent, false);
        return new AdminPoolAdapter.MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final AdminPoolAdapter.MyViewHolder holder, final int position) {

        final Pool item = contact_list.get(position);

        String poolType = item.getPooltype();


        holder.tv_pool_name.setText(item.getPoolname());
        holder.tv_target_amount.setText(item.getAmount());

        if(poolType.equals(1)){
            holder.tv_game_name.setText("CustomPool");
        }else {

        }






    }


    @Override
    public int getItemCount() {
        return contact_list.size();
    }
}

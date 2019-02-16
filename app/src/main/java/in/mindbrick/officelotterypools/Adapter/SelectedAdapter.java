package in.mindbrick.officelotterypools.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.mindbrick.officelotterypools.Interface.ItemClickListener;
import in.mindbrick.officelotterypools.Models.Contact;
import in.mindbrick.officelotterypools.R;

/**
 * Created by chethana on 1/18/2019.
 */

public class SelectedAdapter extends RecyclerView.Adapter<SelectedAdapter.MyViewHolder> implements Filterable {


    private Context context;
    private List<Contact> contact_list,selected;
    private ContactsAdapter.OnItemCheckListener onItemClick;
    public ArrayList<Contact> checkedContacts = new ArrayList<>();
    private List<Contact> orig;
    private List<Contact> list;





    public SelectedAdapter(Context context, List<Contact> contact_list) {
        this.context = context;
        this.contact_list = contact_list;
    }




    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<Contact> results = new ArrayList<Contact>();
                if (orig == null)
                    orig = list;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final Contact g : orig) {
                            if (g.getFirstname().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();

            }
        };
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_mobile_num,tv_common_group;

        ImageView iv_profilepic;
        LinearLayout ll_selected;
        CheckBox iv_select;
        ItemClickListener itemClickListener;


        public MyViewHolder(View itemView) {
            super(itemView);

            tv_mobile_num = itemView.findViewById(R.id.tv_mobile_num);
            tv_common_group = itemView.findViewById(R.id.tv_common_group);
            iv_profilepic = itemView.findViewById(R.id.iv_profilepic);
            ll_selected = itemView.findViewById(R.id.ll_selected);
            iv_select = itemView.findViewById(R.id.iv_select);
            iv_select.setVisibility(View.GONE);

        }


    }


    @Override
    public SelectedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item, parent, false);
        return new SelectedAdapter.MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final SelectedAdapter.MyViewHolder holder, final int position) {

        final Contact item = contact_list.get(position);

        String image = item.getProfilePic();

        holder.tv_mobile_num.setText(item.getFirstname());
        holder.tv_common_group.setText(item.getPhone());

        if (image != null && !image.isEmpty() && !image.equals("null"))
            Picasso.with(context)
                    .load( context.getString(R.string.base_url)+"profiles/"+item.getProfilePic())
                    .resize(200, 200)
                    .into(holder.iv_profilepic);
        else {
            holder.iv_profilepic.setImageResource(R.drawable.man);
        }







    }


    @Override
    public int getItemCount() {
        return contact_list.size();
    }



}

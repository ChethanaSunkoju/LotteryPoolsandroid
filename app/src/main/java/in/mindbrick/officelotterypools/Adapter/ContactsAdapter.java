package in.mindbrick.officelotterypools.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
 * Created by chethana on 1/2/2019.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder>  {

    private Context context;
    private List<Contact> contact_list,selected;
    private OnItemCheckListener onItemClick;
    public ArrayList<Contact> checkedContacts = new ArrayList<>();

   // public ArrayList<Contact> selected_usersList=new ArrayList<>();
    boolean isSelect = false;

    public interface OnItemCheckListener {
        void onItemCheck(Contact item);
        void onItemUncheck(Contact item);
    }

    public ContactsAdapter(Context context, List<Contact> contact_list) {
        this.context = context;
        this.contact_list = contact_list;
    }

    public ContactsAdapter(Context context, List<Contact> contact_list, ArrayList<Contact> selected) {
        this.context = context;
        this.contact_list = contact_list;
        this.selected = selected;
    }

    public ContactsAdapter(Context context, List<Contact> contact_list, OnItemCheckListener onItemCheckListener) {
        this.context = context;
        this.contact_list = contact_list;
        this.onItemClick = onItemCheckListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
            iv_select.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v,getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener ic){
            this.itemClickListener = ic;
        }
    }


    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_item, parent, false);
        return new ContactsAdapter.MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ContactsAdapter.MyViewHolder holder, final int position) {

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

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int post) {
                if(holder.iv_select.isChecked()){

                    checkedContacts.add(contact_list.get(position));

                }else {
                    checkedContacts.remove(contact_list.get(position));
                }
            }
        });


        /*if(selected_usersList.contains(contact_list.get(position)))
            holder.ll_selected.setBackgroundColor(context.getColor(R.color.colorPrimary));
        else
            holder.ll_selected.setBackgroundColor(ContextCompat.getColor(context, R.color.list_item_normal_state));*/

        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // isSelect = true;
                *//*holder.iv_select.setVisibility(View.VISIBLE);
                return false;*//*
            }
        });*/

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //  holder.iv_select.setVisibility(View.VISIBLE);
                *//*((MyViewHolder) holder).iv_select.setChecked(
                        !((MyViewHolder) holder).iv_select.isChecked());
                if (((MyViewHolder) holder).iv_select.isChecked()) {
                    onItemClick.onItemCheck(item);
                } else {
                    onItemClick.onItemUncheck(item);
                }*//*


               holder.iv_select.setChecked(!holder.iv_select.isChecked());

                if(holder.iv_select.isChecked()){
                    onItemClick.onItemCheck(item);
                }else {
                    onItemClick.onItemUncheck(item);
                }
                *//*if (selected.contains(item)) {
                    selected.remove(item);
                    unhighlightView(holder);
                    onItemClick.onItemCheck(item);
                } else {
                    selected.add(item);
                    highlightView(holder);
                    onItemClick.onItemUncheck(item);
                }*//*
            }



        });*/

        /*if (selected.contains(item))
            highlightView(holder);
        else
            unhighlightView(holder);*/

        /*if(selected_usersList.contains(contact_list.get(position)))
            holder.iv_select.setVisibility(View.VISIBLE);
        else
            holder.iv_select.setVisibility(View.INVISIBLE);*/
        /*if(isSelect == true){
            holder.iv_select.setVisibility(View.VISIBLE);
        }else {
            holder.iv_select.setVisibility(View.INVISIBLE);
        }
*/



    }

    private void highlightView(final ContactsAdapter.MyViewHolder holder) {
      //  holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        holder.iv_select.setVisibility(View.VISIBLE);
    }

    private void unhighlightView(final ContactsAdapter.MyViewHolder holder) {
        //holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        holder.iv_select.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return contact_list.size();
    }
    public void selectedItems(List<Contact> selected){

    }

    public void addAll(List<Contact> items) {
        clearAll(false);
        this.contact_list = items;
        notifyDataSetChanged();
    }

    public void clearAll(boolean isNotify) {
        contact_list.clear();
        selected.clear();
        if (isNotify) notifyDataSetChanged();
    }

    public void clearSelected() {
        selected.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        selected.clear();
        selected.addAll(contact_list);
        notifyDataSetChanged();
    }

    public List<Contact> getSelected() {
        return selected;
    }
}

package com.decoders.icontacts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.decoders.icontacts.R;
import com.decoders.icontacts.model.ContactModel;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ContactModel> mList;
    private LayoutInflater mLayoutInflater = null;

    public ContactsAdapter(Context context, ArrayList<ContactModel> list) {
        mContext = context;
        mList = list;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CompleteListViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.contact_item, null);
            viewHolder = new CompleteListViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (CompleteListViewHolder) v.getTag();
        }

        // get contact model here
        ContactModel model = mList.get(position);

        // set views data here
        viewHolder.name.setText(model.getName());
        viewHolder.phone.setText(model.getPhone());

        return v;
    }

    public void refreshAdapter(ArrayList<ContactModel> models) {
        this.mList = models;
        notifyDataSetChanged();
    }

    static class CompleteListViewHolder {
        // declare views here
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.phone)
        TextView phone;
        public CompleteListViewHolder(View view) {
            //initialize views here
            ButterKnife.bind(this, view);
        }
    }
}

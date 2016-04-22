package com.example.qmma.featuredetection.EbaySearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qmma.featuredetection.EbaySearch.Model.Item;
import com.example.qmma.featuredetection.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by S_And on 2016/4/22 0022.
 */
public class SearchResultAdapter extends BaseAdapter {
    private Context context;
    private List<Item> listItem;
    public SearchResultAdapter (Context context, List<Item> listItem) {
        this.context = context;
        this.listItem = listItem;
    }
    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_result_adapter, null);
            holder = new ViewHolder();
            holder.imgItem = (ImageView) convertView.findViewById(R.id.imgItem);
            holder.txtViewName = (TextView) convertView.findViewById(R.id.txtViewName);
            holder.txtViewPrice = (TextView) convertView.findViewById(R.id.txtViewPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Item item = listItem.get(position);
        if (item != null) {
            //Picasso.with(context).load(item.getImageUrl()).resize(120, 150).into(holder.imgItem);
            Picasso.with(context).load(item.getImageUrl()).into(holder.imgItem);
            holder.txtViewName.setText(item.getItemName());
            //holder.txtViewPrice.setText("Price : $ " + item.getPrice());
            holder.txtViewPrice.setText("$" + String.valueOf(item.getDoublePrice()));
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView imgItem;
        TextView txtViewName;
        TextView txtViewPrice;
    }
}

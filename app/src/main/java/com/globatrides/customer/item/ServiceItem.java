package com.globatrides.customer.item;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.globatrides.customer.R;
import com.globatrides.customer.activity.AllMerchantActivity;
import com.globatrides.customer.activity.RentCarActivity;
import com.globatrides.customer.activity.RideCarActivity;
import com.globatrides.customer.activity.SendActivity;
import com.globatrides.customer.constants.Constant;
import com.globatrides.customer.gmap.activity.AllMerchantGmapActivity;
import com.globatrides.customer.gmap.activity.RentCarGmapActivity;
import com.globatrides.customer.gmap.activity.RideCarGmapActivity;
import com.globatrides.customer.gmap.activity.SendGmapActivity;
import com.globatrides.customer.models.ServiceDataModel;
import com.globatrides.customer.utils.PicassoTrustAll;
import com.globatrides.customer.utils.SettingPreference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by otacodes on 3/24/2019.
 */

public class ServiceItem extends RecyclerView.Adapter<ServiceItem.ItemRowHolder> {

    private List<ServiceDataModel> dataList;
    private Context mContext;
    private int rowLayout;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ServiceDataModel item);
    }

    public ServiceItem(Context context, List<ServiceDataModel> dataList, int rowLayout, OnItemClickListener listener) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final ServiceDataModel singleItem = dataList.get(position);
        SettingPreference sp = new SettingPreference(mContext);
        holder.text.setText(singleItem.getFitur());
            PicassoTrustAll.getInstance(mContext)
                    .load(Constant.IMAGESFITUR + singleItem.getIcon())
                    .resize(100, 100)
                    .into(holder.image);


        switch (singleItem.getHome()) {
            case "1":
                holder.background.setBackground(mContext.getResources().getDrawable(R.drawable.btn_rect));
                holder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i;
                        if (singleItem.getMaps().equals("1")) {
                            i = new Intent(mContext, RideCarGmapActivity.class);
                        } else {
                            i = new Intent(mContext, RideCarActivity.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("FiturKey", singleItem.getIdFitur());
                        i.putExtra("job", singleItem.getJob());
                        i.putExtra("icon", singleItem.getIcon());
                        mContext.startActivity(i);

                    }
                });
                break;
            case "2":
                holder.background.setBackground(mContext.getResources().getDrawable(R.drawable.btn_rect2));
                holder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i;
                        if (singleItem.getMaps().equals("1")) {
                            i = new Intent(mContext, SendGmapActivity.class);
                        } else {
                            i = new Intent(mContext, SendActivity.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("FiturKey", singleItem.getIdFitur());
                        i.putExtra("job", singleItem.getJob());
                        i.putExtra("icon", singleItem.getIcon());
                        mContext.startActivity(i);

                    }
                });
                break;
            case "3":
                holder.background.setBackground(mContext.getResources().getDrawable(R.drawable.btn_rect3));
                holder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i;
                        if (singleItem.getMaps().equals("1")) {
                            i = new Intent(mContext, RentCarGmapActivity.class);
                        } else {
                            i = new Intent(mContext, RentCarActivity.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("FiturKey", singleItem.getIdFitur());
                        i.putExtra("icon", singleItem.getIcon());
                        mContext.startActivity(i);

                    }
                });
                break;
            case "4":
                holder.background.setBackground(mContext.getResources().getDrawable(R.drawable.btn_rect4));
                holder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i;
                        if (singleItem.getMaps().equals("1")) {
                            i = new Intent(mContext, AllMerchantGmapActivity.class);
                        } else {
                            i = new Intent(mContext, AllMerchantActivity.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("FiturKey", singleItem.getIdFitur());
                        mContext.startActivity(i);

                    }
                });
                break;
        }
        if (singleItem.getHome().equals("0") && singleItem.getIdFitur() == 100) {
            holder.bind(singleItem, listener);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView background,image;

        ItemRowHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }

        public void bind(final ServiceDataModel item, final OnItemClickListener listener) {

            if (item.getHome().equals("0")) {
                background.getBackground().setColorFilter(mContext.getResources().getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
                    PicassoTrustAll.getInstance(mContext)
                            .load(R.drawable.ic_more)
                            .resize(100, 100)
                            .into(image);

                    background.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onItemClick(item);
                        }
                    });
            }
        }
    }
}

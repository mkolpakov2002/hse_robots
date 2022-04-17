package ru.hse.control_system_v2.list_devices;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import ru.hse.control_system_v2.activity.MainActivity;
import ru.hse.control_system_v2.R;

public class ViewHolderFactory {

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout buttonLayout;
        TextView buttonTextInfo;

        public ButtonViewHolder(View itemView) {
            super(itemView);
            buttonLayout = (ConstraintLayout) itemView.findViewById(R.id.button_add_layout);
            buttonTextInfo = itemView.findViewById(R.id.button_add_device_text);
        }
    }

    static class ListDevicesHolder extends RecyclerView.ViewHolder {
        TextView mName;
        ImageView deviceImage;
        ImageView checkMark;
        ImageView wifiSupportIcon;
        ImageView btSupportIcon;
        MainActivity ma;
        MaterialCardView materialCardView;

        private static final String TAG = "VHFactory";


        public ListDevicesHolder(@NonNull View itemView, @NonNull Context context, IListener listener) {
            super(itemView);
            mName = itemView.findViewById(R.id.item_name);
            deviceImage = itemView.findViewById(R.id.icon_image_view);
            checkMark = itemView.findViewById(R.id.check_mark);
            wifiSupportIcon = itemView.findViewById(R.id.wifi_icon);
            btSupportIcon = itemView.findViewById(R.id.bt_icon);
            materialCardView = itemView.findViewById(R.id.device_item_card_view);

            if (context instanceof Activity) {
                ma = (MainActivity) context;
            }
            itemView.setOnClickListener(v -> {
                listener.onDeviceClicked(getAdapterPosition(), itemView);
            });
            itemView.setOnLongClickListener(v -> {
                listener.onDeviceLongClicked(getAdapterPosition(), itemView);
                return true;
            });
        }

        interface IListener {
            void onDeviceClicked(int id, View itemView);

            void onDeviceLongClicked(int id, View itemView);
        }

    }

    public static RecyclerView.ViewHolder create(ViewGroup parent, int viewType, Context context, ListDevicesHolder.IListener listener) {
        switch (viewType) {
            case ItemType.BUTTON_ITEM_TYPE:
                View buttonTypeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list_button_add, parent, false);
                return new ViewHolderFactory.ButtonViewHolder(buttonTypeView);

            case ItemType.DEVICE_ITEM_TYPE:
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View layout = inflater.inflate(R.layout.item_main_list_device, parent, false);
                return new ViewHolderFactory.ListDevicesHolder(layout, context, listener);
            default:
                return null;
        }

    }

}

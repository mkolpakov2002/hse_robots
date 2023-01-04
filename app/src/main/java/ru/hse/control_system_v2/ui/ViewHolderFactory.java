package ru.hse.control_system_v2.ui;

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

import ru.hse.control_system_v2.MainActivity;
import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.data.ItemType;

public class ViewHolderFactory {

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout buttonLayout;
        public TextView buttonTextInfo;

        public ButtonViewHolder(View itemView) {
            super(itemView);
            buttonLayout = itemView.findViewById(R.id.button_add_layout);
            buttonTextInfo = itemView.findViewById(R.id.button_add_device_text);
        }
    }

    public static class ListDevicesHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public ImageView deviceImage;
        public ImageView checkMark;
        public ImageView wifiSupportIcon;
        public ImageView btSupportIcon;
        MainActivity ma;
        public MaterialCardView materialCardView;

        public ListDevicesHolder(@NonNull View itemView, @NonNull Context context, IListener listener) {
            super(itemView);
            mName = itemView.findViewById(R.id.item_name);
            deviceImage = itemView.findViewById(R.id.icon_image_view);
            checkMark = itemView.findViewById(R.id.check_mark);
            wifiSupportIcon = itemView.findViewById(R.id.wifi_icon);
            btSupportIcon = itemView.findViewById(R.id.bt_icon);
            materialCardView = itemView.findViewById(R.id.device_item_card_view);

            if (context instanceof MainActivity activity) {
                ma = activity;
            }
            itemView.setOnClickListener(v -> {
                listener.onDeviceClicked(getBindingAdapterPosition(), itemView);
            });
            itemView.setOnLongClickListener(v -> {
                listener.onDeviceLongClicked(getBindingAdapterPosition(), itemView);
                return true;
            });
        }

        interface IListener {
            void onDeviceClicked(int id, View itemView);
            void onDeviceLongClicked(int id, View itemView);
        }

    }

    public static RecyclerView.ViewHolder create(ViewGroup parent, int viewType, Context context, ListDevicesHolder.IListener listener) {
        return switch (viewType) {
            case ItemType.BUTTON_ITEM_TYPE ->
                    new ViewHolderFactory.ButtonViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_main_list_button_add, parent, false));
            case ItemType.DEVICE_ITEM_TYPE ->
                    new ViewHolderFactory.ListDevicesHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_main_list_device, parent, false),
                            context, listener);
            default -> null;
        };

    }

}

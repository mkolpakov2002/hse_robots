package ru.hse.control_system_v2.list_devices;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import ru.hse.control_system_v2.App;
import ru.hse.control_system_v2.AppDataBase;
import ru.hse.control_system_v2.DeviceHandler;
import ru.hse.control_system_v2.DeviceItemTypeDao;
import ru.hse.control_system_v2.MainActivity;
import ru.hse.control_system_v2.MainMenuFragment;
import ru.hse.control_system_v2.R;

public class MultipleTypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ViewHolderFactory.ListDevicesHolder.IListener {

    private ArrayList<ItemType> itemTypeArrayList;
    private ArrayList<DeviceItemType> mData;
    private final Context context;
    private MainActivity ma;
    private MainMenuFragment mainMenuFragment;
    private final DeviceClickedListener listener;
    private ArrayList<DeviceItemType> selectedDevicesList;


    public MultipleTypesAdapter(@NonNull Context context) {
        super();
        if (context instanceof Activity) {
            ma = (MainActivity) context;
        }
        this.mData = getDevicesArrayList();
        this.itemTypeArrayList = new ArrayList<ItemType>();
        itemTypeArrayList.add(new ButtonItemType(ma));
        itemTypeArrayList.addAll(mData);
        this.context = context;
        listener = new MyListener();
    }

    public ArrayList<DeviceItemType> getDevicesArrayList() {
        AppDataBase dbDevices = App.getDatabase();
        DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
        return (ArrayList<DeviceItemType>) devicesDao.getAll();
    }

    public void refreshAdapterData() {
        ArrayList<ItemType> newDataSet = new ArrayList<>();
        newDataSet.add(itemTypeArrayList.get(0));
        var newData = getDevicesArrayList();
        newDataSet.addAll(newData);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemsDiffUtilCallBack(newDataSet, itemTypeArrayList), true);
        this.itemTypeArrayList = newDataSet;
        this.mData = getDevicesArrayList();
        diffResult.dispatchUpdatesTo(this);
    }

    public interface DeviceClickedListener {
        void deviceClicked(DeviceItemType item, View itemView);

        void deviceLongClicked(DeviceItemType item, View itemView);
    }

    @Override
    public void onDeviceClicked(int id, View itemView) {
        listener.deviceClicked(mData.get(id - 1), itemView);
    }

    @Override
    public void onDeviceLongClicked(int id, View itemView) {
        listener.deviceLongClicked(mData.get(id - 1), itemView);
    }

    public class MyListener implements MultipleTypesAdapter.DeviceClickedListener {
        private static final String TAG = "MA_ItemsAdapter";

        @Override
        public void deviceClicked(DeviceItemType item, View itemView) {
            ImageView checkMark = (ImageView) itemView.findViewById(R.id.check_mark);
            MaterialCardView materialCardView = itemView.findViewById(R.id.device_item_card_view);
            mainMenuFragment = ma.getMainMenuFragment();

            selectedDevicesList = new ArrayList<DeviceItemType>();
            for (var i = 1; i < itemTypeArrayList.size(); i++) {
                if (itemTypeArrayList.get(i).getIsSelectedOnScreen()) {
                    selectedDevicesList.add(mData.get(i - 1));
                }
            }
            //проверяю происходит ли выбор списка устройств
            if (selectedDevicesList.size() != 0) {
                Log.d(TAG, "...Список не пуст, нажато устройство...");
                //список не пуст

                //необходимо проверить на присутствие в списке
                boolean wasAlreadySelected = false;
                for (DeviceItemType currentDevice : selectedDevicesList) {
                    if (currentDevice.getDeviceMAC().equals(item.getDeviceMAC())) {
                        selectedDevicesList.remove(currentDevice);
                        itemTypeArrayList.get(itemTypeArrayList.indexOf(item)).setIsSelectedOnScreen(false);
                        wasAlreadySelected = true;
                        Log.d(TAG, "...В списке нашлось это устройство, удаляю...");
                        checkMark.setVisibility(View.GONE);
                        materialCardView.setStrokeColor(Color.TRANSPARENT);

                        break;
                    }
                }

                if (!wasAlreadySelected) {
                    Log.d(TAG, "...В списке не нашлось это устройство, добавляю...");

                    itemTypeArrayList.get(itemTypeArrayList.indexOf(item)).setIsSelectedOnScreen(true);
                    selectedDevicesList.add(item);

                    if (mainMenuFragment != null) {
                        mainMenuFragment.showItemSelectionMenu();
                    }
                    materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.color_accent));
                    checkMark.setVisibility(VISIBLE);
                    ((Animatable) checkMark.getDrawable()).start();
                } else {
                    if (selectedDevicesList.size() == 0) {
                        Log.d(TAG, "...Список очищен...");
                        if (mainMenuFragment != null) {
                            mainMenuFragment.onRefresh();
                        }
                    }
                }

            } else {
                selectedDevicesList.add(item);
                DeviceHandler.setDevicesList(selectedDevicesList);
                Log.d(TAG, "...Список пуст, открываю диалог...");
                //список пуст, открываем диалог для одного устройства
                Bundle args = new Bundle();
                args.putBoolean("isNew", false);
                Navigation.findNavController(itemView).navigate(R.id.deviceMenuFragment, args);
            }
        }

        @Override
        public void deviceLongClicked(DeviceItemType item, View itemView) {
            MaterialCardView materialCardView = itemView.findViewById(R.id.device_item_card_view);
            ImageView checkMark = (ImageView) itemView.findViewById(R.id.check_mark);
            mainMenuFragment = ma.getMainMenuFragment();

            selectedDevicesList = new ArrayList<DeviceItemType>();
            for (var i = 1; i < itemTypeArrayList.size(); i++) {
                if (itemTypeArrayList.get(i).getIsSelectedOnScreen()) {
                    selectedDevicesList.add(mData.get(i - 1));
                }
            }

            if (selectedDevicesList.size() == 0) {
                Log.d(TAG, "...Список пуст, добавляю устройство...");
                itemTypeArrayList.get(itemTypeArrayList.indexOf(item)).setIsSelectedOnScreen(true);
                selectedDevicesList.add(item);
                if (mainMenuFragment != null) {
                    mainMenuFragment.showItemSelectionMenu();
                }
                materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.color_accent));
                checkMark.setVisibility(VISIBLE);
                ((Animatable) checkMark.getDrawable()).start();

            } else {
                {
                    boolean wasAlreadySelected = false;
                    for (int i = 0; i < selectedDevicesList.size(); i++) {
                        if (selectedDevicesList.get(i).getDeviceMAC().equals(item.getDeviceMAC())) {
                            wasAlreadySelected = true;
                        }
                    }
                    if (!wasAlreadySelected) {
                        itemTypeArrayList.get(itemTypeArrayList.indexOf(item)).setIsSelectedOnScreen(true);
                        selectedDevicesList.add(item);
                        materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.color_accent));
                        checkMark.setVisibility(VISIBLE);
                        ((Animatable) checkMark.getDrawable()).start();
                    }
                }

            }
            DeviceHandler.setDevicesList(selectedDevicesList);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return Objects.requireNonNull(ViewHolderFactory.create(parent, viewType, context, this));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        itemTypeArrayList.get(position).onBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return itemTypeArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemTypeArrayList.get(position).getItemViewType();
    }

    public boolean areDevicesConnectable() {
        for (DeviceItemType deviceItemType : selectedDevicesList) {
            if (selectedDevicesList.get(0).getDevClass() != deviceItemType.getDevClass()
                    || selectedDevicesList.get(0).getDevType() != deviceItemType.getDevType()
                    || selectedDevicesList.get(0).getDevProtocol() != deviceItemType.getDevProtocol()) {
                return false;
            }
        }
        return true;
    }

}

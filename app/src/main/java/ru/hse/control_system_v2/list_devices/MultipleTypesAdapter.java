package ru.hse.control_system_v2.list_devices;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.hse.control_system_v2.DialogDevice;
import ru.hse.control_system_v2.MainActivity;
import ru.hse.control_system_v2.MainMenuFragment;
import ru.hse.control_system_v2.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MultipleTypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ViewHolderFactory.ListDevicesHolder.IListener {

    public static List<ItemType> dataSet;
    public static List<DeviceItemType> mData = new ArrayList<>();
    Context context;
    MainActivity ma;
    DeviceClickedListener listener;


    public MultipleTypesAdapter(List<ItemType> dataSet, @NonNull Context context, List <DeviceItemType> devices){
        super();
        MultipleTypesAdapter.dataSet = dataSet;
        mData = devices;
        this.context = context;
        if (context instanceof Activity){
            ma = (MainActivity) context;
        }
        listener = new MyListener();
    }


    public interface DeviceClickedListener {
        void deviceClicked(DeviceItemType item, View itemView);
        void deviceLongClicked(DeviceItemType item, View itemView);
    }

    @Override
    public void onDeviceClicked(int id, View itemView) {
        listener.deviceClicked(mData.get(id-1), itemView);
    }

    @Override
    public void onDeviceLongClicked(int id, View itemView) {
        listener.deviceLongClicked(mData.get(id-1), itemView);
    }
    public class MyListener implements MultipleTypesAdapter.DeviceClickedListener {
        private static final String TAG = "MA_ItemsAdapter";

        @Override
        public void deviceClicked(DeviceItemType item, View itemView) {
            List<DeviceItemType> selectedDevicesList = MainMenuFragment.selectedDevicesList;
            View deviceImage = itemView.findViewById(R.id.icon_image_view);
            View mName = itemView.findViewById(R.id.item_name);
            View checkMark = itemView.findViewById(R.id.check_mark);
            //проверяю происходит ли выбор списка устройств
            if(selectedDevicesList.size() != 0) {
                Log.d(TAG, "...Список не пуст, нажато устройство...");
                //список не пуст
                if (!selectedDevicesList.get(0).getProtocol().equals(item.getProtocol())||
                        !selectedDevicesList.get(0).getDevClass().equals(item.getDevClass())||
                        !selectedDevicesList.get(0).getDevType().equals(item.getDevType()))
                    ma.showToast("Нужны устройства с одним протоколом, классом, типом");
                else {
                    //необходимо проверить на присутствие в списке
                    boolean wasAlreadySelected = false;
                    for (int i = 0; i < selectedDevicesList.size(); i++) {
                        if (selectedDevicesList.get(i).getMAC().equals(item.getMAC())) {
                            MainMenuFragment.selectedDevicesList.remove(i);
                            wasAlreadySelected = true;
                            Log.d(TAG, "...В списке нашлось это устройство, удаляю...");
                            deviceImage.setVisibility(VISIBLE);
                            mName.setAlpha(1f);
                            checkMark.setVisibility(INVISIBLE);
                            break;
                        }
                    }

                    if (!wasAlreadySelected) {
                        Log.d(TAG, "...В списке не нашлось это устройство, добавляю...");
                        MainMenuFragment.selectedDevicesList.add(item);
                        NavHostFragment navHostFragment = (NavHostFragment)((MainActivity) context).getSupportFragmentManager().getPrimaryNavigationFragment();
                        assert navHostFragment != null;
                        FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
                        Fragment current = fragmentManager.getPrimaryNavigationFragment();
                        if(current instanceof MainMenuFragment){
                            MainMenuFragment mainMenuFragment = (MainMenuFragment) current;
                            mainMenuFragment.showDeviceSelectedItems();
                        }
                        deviceImage.setVisibility(INVISIBLE);
                        mName.setAlpha(0.6f);
                        checkMark.setVisibility(VISIBLE);
                    } else {
                        if(selectedDevicesList.size() == 0) {
                            Log.d(TAG, "...Список очищен...");
                            NavHostFragment navHostFragment = (NavHostFragment)((MainActivity) context).getSupportFragmentManager().getPrimaryNavigationFragment();
                            assert navHostFragment != null;
                            FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
                            Fragment current = fragmentManager.getPrimaryNavigationFragment();
                            if(current instanceof MainMenuFragment){
                                MainMenuFragment mainMenuFragment = (MainMenuFragment) current;
                                mainMenuFragment.hideDeviceSelectedItems();
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "...Список пуст, открываю диалог...");
                //список пуст, открываем диалог для одного устройства
                DialogDevice dialog = new DialogDevice(item);
                Bundle args = new Bundle();
                dialog.setArguments(args);
                //fragment.currentDevice = item;
                dialog.show(ma.getSupportFragmentManager(), "dialog");
            }
        }

        @Override
        public void deviceLongClicked(DeviceItemType item, View itemView) {
            List<DeviceItemType> selectedDevicesList = MainMenuFragment.selectedDevicesList;

            View deviceImage = itemView.findViewById(R.id.icon_image_view);
            View deviceName = itemView.findViewById(R.id.item_name);
            View checkMark = itemView.findViewById(R.id.check_mark);
            if (selectedDevicesList.size() == 0) {
                Log.d(TAG, "...Список пуст, добавляю устройство...");
                MainMenuFragment.selectedDevicesList.add(item);
                NavHostFragment navHostFragment = (NavHostFragment)((MainActivity) context).getSupportFragmentManager().getPrimaryNavigationFragment();
                assert navHostFragment != null;
                FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
                Fragment current = fragmentManager.getPrimaryNavigationFragment();
                if(current instanceof MainMenuFragment){
                    MainMenuFragment mainMenuFragment = (MainMenuFragment) current;
                    mainMenuFragment.showDeviceSelectedItems();
                }
                deviceImage.setVisibility(INVISIBLE);
                deviceName.setAlpha(0.6f);
                checkMark.setVisibility(VISIBLE);
            } else {
                if (!selectedDevicesList.get(0).getProtocol().equals(item.getProtocol())||
                        !selectedDevicesList.get(0).getDevClass().equals(item.getDevClass())||
                        !selectedDevicesList.get(0).getDevType().equals(item.getDevType())) {
                    ma.showToast("Нужны устройства с одним протоколом, классом, типом");
                } else {
                    boolean wasAlreadySelected = false;
                    for (int i = 0; i < selectedDevicesList.size(); i++) {
                        if (selectedDevicesList.get(i).getMAC().equals(item.getMAC())) {
                            wasAlreadySelected = true;
                        }
                    }
                    if (!wasAlreadySelected) {
                        MainMenuFragment.selectedDevicesList.add(item);
                        deviceImage.setVisibility(INVISIBLE);
                        deviceName.setAlpha(0.6f);
                        checkMark.setVisibility(VISIBLE);
                    }
                }

            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return Objects.requireNonNull(ViewHolderFactory.create(parent, viewType, context, this));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        dataSet.get(position).onBindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getItemViewType();
    }

}

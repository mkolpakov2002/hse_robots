package ru.hse.control_system_v2.list_devices;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.hse.control_system_v2.DeviceHandler;
import ru.hse.control_system_v2.DialogDevice;
import ru.hse.control_system_v2.MainActivity;
import ru.hse.control_system_v2.MainMenuFragment;
import ru.hse.control_system_v2.R;

public class MultipleTypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ViewHolderFactory.ListDevicesHolder.IListener {

    public ArrayList<ItemType> dataSet;
    public ArrayList<DeviceItemType> mData;
    Context context;
    MainActivity ma;
    MainMenuFragment mainMenuFragment;
    DeviceClickedListener listener;
    ArrayList<DeviceItemType> selectedDevicesList;


    public MultipleTypesAdapter(ArrayList<ItemType> dataSet, @NonNull Context context, ArrayList <DeviceItemType> mData){
        super();
        this.dataSet = dataSet;
        this.mData = mData;
        this.context = context;
        if (context instanceof Activity){
            ma = (MainActivity) context;
        }
        NavHostFragment navHostFragment = (NavHostFragment)(ma).getSupportFragmentManager().getPrimaryNavigationFragment();

        FragmentManager fragmentManager = null;
        if (navHostFragment != null) {
            fragmentManager = navHostFragment.getChildFragmentManager();
        }
        Fragment current = null;
        if (fragmentManager != null) {
            current = fragmentManager.getPrimaryNavigationFragment();
        }
        if(current instanceof MainMenuFragment){
            mainMenuFragment = (MainMenuFragment) current;
        }
        listener = new MyListener();
        selectedDevicesList = new ArrayList<>();
    }

    public void onNewData(ArrayList<DeviceItemType> newDevicesList) {
        ArrayList<ItemType> newDataSet = new ArrayList<>();
        newDataSet.add(dataSet.get(0));
        newDataSet.addAll(newDevicesList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemsDiffUtilCallBack(newDataSet,dataSet),true);
        this.dataSet = newDataSet;
        this.mData = newDevicesList;
        diffResult.dispatchUpdatesTo(this);
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
            ImageView checkMark = (ImageView) itemView.findViewById(R.id.check_mark);
            MaterialCardView materialCardView = itemView.findViewById(R.id.device_item_card_view);

            //проверяю происходит ли выбор списка устройств
            if(selectedDevicesList.size() != 0) {
                Log.d(TAG, "...Список не пуст, нажато устройство...");
                //список не пуст
                if (!selectedDevicesList.get(0).getDevProtocol().equals(item.getDevProtocol())||
                        !selectedDevicesList.get(0).getDevClass().equals(item.getDevClass())||
                        !selectedDevicesList.get(0).getDevType().equals(item.getDevType()))
                    ma.showToast("Нужны устройства с одним протоколом, классом, типом");
                else {
                    //необходимо проверить на присутствие в списке
                    boolean wasAlreadySelected = false;
                    for (DeviceItemType currentDevice: selectedDevicesList) {
                        if (currentDevice.getDeviceMAC().equals(item.getDeviceMAC())) {
                            selectedDevicesList.remove(currentDevice);
                            wasAlreadySelected = true;
                            Log.d(TAG, "...В списке нашлось это устройство, удаляю...");
                            checkMark.setVisibility(View.GONE);
                            materialCardView.setStrokeColor(Color.TRANSPARENT);
                            break;
                        }
                    }

                    if (!wasAlreadySelected) {
                        Log.d(TAG, "...В списке не нашлось это устройство, добавляю...");
                        selectedDevicesList.add(item);
                        item.setIsSelectedOnScreen(true);
                        mainMenuFragment.showFabToStartConnecting();
                        materialCardView.setStrokeColor(ContextCompat.getColor(ma,R.color.colorAccent));
                        checkMark.setVisibility(VISIBLE);
                        ((Animatable) checkMark.getDrawable()).start();

                    } else {
                        item.setIsSelectedOnScreen(false);
                        if(selectedDevicesList.size() == 0) {
                            Log.d(TAG, "...Список очищен...");
                            mainMenuFragment.hideFabToStartConnecting();
                        }
                    }
                }
            } else {
                Log.d(TAG, "...Список пуст, открываю диалог...");
                DeviceHandler.setDevicesList(item);
                //список пуст, открываем диалог для одного устройства
                Navigation.findNavController(mainMenuFragment.requireParentFragment().requireView()).navigateUp();
                Navigation.findNavController(mainMenuFragment.requireParentFragment().requireView()).navigate(R.id.action_mainMenuFragment_to_device_dialog);
            }
        }

        @Override
        public void deviceLongClicked(DeviceItemType item, View itemView) {

            MaterialCardView materialCardView = itemView.findViewById(R.id.device_item_card_view);
            ImageView checkMark = (ImageView) itemView.findViewById(R.id.check_mark);
            if (selectedDevicesList.size() == 0) {
                Log.d(TAG, "...Список пуст, добавляю устройство...");
                selectedDevicesList.add(item);
                item.setIsSelectedOnScreen(true);
                mainMenuFragment.showFabToStartConnecting();
                materialCardView.setStrokeColor(ContextCompat.getColor(ma,R.color.colorAccent));
                checkMark.setVisibility(VISIBLE);
                ((Animatable) checkMark.getDrawable()).start();

            } else {
                if (!selectedDevicesList.get(0).getDevProtocol().equals(item.getDevProtocol())||
                        !selectedDevicesList.get(0).getDevClass().equals(item.getDevClass())||
                        !selectedDevicesList.get(0).getDevType().equals(item.getDevType())) {
                    ma.showToast("Нужны устройства с одним протоколом, классом, типом");
                } else {
                    boolean wasAlreadySelected = false;
                    for (int i = 0; i < selectedDevicesList.size(); i++) {
                        if (selectedDevicesList.get(i).getDeviceMAC().equals(item.getDeviceMAC())) {
                            wasAlreadySelected = true;
                        }
                    }
                    if (!wasAlreadySelected) {
                        selectedDevicesList.add(item);
                        item.setIsSelectedOnScreen(true);
                        materialCardView.setStrokeColor(ContextCompat.getColor(ma,R.color.colorAccent));
                        checkMark.setVisibility(VISIBLE);
                        ((Animatable) checkMark.getDrawable()).start();
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

    public void clearSelected(){
        selectedDevicesList.clear();
    }

    public List<DeviceItemType> getSelectedDevices(){
        return selectedDevicesList;
    }

}

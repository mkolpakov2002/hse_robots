package ru.hse.control_system_v2.ui;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Context;
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
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import ru.hse.control_system_v2.App;
import ru.hse.control_system_v2.Constants;
import ru.hse.control_system_v2.MainActivity;
import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.data.AppDataBase;
import ru.hse.control_system_v2.data.ButtonItemType;
import ru.hse.control_system_v2.data.DeviceItemType;
import ru.hse.control_system_v2.data.DeviceItemTypeDao;
import ru.hse.control_system_v2.data.ItemType;

public class MultipleTypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ViewHolderFactory.ListDevicesHolder.IListener {

    private ArrayList<ItemType> itemTypeArrayList;
    private java.util.List<java.util.Map.Entry<Boolean,DeviceItemType>> mData;
    private final Context context;
    private MainActivity ma;
    private MainMenuFragment mainMenuFragment;
    private final DeviceClickedListener listener;


    public MultipleTypesAdapter(@NonNull Context context) {
        super();
        if (context instanceof Activity) {
            ma = (MainActivity) context;
        }
        mData = new java.util.ArrayList<>();
        this.itemTypeArrayList = new ArrayList<>();
        itemTypeArrayList.add(new ButtonItemType(ma));
        for(DeviceItemType itemType: getDevicesArrayList()){
            java.util.Map.Entry<Boolean, DeviceItemType> pair = new java.util.AbstractMap.SimpleEntry<>(false, itemType);
            this.mData.add(pair);
            this.itemTypeArrayList.add(itemType);
        }
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
        this.mData.clear();
        for(DeviceItemType itemType: getDevicesArrayList()){
            this.mData.add(new java.util.AbstractMap.SimpleEntry<>(false, itemType));
        }
        diffResult.dispatchUpdatesTo(this);
    }

    public interface DeviceClickedListener {
        void deviceClicked(DeviceItemType item, View itemView);
        void deviceLongClicked(DeviceItemType item, View itemView);
    }

    @Override
    public void onDeviceClicked(int id, View itemView) {
        listener.deviceClicked(mData.get(id - 1).getValue(), itemView);
    }

    @Override
    public void onDeviceLongClicked(int id, View itemView) {
        listener.deviceLongClicked(mData.get(id - 1).getValue(), itemView);
    }

    public class MyListener implements MultipleTypesAdapter.DeviceClickedListener {
        private static final String TAG = "MA_ItemsAdapter";

        @Override
        public void deviceClicked(DeviceItemType item, View itemView) {
            ImageView checkMark = (ImageView) itemView.findViewById(R.id.check_mark);
            MaterialCardView materialCardView = itemView.findViewById(R.id.device_item_card_view);
            mainMenuFragment = ma.getMainMenuFragment();

            boolean flag = false;
            boolean flagWasSelected = false;
            int position = -1;
            for(int i = 0; i<mData.size(); i++){
                if(mData.get(i).getKey()){
                    flag = true;
                    if(mData.get(i).getValue().getDevId()==item.getDevId()){
                        flagWasSelected = true;
                        position = i;
                    }
                }
            }

            //проверяю происходит ли выбор списка устройств
            if (flag) {
                Log.d(TAG, "...Список не пуст, нажато устройство...");
                //список не пуст

                //необходимо проверить на присутствие в списке
                if (flagWasSelected) {
                    mData.set(position, new java.util.AbstractMap.SimpleEntry<>(false, item));
                    Log.d(TAG, "...В списке нашлось это устройство, удаляю...");
                    checkMark.setVisibility(View.GONE);
                    materialCardView.setStrokeColor(Color.TRANSPARENT);
                } else {
                    Log.d(TAG, "...В списке не нашлось это устройство, добавляю...");
                    mData.set(position, new java.util.AbstractMap.SimpleEntry<>(true, item));

                    if (mainMenuFragment != null) {
                        mainMenuFragment.showItemSelectionMenu();
                    }
                    materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.color_accent));
                    checkMark.setVisibility(VISIBLE);
                    ((Animatable) checkMark.getDrawable()).start();
                }

            } else {
                Log.d(TAG, "...Список пуст, открываю диалог...");
                //список пуст, открываем диалог для одного устройства
                Bundle args = new Bundle();
                args.putBoolean("isNew", false);
                args.putSerializable("device", item);
                Navigation.findNavController(itemView).navigate(R.id.deviceMenuFragment, args);
            }
        }

        @Override
        public void deviceLongClicked(DeviceItemType item, View itemView) {
            MaterialCardView materialCardView = itemView.findViewById(R.id.device_item_card_view);
            ImageView checkMark = (ImageView) itemView.findViewById(R.id.check_mark);
            mainMenuFragment = ma.getMainMenuFragment();

            boolean flag = false;
            boolean flagWasSelected = false;
            int position = -1;
            for(int i = 0; i<mData.size(); i++){
                if(mData.get(i).getKey()){
                    flag = true;
                    if(mData.get(i).getValue().getDevId()==item.getDevId()){
                        flagWasSelected = true;
                        position = i;
                    }
                }
            }

            if (!flag) {
                Log.d(TAG, "...Список пуст, добавляю устройство...");
                mData.set(position, new java.util.AbstractMap.SimpleEntry<>(true, item));
                if (mainMenuFragment != null) {
                    mainMenuFragment.showItemSelectionMenu();
                }
                materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.color_accent));
                checkMark.setVisibility(VISIBLE);
                ((Animatable) checkMark.getDrawable()).start();

            } else {
                if (!flagWasSelected) {
                    mData.set(position, new java.util.AbstractMap.SimpleEntry<>(true, item));
                    materialCardView.setStrokeColor(ContextCompat.getColor(ma, R.color.color_accent));
                    checkMark.setVisibility(VISIBLE);
                    ((Animatable) checkMark.getDrawable()).start();
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
        if(itemTypeArrayList.get(position) instanceof DeviceItemType curr){
            ViewHolderFactory.ListDevicesHolder mViewHolder = (ViewHolderFactory.ListDevicesHolder) holder;
            mViewHolder.mName.setText(curr.getDevName());
            mViewHolder.checkMark.setVisibility(View.GONE);
            mViewHolder.materialCardView.setStrokeColor(Color.TRANSPARENT);
            if (curr.getDevClass().equals("class_arduino")) {
                switch (curr.getImageType()) {
                    case "type_computer":
                        mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                        break;
                    case "type_sphere":
                        //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                        break;
                    case "type_anthropomorphic":
                        //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                        break;
                    case "type_cubbi":
                        mViewHolder.deviceImage.setImageResource(R.drawable.type_cubbi);
                        break;
                    case "no_type":
                        mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
                        break;
                }
            } else {
                switch (curr.getImageType()) {
                    case "class_android" -> mViewHolder.deviceImage.setImageResource(R.drawable.class_android);
                    case "no_class" -> mViewHolder.deviceImage.setImageResource(R.drawable.type_no_type);
                    case "class_computer" -> mViewHolder.deviceImage.setImageResource(R.drawable.class_computer);
                }
            }
            mViewHolder.deviceImage.setVisibility(View.VISIBLE);

            if (curr.isWiFiSupported()) {
                mViewHolder.wifiSupportIcon.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.wifiSupportIcon.setVisibility(View.GONE);
            }

            if (curr.isBtSupported()) {
                mViewHolder.btSupportIcon.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.btSupportIcon.setVisibility(View.GONE);
            }
        } else if(itemTypeArrayList.get(position) instanceof ButtonItemType curr){
            ViewHolderFactory.ButtonViewHolder buttonViewHolder = (ViewHolderFactory.ButtonViewHolder) holder;
            buttonViewHolder.buttonLayout.setOnClickListener(getOnButtonClickListener());
            buttonViewHolder.buttonTextInfo.setText(curr.getTextInfo());
        }
    }

    public View.OnClickListener getOnButtonClickListener() {
        return v -> {
            mainMenuFragment = ma.getMainMenuFragment();
            if (mainMenuFragment != null) {
                mainMenuFragment.showBottomSheetToAdd();
            }
        };
    }

    @Override
    public int getItemCount() {
        return itemTypeArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(itemTypeArrayList.get(position) instanceof DeviceItemType)
            return Constants.DEVICE_ITEM_TYPE;
        else return Constants.BUTTON_ITEM_TYPE;
    }

    public boolean areDevicesConnectable() {
//        for (DeviceItemType deviceItemType : selectedDevicesList) {
//            if (!selectedDevicesList.get(0).getDevClass().equals(deviceItemType.getDevClass())
//                    || !selectedDevicesList.get(0).getDevType().equals(deviceItemType.getDevType())
//                    || !selectedDevicesList.get(0).getDevProtocol().equals(deviceItemType.getDevProtocol())) {
//                return false;
//            }
//        }
        return true;
    }

}

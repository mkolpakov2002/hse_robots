package ru.hse.control_system_v2;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;


public class DialogSaveDeviceWithMAC extends DialogFragment {
    private Context c;
    private String mPreviousMac = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            c = context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_mac, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(c, R.style.AlertDialog_AppTheme);
        builder.setView(dialogView);
        EditText editTextMACAlert = dialogView.findViewById(R.id.editDeviceMAC);
        editTextMACAlert.setInputType(InputType.TYPE_CLASS_TEXT);

        editTextMACAlert.addTextChangedListener(new TextWatcher() {
            //https://github.com/r-cohen/macaddress-edittext
            private void setMacEdit(String cleanMac, String formattedMac, int selectionStart, int lengthDiff) {
                editTextMACAlert.removeTextChangedListener(this);
                if (cleanMac.length() <= 12) {
                    editTextMACAlert.setText(formattedMac);
                    editTextMACAlert.setSelection(selectionStart + lengthDiff);
                    mPreviousMac = formattedMac;
                } else {
                    editTextMACAlert.setText(mPreviousMac);
                    editTextMACAlert.setSelection(mPreviousMac.length());
                }
                editTextMACAlert.addTextChangedListener(this);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!editTextMACAlert.getText().toString().equals(editTextMACAlert.getText().toString()))
                {
                    String upperText = editTextMACAlert.getText().toString().toUpperCase();
                    editTextMACAlert.setText(upperText);
                    editTextMACAlert.setSelection(editTextMACAlert.length()); //fix reverse texting
                }
                String enteredMac = editTextMACAlert.getText().toString().toUpperCase();
                String cleanMac = clearNonMacCharacters(enteredMac);
                String formattedMac = formatMacAddress(cleanMac);
                int selectionStart = editTextMACAlert.getSelectionStart();
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart);
                int lengthDiff = formattedMac.length() - enteredMac.length();
                setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        MaterialButton buttonToCancel = dialogView.findViewById(R.id.dialog_mac_cancel);
        MaterialButton buttonToAccept = dialogView.findViewById(R.id.dialog_mac_accept);

        android.app.AlertDialog alertDialog = builder.create();

        buttonToCancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        buttonToAccept.setOnClickListener(view -> {
            String macAddr = editTextMACAlert.getText().toString();
            if(macAddr.length()==0){
                editTextMACAlert.requestFocus();
                editTextMACAlert.setError(getString(R.string.error_empty));
            } else if(!BluetoothAdapter.checkBluetoothAddress(macAddr)){
                editTextMACAlert.requestFocus();
                editTextMACAlert.setError(getString(R.string.error_mac));
            } else {
                alertDialog.dismiss();
                DialogDeviceEdit alertDialogSave = new DialogDeviceEdit(null,macAddr);
                Bundle args = new Bundle();
                alertDialogSave.setArguments(args);
                //fragment.currentDevice = item;
                alertDialogSave.show(((MainActivity) c).getSupportFragmentManager(), "dialog");

            }
        });

        return alertDialog;
    }

    private String handleColonDeletion(String enteredMac, String formattedMac, int selectionStart) {
        if (mPreviousMac != null && mPreviousMac.length() > 1) {
            int previousColonCount = colonCount(mPreviousMac);
            int currentColonCount = colonCount(enteredMac);

            if (currentColonCount < previousColonCount) {
                formattedMac = formattedMac.substring(0, selectionStart - 1) + formattedMac.substring(selectionStart);
                String cleanMac = clearNonMacCharacters(formattedMac);
                formattedMac = formatMacAddress(cleanMac);
            }
        }
        return formattedMac;
    }

    private static String formatMacAddress(String cleanMac) {
        int grouppedCharacters = 0;
        StringBuilder formattedMac = new StringBuilder();
        for (int i = 0; i < cleanMac.length(); ++i) {
            formattedMac.append(cleanMac.charAt(i));
            ++grouppedCharacters;
            if (grouppedCharacters == 2) {
                formattedMac.append(":");
                grouppedCharacters = 0;
            }
        }
        if (cleanMac.length() == 12) {
            formattedMac = new StringBuilder(formattedMac.substring(0, formattedMac.length() - 1));
        }
        return formattedMac.toString();
    }

    private static String clearNonMacCharacters(String mac) {
        return mac.replaceAll("[^A-Fa-f0-9]", "");
    }

    private static int colonCount(String formattedMac) {
        return formattedMac.replaceAll("[^:]", "").length();
    }
}

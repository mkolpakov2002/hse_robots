package ru.hse.control_system_v2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class OneButtonAlertDialogFragment extends DialogFragment {
    private static String mTitle = "title";
    private static String mMessage = "message";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null)
            return;

        mTitle = args.getString("dialogTitle", "title");
        mMessage = args.getString("dialogText", "message");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle).setTitle(mTitle).setMessage(mMessage).setPositiveButton(R.string.ok, null).create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        Object parent = getParentFragment();
        if (parent == null)
            parent = requireActivity();

        if (parent instanceof OnDismissListener && getTag() != null)
            ((OnDismissListener) parent).onDialogDismissed(getTag());

    }

    public interface OnDismissListener {

        void onDialogDismissed(@NonNull String dialogTag);

    }
}
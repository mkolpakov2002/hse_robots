package ru.hse.control_system_v2.fragment;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

import ru.hse.control_system_v2.R;

public class OneButtonAlertDialogFragment extends DialogFragment {
    private String mTitle = "title";
    private String mMessage = "message";
    OnDismissListener onDismissListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDismissListener) {
            onDismissListener = (OnDismissListener) context;
        } else {
            Log.d(APP_LOG_TAG, "no OneButtonAlertDialogFragment.OnDismissListener");
        }
    }

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
        onDismissListener.onDialogDismissed();
    }

    public interface OnDismissListener {
        void onDialogDismissed();
    }
}
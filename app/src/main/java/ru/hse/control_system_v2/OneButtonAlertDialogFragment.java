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

public class OneButtonAlertDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private CharSequence mTitle;
    private CharSequence mMessage;

    public static OneButtonAlertDialogFragment newInstance(Context c, @StringRes int title, @StringRes int message) {
        return newInstance(c.getText(title), c.getText(message));
    }

    public static OneButtonAlertDialogFragment newInstance(CharSequence title, CharSequence message) {
        OneButtonAlertDialogFragment fragment = new OneButtonAlertDialogFragment();
        Bundle args = new Bundle();
        args.putCharSequence(ARG_TITLE, title);
        args.putCharSequence(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null)
            return;

        mTitle = args.getCharSequence(ARG_TITLE, "title");
        mMessage = args.getCharSequence(ARG_MESSAGE, "message");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext(), R.style.AlertDialog_AppTheme).setTitle(mTitle).setMessage(mMessage).setPositiveButton(R.string.ok, null).create();
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
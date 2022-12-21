package ru.hse.control_system_v2.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import ru.hse.control_system_v2.App;
import ru.hse.control_system_v2.R;

public class DialogDataParams extends Fragment {
    Context c;
    private TextInputEditText editTextNumberCommandFirstChar, editTextNumberCommandLastChar,
            editTextStringCommandFirstChar, editTextStringCommandLastChar;
    MaterialButton saveButton;
    private Toolbar toolbar;
    private View view;

    public DialogDataParams() {
        //nothing
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            c = context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.dialog_send_data_params, container, false);
        }
        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveButton = view.findViewById(R.id.button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.updateDataParams(editTextNumberCommandFirstChar.getText().toString().trim(),
                        editTextNumberCommandLastChar.getText().toString().trim(),
                        editTextStringCommandFirstChar.getText().toString().trim(),
                        editTextStringCommandLastChar.getText().toString().trim());
                Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.settingsFragment);
            }
        });
        editTextNumberCommandFirstChar = view.findViewById(R.id.editTextNumberCommandFirstChar);
        editTextNumberCommandFirstChar.setText(App.getNumberCommandFirstChar());
        editTextNumberCommandFirstChar.addTextChangedListener(new TextChangedListener<>(editTextNumberCommandFirstChar) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                onRefresh();
            }
        });

        editTextNumberCommandLastChar = view.findViewById(R.id.editTextNumberCommandLastChar);
        editTextNumberCommandLastChar.setText(App.getNumberCommandLastChar());
        editTextNumberCommandLastChar.addTextChangedListener(new TextChangedListener<>(editTextNumberCommandLastChar) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                onRefresh();
            }
        });

        editTextStringCommandFirstChar = view.findViewById(R.id.editTextStringCommandFirstChar);
        editTextStringCommandFirstChar.setText(App.getStringCommandFirstChar());
        editTextStringCommandFirstChar.addTextChangedListener(new TextChangedListener<>(editTextStringCommandFirstChar) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                onRefresh();
            }
        });

        editTextStringCommandLastChar = view.findViewById(R.id.editTextStringCommandLastChar);
        editTextStringCommandLastChar.setText(App.getStringCommandLastChar());
        editTextStringCommandLastChar.addTextChangedListener(new TextChangedListener<>(editTextStringCommandLastChar) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                onRefresh();
            }
        });
        onRefresh();

    }


    void onRefresh(){
        saveButton.setEnabled(!editTextNumberCommandFirstChar.getText().toString().trim().equals(App.getNumberCommandFirstChar())
                || !editTextNumberCommandLastChar.getText().toString().trim().equals(App.getNumberCommandLastChar())
                || !editTextStringCommandFirstChar.getText().toString().trim().equals(App.getStringCommandFirstChar())
                || !editTextStringCommandLastChar.getText().toString().trim().equals(App.getStringCommandLastChar()));
    }

}

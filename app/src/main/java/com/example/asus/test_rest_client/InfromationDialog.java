package com.example.asus.test_rest_client;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.asus.test_rest_client.model.User;

import java.util.HashMap;


public class InfromationDialog extends DialogFragment {

    private PatchingListener patchingListener;

    public interface PatchingListener{
        void OnTaskAdded(HashMap<String, Object> map);
        void OnTaskCancel();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        patchingListener = (PatchingListener) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        patchingListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle("Information");
        View container = inflater.inflate(R.layout.dialog_fragment, null);
        final TextInputLayout tilName= (TextInputLayout) container.findViewById(R.id.tilDialogName);
        final EditText edName= tilName.getEditText();
        edName.setText(getArguments().getString("Name"));
        final TextInputLayout tilAbout= (TextInputLayout) container.findViewById(R.id.tilDialogAbout);
        final EditText edAbout= tilAbout.getEditText();
        edAbout.setText(getArguments().getString("About"));
        builder.setView(container);
        builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", edName.getText().toString());
                map.put("about_me", edAbout.getText().toString());
                hideKeyboard();
                patchingListener.OnTaskAdded(map);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideKeyboard();
                patchingListener.OnTaskCancel();
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final Button positivebutton= ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
                positivebutton.setEnabled(false);
               edName.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   }

                   @Override
                   public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(charSequence.length()!=0){
                            positivebutton.setEnabled(true);
                        }
                       else{
                            positivebutton.setEnabled(false);
                            edName.setError(getString(R.string.dialog_error));
                        }
                   }

                   @Override
                   public void afterTextChanged(Editable editable) {

                   }
               });
                edAbout.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        positivebutton.setEnabled(true);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });
        return alertDialog;
    }
    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

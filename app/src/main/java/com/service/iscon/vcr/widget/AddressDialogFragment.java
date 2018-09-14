package com.service.iscon.vcr.widget;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Dictionary;

/**
 * Created by root on 13/9/18.
 */

public class AddressDialogFragment extends DialogFragment {


    public static AddressDialogFragment newInstance(Context context) {
        AddressDialogFragment fragment = new AddressDialogFragment();

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
            }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }
}

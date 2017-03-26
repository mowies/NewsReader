package com.xtracteddev.newsreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class InfoFragment extends DialogFragment{

    private static final String KEY_TITLE = "key-title";
    private static final String KEY_MESSAGE = "key-message";

        public static InfoFragment getInstance(String title, String message) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_TITLE, title);
            bundle.putString(KEY_MESSAGE, message);
            InfoFragment fragment = new InfoFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getArguments().getString(KEY_TITLE))
                    .setMessage(getArguments().getString(KEY_MESSAGE))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // closed by user
                        }
                    });
            return builder.create();
        }
}

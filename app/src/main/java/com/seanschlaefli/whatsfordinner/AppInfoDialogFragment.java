package com.seanschlaefli.whatsfordinner;

import com.seanschlaefli.whatsfordinner.BuildConfig;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AppInfoDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_appinfo, null);
        TextView author = (TextView) v.findViewById(R.id.author_id);
        TextView url = (TextView) v.findViewById(R.id.url_id);
        TextView version = (TextView) v.findViewById(R.id.version_id);
        TextView copyright = (TextView) v.findViewById(R.id.copyright_id);

        Resources r = getResources();

        author.setText(r.getString(R.string.author));
        url.setText(r.getString(R.string.url_info));
        version.setText(r.getString(R.string.version) + Integer.toString(BuildConfig.VERSION_CODE));
        copyright.setText(r.getString(R.string.copyright));
        return v;
    }

    public static AppInfoDialogFragment newInstance() {
        return new AppInfoDialogFragment();
    }
}

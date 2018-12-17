package com.seanschlaefli.whatsfordinner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ImageDialogFragment extends DialogFragment {

    private EditText mURL;
    private EditText mFilePath;
    private Button mDownload;
    private Button mFindFile;
    private Button mCancel;


    private OnURLSelectedListener mCallback;

    public interface OnURLSelectedListener {
         void onURLSelected(String URL);
         void onLocalFileSelected(String path);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image_selection, null);

        mURL = (EditText) v.findViewById(R.id.download_edit_text);
        mDownload = (Button) v.findViewById(R.id.download_button_id);
        mFilePath = (EditText) v.findViewById(R.id.local_file_edit_text);
        mCancel = (Button) v.findViewById(R.id.cancel_button_id);
        mFindFile = (Button) v.findViewById(R.id.local_button_id);


        mDownload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCallback.onURLSelected(mURL.getText().toString());
                dismiss();
            }
        });

        mFindFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onLocalFileSelected(mFilePath.getText().toString());
                dismiss();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (OnURLSelectedListener) context;
    }

    public static ImageDialogFragment newInstance() {
        return new ImageDialogFragment();
    }

}

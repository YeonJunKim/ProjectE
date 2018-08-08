package com.example.yeonjun.uidesign;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class DrowsyFragment extends Fragment {

    Button testButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("dddd", "onCreateView: ??????????????????????");
        return inflater.inflate(R.layout.fragment_drowsy, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Log.d("dddd", "onViewCreated: ??????????????????????");
//        testButton = (Button) getView().findViewById(R.id.button);
//
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MySingletone.getInstance().ShowToastMessage("drowsy fragment", getActivity());
//            }
//        });
    }
}

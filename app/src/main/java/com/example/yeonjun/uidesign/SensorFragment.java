package com.example.yeonjun.uidesign;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SensorFragment extends Fragment {

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        String[] listItems = {"Do", "Do more", "Harder", "Do", "Do more", "Harder", "Do", "Do more", "Harder", "Do", "Do more", "Harder", "Do", "Do more", "Harder", "Do", "Do more", "Harder"};

        listView = (ListView)view.findViewById(R.id.listView);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                String s = (String)listView.getItemAtPosition(position);
                MySingletone.getInstance().ShowToastMessage(s, getActivity().getApplicationContext());
            }
        });

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                listItems
                );

        listView.setAdapter(listViewAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

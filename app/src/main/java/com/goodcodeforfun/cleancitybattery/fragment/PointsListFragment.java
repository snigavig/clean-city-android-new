package com.goodcodeforfun.cleancitybattery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.activity.MainActivity;

import java.lang.ref.WeakReference;

public class PointsListFragment extends Fragment {

    private WeakReference<MainActivity> mainActivityWeakReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mainActivityWeakReference = new WeakReference<>((MainActivity) getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_map_view).setVisible(true);
        menu.findItem(R.id.action_list_view).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_map_view) {
            MainActivity activity = mainActivityWeakReference.get();
            activity.showMap();
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.points_list_fragment, container, false);
    }
}
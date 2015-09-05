package com.goodcodeforfun.cleancitybattery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

public class PointsListFragment extends Fragment {

    private WeakReference<MainActivity> mainActivityWeakReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityWeakReference = new WeakReference<>((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.points_list_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        @SuppressLint("PrivateResource") final Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        upArrow.setColorFilter(getResources().getColor(R.color.half_black, getActivity().getTheme()), PorterDuff.Mode.SRC_ATOP);
        MainActivity activity = mainActivityWeakReference.get();
//        if (null != activity) {
//            activity.getDrawerToggle().setHomeAsUpIndicator(upArrow);
//            activity.getDrawerToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    getChildFragmentManager().popBackStackImmediate();
//                }
//            });
//            ActionBar actionBar = activity.getSupportActionBar();
//            if (null != actionBar) {
//                actionBar.setDisplayShowHomeEnabled(true);
//                actionBar.setDisplayHomeAsUpEnabled(true);
//            }
//        }
    }
}
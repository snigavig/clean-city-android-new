package com.goodcodeforfun.cleancitybattery.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.goodcodeforfun.cleancitybattery.CleanCityApplication;
import com.goodcodeforfun.cleancitybattery.R;
import com.goodcodeforfun.cleancitybattery.activity.MainActivity;
import com.goodcodeforfun.cleancitybattery.model.Location;
import com.goodcodeforfun.cleancitybattery.util.DeviceStateHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snigavig on 27.09.15.
 */
public class LocationDetailsFragment extends Fragment {

    private WeakReference<MainActivity> mainActivityWeakReference;
    private TextView mNameView;
    private ScrollGalleryView mScrollGalleryView;
    private ImageView mArrow;
    private String mCurrentLocation;
    private List<Target> targetList = new ArrayList<>();
    private ViewPager viewPager;

    public ImageView getArrow() {
        return mArrow;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(false);
        mainActivityWeakReference = new WeakReference<>((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_details_fragment, container, false);
        mNameView = (TextView) rootView.findViewById(R.id.nameTextView);
        mArrow = (ImageView) rootView.findViewById(R.id.arrowImageView);
        mScrollGalleryView = (ScrollGalleryView) rootView.findViewById(R.id.scroll_gallery_view);
        mScrollGalleryView
                .setThumbnailSize(DeviceStateHelper.convertDpToPixel(50))
                .setZoom(true)
                .setFragmentManager(getChildFragmentManager());
        viewPager = getViewPager();
        return rootView;
    }

    public void setCurrentLocation(String mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
        final Location location = new Select().from(Location.class).where(Location.COLUMN_API_ID + " = ?", mCurrentLocation).executeSingle();
        if (null != location.getPhotos()) {
            for (final String photoUrl : location.getPhotos()) {
                Target target = new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mScrollGalleryView.addImage(bitmap);
                        targetList.remove(this);
                        focusGallery();
                    }

                    @Override
                    public void onBitmapFailed(final Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(final Drawable placeHolderDrawable) {
                        Log.d("TAG", "Prepare Load");
                    }
                };

                targetList.add(target);
                CleanCityApplication.getInstance().getPicasso().load(photoUrl).into(target);
            }
        }
    }

    private ViewPager getViewPager() {
        //Serious stuff going on down there
        Field f;
        try {
            f = mScrollGalleryView.getClass().getDeclaredField("viewPager");
            f.setAccessible(true);
            try {
                return (ViewPager) f.get(mScrollGalleryView);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void focusGallery() {
        viewPager.setCurrentItem(0, false);
    }
}

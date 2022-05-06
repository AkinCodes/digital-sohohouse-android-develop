package com.sohohouse.seven.base.mvpimplementation;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This fragment survives the configuration changes and saves object instance inside a {@link Map}
 */
public final class RetainedFragment extends Fragment {

    private final Map<String, Object> mDataMap = new HashMap<>();

    private final List<OnDestroyListener> onDestroyListenerList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void put(String keyName, Object value) {
        mDataMap.put(keyName, value);
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T> T getValue(String keyName) {
        return (T) mDataMap.get(keyName);
    }

    @SuppressWarnings("unused")
    public boolean contains(String keyName) {
        return mDataMap.containsKey(keyName);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String keyName) {
        return (T) mDataMap.get(keyName);
    }


    public void addOnDestroyListener(OnDestroyListener onDestroyListener) {
        if (!onDestroyListenerList.contains(onDestroyListener)) {
            onDestroyListenerList.add(onDestroyListener);
        }
    }

    public interface OnDestroyListener {
        void onDestroyRetainedFragment();
    }
}

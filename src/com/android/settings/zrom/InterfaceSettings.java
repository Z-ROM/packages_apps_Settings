/*
 * Copyright (C) 2014 Z-ROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.zrom;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFrameLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Arrays;

import com.android.settings.zrom.AdvancedSettings;
import com.android.settings.zrom.ScreenAndAnimations;
import com.android.settings.zrom.MiscTweaks;
import com.android.settings.zrom.LockscreenSettings;
import com.android.settings.zrom.NotificationDrawerQsSettings;
import com.android.settings.zrom.RecentsPanel;
import com.android.settings.zrom.StatusBar;

public class InterfaceSettings extends SettingsPreferenceFragment {
    // layout inflater object used to inflate views
    private LayoutInflater mInflater;
    
    private ViewGroup mContentContainer;
    private View mRootView;
    private ViewPager mViewPager;

    CharSequence[] mPageNames;

    int mCurPos;

    class MyPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if (position==0)
            {
                 fragment = new MiscTweaks();
            }
            if (position==1)
            {
                 fragment = new NotificationDrawerQsSettings();
            }
            if (position==2)
            {
                 fragment = new RecentsPanel();
            }
            if (position==3)
            {
                 fragment = new LockscreenSettings();
            }
            if (position==4)
            {
                 fragment = new StatusBar();
            }
            if (position==5)
            {
                 fragment = new ScreenAndAnimations();
            }
            if (position==6)
            {
                 fragment = new AdvancedSettings();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageNames[position];
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mCurPos = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                //updateCurrentTab(mCurPos);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {

        // initialize the inflater
        mInflater = inflater;

        View rootView = mInflater.inflate(R.layout.tabs_interface,
                container, false);
        mContentContainer = container;
        mRootView = rootView;

        mPageNames = getResources().getTextArray(R.array.interface_tabs_catogory);

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(adapter);
        PagerTabStrip tabs = (PagerTabStrip) rootView.findViewById(R.id.tabs);
        tabs.setTabIndicatorColorResource(R.color.tab_selector);
        tabs.setTextColor(R.color.tab_selector);
        tabs.setBackgroundColor(Color.TRANSPARENT);

        // We have to do this now because PreferenceFrameLayout looks at it
        // only when the view is added.
        if (container instanceof PreferenceFrameLayout) {
            ((PreferenceFrameLayout.LayoutParams) rootView.getLayoutParams()).removeBorders = true;
        }

        return rootView;
    }
}

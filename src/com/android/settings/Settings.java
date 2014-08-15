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

package com.android.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.CompatibilityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.Display;

import com.android.internal.util.ArrayUtils;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accessibility.CaptionPropertiesFragment;
import com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment;
import com.android.settings.accounts.AccountSyncSettings;
import com.android.settings.accounts.AuthenticatorHelper;
import com.android.settings.accounts.ManageAccountsSettings;
import com.android.settings.applications.AppOpsSummary;
import com.android.settings.applications.ManageApplications;
import com.android.settings.applications.ProcessStatsUi;
import com.android.settings.bluetooth.BluetoothEnabler;
import com.android.settings.bluetooth.BluetoothSettings;
import com.android.settings.deviceinfo.Memory;
import com.android.settings.deviceinfo.UsbSettings;
import com.android.settings.fuelgauge.PowerUsageSummary;
import com.android.settings.inputmethod.InputMethodAndLanguageSettings;
import com.android.settings.inputmethod.KeyboardLayoutPickerFragment;
import com.android.settings.inputmethod.SpellCheckersSettings;
import com.android.settings.inputmethod.UserDictionaryList;
import com.android.settings.location.LocationEnabler;
import com.android.settings.location.LocationSettings;
import com.android.settings.net.MobileDataEnabler;
import com.android.settings.nfc.AndroidBeam;
import com.android.settings.nfc.PaymentSettings;
import com.android.settings.print.PrintJobSettingsFragment;
import com.android.settings.print.PrintServiceSettingsFragment;
import com.android.settings.print.PrintSettingsFragment;
import com.android.settings.zrom.AdvancedSettings;
import com.android.settings.zrom.BatteryIconStyle;
import com.android.settings.zrom.blacklist.BlacklistSettings;
import com.android.settings.zrom.DisplayRotation;
import com.android.settings.zrom.InterfaceSettings;
import com.android.settings.zrom.NavigationSettings;
import com.android.settings.zrom.quicksettings.QuickSettingsTiles;
import com.android.settings.zrom.ShakeEvents;
import com.android.settings.zrom.QuietHours;
import com.android.settings.zrom.themes.ThemeEnabler;
import com.android.settings.zrom.themes.ThemeSettings;
import com.android.settings.search.SettingsAutoCompleteTextView;
import com.android.settings.search.SearchPopulator;
import com.android.settings.search.SettingsSearchFilterAdapter;
import com.android.settings.search.SettingsSearchFilterAdapter.SearchInfo;
import com.android.settings.tts.TextToSpeechSettings;
import com.android.settings.users.UserSettings;
import com.android.settings.vpn2.VpnSettings;
import com.android.settings.wfd.WifiDisplaySettings;
import com.android.settings.wifi.AdvancedWifiSettings;
import com.android.settings.wifi.WifiEnabler;
import com.android.settings.wifi.WifiSettings;
import com.android.settings.wifi.p2p.WifiP2pSettings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Settings extends PreferenceActivity implements OnItemClickListener {

    private ViewPager mPager;
    private List<View> listViews;
    private TextView z_tab, system_tab;
    private int offset = 0;
    private int currIndex = 0;
    private int bmpW;
    private LocalActivityManager localManager;
    private LayoutInflater mInflater;
    private ViewPagerAdapter mPagerAdapter;
    private static final String[] titles = { "System", "Z-Settings" };
    private int mLayout = 0;

    /**
     * Private variables
     */

    private static final String ZROM_DPI_SUFFIX = ".dpi";
    private static final String ZROM_PREFIX = "%";
    private static final String ZROM_LAYOUT_SUFFIX = ".layout";

    private ActionBar mActionBar;
    private MenuItem mSearchItem;
    private SettingsAutoCompleteTextView mSearchBar;

    private int mCurrentState = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // We only want to inflate the search menu item in the top-level activity
        if (getClass() != Settings.class) {
            return false;
        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_search, menu);
        mSearchItem = menu.findItem(R.id.action_search);
        final InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mSearchBar = (SettingsAutoCompleteTextView) mSearchItem.getActionView();
        mSearchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSearchBar.clearFocus();
                mSearchBar.setText("");
                imm.hideSoftInputFromWindow(mSearchBar.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchBar.requestFocus();
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY);
                return true;
            }
        });

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT);

        mSearchBar.setLayoutParams(layoutParams);
        mSearchBar.setHint(R.string.settings_search_autocompleteview_hint);
        mSearchBar.setThreshold(1);
        mSearchBar.setOnItemClickListener(this);
        mSearchBar.setAdapter(new SettingsSearchFilterAdapter(this));

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabsettings);
        localManager = new LocalActivityManager(this, true);
        localManager.dispatchCreate(savedInstanceState);

        mActionBar = getActionBar();
        mActionBar.setDisplayShowCustomEnabled(true);

        InitViewPager();

    }

    private void InitViewPager() {
        mInflater = getLayoutInflater();
        mPager = (ViewPager) findViewById(R.id.viewPager);
        listViews = new ArrayList<View>();
        Intent SystemIntent = new Intent(this, TabSettings.class);
        listViews.add(localManager.startActivity("SystemSettings",
                SystemIntent).getDecorView());
        Intent ZSettingsIntent = new Intent(this, ZSettings.class);
        listViews.add(localManager.startActivity("Z-Settings",
                ZSettingsIntent).getDecorView());
        mPagerAdapter = new ViewPagerAdapter(listViews);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void onBackPressed() {
        if (mSearchBar != null && mSearchBar.hasFocus()) {
            mSearchBar.clearFocus();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSearchBar != null) {
            mSearchBar.clearFocus();
        }

        if (mSearchItem != null) {
            mSearchItem.collapseActionView();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mSearchBar != null) {
            mSearchBar.clearFocus();
        }
        if (mSearchItem != null) {
            mSearchItem.collapseActionView();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        SearchInfo info = (SearchInfo) parent.getItemAtPosition(position);
        mSearchBar.setText("");
        mSearchBar.clearFocus();
        mSearchItem.collapseActionView();;

        if (info.level == 0) {
            Bundle args = info.header.fragmentArguments;
            if (args == null) {
                args = info.header.fragmentArguments = new Bundle();
            }
            if (info.key != null && !args.containsKey(SearchPopulator.EXTRA_PREF_KEY)) {
                args.putString(SearchPopulator.EXTRA_PREF_KEY, info.key);
            }
            onHeaderClick(info.header, 0);
        } else {
            Intent i = new Intent(this, SubSettings.class);
            i.putExtra(EXTRA_SHOW_FRAGMENT, info.fragment);
            i.putExtra(EXTRA_SHOW_FRAGMENT_TITLE, info.parentTitle);
            i.putExtra(SearchPopulator.EXTRA_PREF_KEY, info.key);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        if (mSearchItem != null) {
            mSearchItem.collapseActionView();
        }
        return true;
    }

    public class ViewPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public ViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };

    public class MyOnPageChangeListener implements OnPageChangeListener {

        int one = offset + bmpW;
        int two = one;

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    /*
     * Settings subclasses for launching independently.
     */
    public static class BluetoothSettingsActivity extends TabSettings { /* empty */ }
    public static class WirelessSettingsActivity extends TabSettings { /* empty */ }
    public static class TetherSettingsActivity extends TabSettings { /* empty */ }
    public static class VpnSettingsActivity extends TabSettings { /* empty */ }
    public static class DateTimeSettingsActivity extends TabSettings { /* empty */ }
    public static class StorageSettingsActivity extends TabSettings { /* empty */ }
    public static class WifiSettingsActivity extends TabSettings { /* empty */ }
    public static class WifiP2pSettingsActivity extends TabSettings { /* empty */ }
    public static class InputMethodAndLanguageSettingsActivity extends TabSettings { /* empty */ }
    public static class KeyboardLayoutPickerActivity extends TabSettings { /* empty */ }
    public static class InputMethodAndSubtypeEnablerActivity extends TabSettings { /* empty */ }
    public static class SpellCheckersSettingsActivity extends TabSettings { /* empty */ }
    public static class LocalePickerActivity extends TabSettings { /* empty */ }
    public static class UserDictionarySettingsActivity extends TabSettings { /* empty */ }
    public static class SoundSettingsActivity extends TabSettings { /* empty */ }
    public static class DisplaySettingsActivity extends TabSettings { /* empty */ }
    public static class DeviceInfoSettingsActivity extends TabSettings { /* empty */ }
    public static class ApplicationSettingsActivity extends TabSettings { /* empty */ }
    public static class ManageApplicationsActivity extends TabSettings { /* empty */ }
    public static class AppOpsSummaryActivity extends TabSettings {
        @Override
        public boolean isValidFragment(String className) {
            if (AppOpsSummary.class.getName().equals(className)) {
                return true;
            }
            return super.isValidFragment(className);
        }
    }
    public static class StorageUseActivity extends TabSettings { /* empty */ }
    public static class DevelopmentSettingsActivity extends TabSettings { /* empty */ }
    public static class AccessibilitySettingsActivity extends TabSettings { /* empty */ }
    public static class CaptioningSettingsActivity extends TabSettings { /* empty */ }
    public static class SecuritySettingsActivity extends TabSettings { /* empty */ }
    public static class LocationSettingsActivity extends TabSettings { /* empty */ }
    public static class PrivacySettingsActivity extends TabSettings { /* empty */ }
    public static class RunningServicesActivity extends TabSettings { /* empty */ }
    public static class ManageAccountsSettingsActivity extends TabSettings { /* empty */ }
    public static class PowerUsageSummaryActivity extends TabSettings { /* empty */ }
    public static class AccountSyncSettingsActivity extends TabSettings { /* empty */ }
    public static class AccountSyncSettingsInAddAccountActivity extends TabSettings { /* empty */ }
    public static class CryptKeeperSettingsActivity extends TabSettings { /* empty */ }
    public static class DeviceAdminSettingsActivity extends TabSettings { /* empty */ }
    public static class DataUsageSummaryActivity extends TabSettings { /* empty */ }
    public static class AdvancedWifiSettingsActivity extends TabSettings { /* empty */ }
    public static class TextToSpeechSettingsActivity extends TabSettings { /* empty */ }
    public static class AndroidBeamSettingsActivity extends TabSettings { /* empty */ }
    public static class WifiDisplaySettingsActivity extends TabSettings { /* empty */ }
    public static class DreamSettingsActivity extends TabSettings { /* empty */ }
    public static class NotificationStationActivity extends TabSettings { /* empty */ }
    public static class UserSettingsActivity extends TabSettings { /* empty */ }
    public static class NotificationAccessSettingsActivity extends TabSettings { /* empty */ }
    public static class UsbSettingsActivity extends TabSettings { /* empty */ }
    public static class TrustedCredentialsSettingsActivity extends TabSettings { /* empty */ }
    public static class PaymentSettingsActivity extends TabSettings { /* empty */ }
    public static class PrintSettingsActivity extends TabSettings { /* empty */ }
    public static class PrintJobSettingsActivity extends TabSettings { /* empty */ }
    public static class ApnSettingsActivity extends TabSettings { /* empty */ }
    public static class ApnEditorActivity extends TabSettings { /* empty */ }
    public static class BlacklistSettingsActivity extends TabSettings { /* empty */ }
    public static class QuietHoursSettingsActivity extends TabSettings { /* empty */ }
    public static class QuickSettingsTilesSettingsActivity extends TabSettings { /* empty */ }
    public static class BatteryIconStyleSettingsActivity extends TabSettings { /* empty */ }
    public static class DisplayRotationSettingsActivity extends TabSettings { /* empty */ }
    public static class ShakeEventsSettingsActivity extends TabSettings { /* empty */ }
    public static class HomeSettingsActivity extends TabSettings { /* empty */ }
    public static class InterfaceSettingsActivity extends TabSettings { /* empty */ }
    public static class NavigationSettingsActivity extends TabSettings { /* empty */ }
    public static class ThemeSettingsActivity extends TabSettings { /* empty */ }
    public static class AdvancedSettingsActivity extends TabSettings { /* empty */ }
}

package com.example.lvniqi.multimeter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.liveo.interfaces.NavigationLiveoListener;
import br.liveo.navigationliveo.NavigationLiveo;
/**
 * Created by lvniqi on 2015-05-16.
 */

/*
 * Copyright 2015 Rudson Lima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


public class MainActivity extends NavigationLiveo implements NavigationLiveoListener {

    public List<String> mListNameItem;
    int position_last;
    audioEncode audio;
    @Override
    public void onUserInformation() {
        //User information here
        //this.mUserName.setText("Rudson Lima");
        //this.mUserEmail.setText("rudsonlive@gmail.com");
        //this.mUserPhoto.setImageResource(R.drawable.ic_rudsonlive);
        this.mUserBackground.setImageResource(R.drawable.ic_user_background);
    }

    @Override
    public void onInt(Bundle savedInstanceState) {
        //Creation of the list items is here

        // set listener {required}
        this.setNavigationListener(this);

        if (savedInstanceState == null) {
            //First item of the position selected from the list
            this.setDefaultStartPositionNavigation(1);
        }

        // name of the list items
        mListNameItem = new ArrayList<>();
        mListNameItem.add(0, getString(R.string.voltage));
        mListNameItem.add(1, getString(R.string.others_show));
        mListNameItem.add(2, "这是分隔符..."); //This item will be a subHeader
        mListNameItem.add(3, getString(R.string.connect_set));
        mListNameItem.add(4, getString(R.string.update_fireware));
        // icons list items
        List<Integer> mListIconItem = new ArrayList<>();
        mListIconItem.add(0, R.drawable.ic_inbox_black_24dp);
        mListIconItem.add(1, R.drawable.ic_star_black_24dp); //Item no icon set 0
        mListIconItem.add(2, 0); //When the item is a subHeader the value of the icon 0
        mListIconItem.add(3, R.drawable.ic_drafts_black_24dp);
        mListIconItem.add(4, 0);
        //{optional} - Among the names there is some subheader, you must indicate it here
        List<Integer> mListHeaderItem = new ArrayList<>();
        mListHeaderItem.add(2);

        //{optional} - Among the names there is any item counter, you must indicate it (position) and the value here
        SparseIntArray mSparseCounterItem = new SparseIntArray(); //indicate all items that have a counter
        mSparseCounterItem.put(0, 2);
        mSparseCounterItem.put(1, 123);
        //If not please use the FooterDrawer use the setFooterVisible(boolean visible) method with value false
        this.setFooterInformationDrawer(R.string.settings, R.drawable.ic_settings_black_24dp);
        this.setNavigationAdapter(mListNameItem, mListIconItem, mListHeaderItem, mSparseCounterItem);
    }
    @Override
    public void onItemClickNavigation(int position, int layoutContainerId) {

        FragmentManager mFragmentManager = getSupportFragmentManager();

        Fragment mFragment = new FragmentMain().newInstance(mListNameItem.get(position));

        if (mFragment != null){
            mFragmentManager.beginTransaction().replace(layoutContainerId, mFragment).commit();
        }
    }

    @Override
    public void onPrepareOptionsMenuNavigation(Menu menu, int position, boolean visible) {
        //hide the menu when the navigation is opens
        if(position_last != position) {
            switch (position) {
                case 0:
                    menu.findItem(R.id.menu_add).setVisible(!visible);
                    menu.findItem(R.id.menu_search).setVisible(!visible);
                    //测试用 音频播放
                    if(audio == null) {
                        audio = new audioEncode();
                        audio.start();
                    }
                    else{
                        audio.setFrequency(audio.getFrequency()*1.5);
                        Log.i("frequency",""+audio.getFrequency());
                    }
                    break;

                case 1:
                    menu.findItem(R.id.menu_add).setVisible(!visible);
                    menu.findItem(R.id.menu_search).setVisible(!visible);
                    break;
                case 3:
                    startActivity(new Intent(this, SettingsActivity.class));
                    break;
            }
            position_last = position;
        }
        Log.i("position",""+position);
    }

    @Override
    public void onClickUserPhotoNavigation(View v) {
        //user photo onClick
        Toast.makeText(this, R.string.open_user_profile, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickFooterItemNavigation(View v) {
        //footer onClick
        startActivity(new Intent(this, SettingsActivity.class));
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (audio != null)
            audio.stop();
    }
}
package com.example.lvniqi.multimeter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.dexafree.materialList.controller.MaterialListAdapter;
import com.dexafree.materialList.view.MaterialListView;
import com.example.lvniqi.multimeter.Audio.AudioReceiver;
import com.example.lvniqi.multimeter.Audio.AudioSender;
import com.example.lvniqi.multimeter.Card.AudioDecoderCard;
import com.example.lvniqi.multimeter.Card.GraphCard;
import com.example.lvniqi.multimeter.Card.LedCard;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    public static AudioSender audioSender;
    public static AudioReceiver audioReceiver;
    static int menuPosition;
    static Message LEDmessage = new Message();
    final Timer timer = new Timer();
    public List<String> mListNameItem;
    //测试用
    private TimerTask task;

    public static AudioReceiver getAudioReceiver() {
        return audioReceiver;
    }

    static public AudioSender getAudioSender() {
        return audioSender;
    }

    static public int getMenuPosition() {
        return menuPosition;
    }

    public static Message getLEDmessage() {
        return LEDmessage;
    }

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
            this.setDefaultStartPositionNavigation(0);
        }

        // name of the list items
        mListNameItem = new ArrayList<>();
        mListNameItem.add(0, getString(R.string.measure));
        mListNameItem.add(1, ""); //This item will be a subHeader
        mListNameItem.add(2, getString(R.string.settings));
        mListNameItem.add(3, getString(R.string.tools));
        // icons list items
        List<Integer> mListIconItem = new ArrayList<>();
        mListIconItem.add(0, R.drawable.ic_star_black_24dp); //Item no icon set 0
        mListIconItem.add(1, 0); //When the item is a subHeader the value of the icon 0
        mListIconItem.add(2, R.drawable.ic_settings_black_24dp);
        mListIconItem.add(3, R.drawable.abc_ic_commit_search_api_mtrl_alpha);
        //{optional} - Among the names there is some subheader, you must indicate it here
        List<Integer> mListHeaderItem = new ArrayList<>();
        mListHeaderItem.add(1);

        //{optional} - Among the names there is any item counter, you must indicate it (position) and the value here
        SparseIntArray mSparseCounterItem = new SparseIntArray(); //indicate all items that have a counter
        mSparseCounterItem.put(0, 2);
        mSparseCounterItem.put(2, 123);
        //If not please use the FooterDrawer use the setFooterVisible(boolean visible) method with value false
        //this.setFooterInformationDrawer(R.string.settings, R.drawable.ic_settings_black_24dp);
        this.setNavigationAdapter(mListNameItem, mListIconItem, mListHeaderItem, mSparseCounterItem);
        final Context temp = getBaseContext();
        new ToastMessageTask().execute("Welcome!");
        //测试用 定时器
        /*task = new TimerTask() {
            @Override
            public void run() {
                new GraphCardTask().execute(0f);
            }
        };*/

        //timer.schedule(task, 2000, 50);

    }

    @Override
    public void onPrepareOptionsMenuNavigation(Menu menu, int position, boolean visible) {
        //hide the menu when the navigation is opens
        switch (position) {
            case 0:
                //FragmentManager mFragmentManager = getSupportFragmentManager();
                //FragmentMain mFragment = (FragmentMain)mFragmentManager.getFragments().get(0);
                //LedCard card = (LedCard)mFragment.getMeasureCards().getCards().get(1);
                //card.getLedView().setText(12,DefinedMessages.DC);
                //        getViewHolder().mLEDView.setText(12f,DefinedMessages.DC);

                    /*View  rootView = FragmentMain.getRootView();
                    final MaterialListView mListView = (MaterialListView) rootView.findViewById(R.id.material_listview);
                    MaterialListAdapter adapter = (MaterialListAdapter) mListView.getAdapter();
                    LedCard ledCard =  (LedCard)adapter.getCard("DC_CARD");
                    if(ledCard != null){
                        ledCard.setLedValue(12);
                    }*/
                menu.findItem(R.id.menu_add).setVisible(!visible);
                menu.findItem(R.id.menu_search).setVisible(!visible);
                break;

            case 2:
                //menu.findItem(R.id.menu_add).setVisible(!visible);
                //menu.findItem(R.id.menu_search).setVisible(!visible);

                break;
            case 3:
                //startActivity(new Intent(this, SettingsActivity.class));
                //Utils.changeToTheme(this, Utils.THEME_WARNNING);
                break;
        }
    }

    @Override //The "layoutContainerId" should be used in "beginTransaction (). Replace"
    public void onItemClickNavigation(int position, int layoutContainerId) {

        FragmentManager mFragmentManager = getSupportFragmentManager();
        Fragment mFragment = new FragmentMain().newInstance(mListNameItem.get(position));
        this.menuPosition = position;
        switch (position) {
            case 0:
                //测试用 音频播放
                if (audioSender == null) {
                    audioSender = new AudioSender();
                    audioSender.start();
                } else {
                    audioSender.setFrequency(480);
                    Log.i("frequency", "" + audioSender.getFrequency());
                }
                break;
            //case 2:
            //Utils.changeToTheme(this, Utils.THEME_WARNNING);
            //break;
            default:
                if (audioSender != null) {
                    audioSender.stop();
                    audioSender = null;
                }
                break;
        }
        if (mFragment != null) {
            mFragmentManager.beginTransaction().replace(layoutContainerId, mFragment).commit();
        }

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
    protected void onDestroy() {
        if (audioSender != null) {
            audioSender.stop();
            audioSender = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Utils.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        MediaButtonDisabler.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //屏蔽线控
        MediaButtonDisabler.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //屏蔽线控
        MediaButtonDisabler.register(this);
    }

    static class MeasureCardsTask extends AsyncTask<Float, String, Float> {
        Float value;

        @Override
        protected Float doInBackground(Float... params) {
            value = params[0];
            return value;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        // 这是执行在GUI线程context
        protected void onPostExecute(Float result) {
            View rootview = FragmentMain.getRootView();
            final MaterialListView mListView = (MaterialListView) rootview.findViewById(R.id.material_listview);
            if (mListView != null) {
                MaterialListAdapter adapter = (MaterialListAdapter) mListView.getAdapter();
                if (adapter != null) {
                    LedCard dcCard = (LedCard) adapter.getCard("DC_CARD");
                    if (dcCard != null) {
                        dcCard.setLedValue(dcCard.getLED_VALUE() + 1);
                    }
                }
            }
        }
    }

    public static class GraphCardTask extends AsyncTask<short[], String, short[]> {
        short[] value;

        @Override
        protected short[] doInBackground(short[]... params) {
            value = params[0];
            return value;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        // 这是执行在GUI线程context
        protected void onPostExecute(short[] result) {
            View rootview = FragmentMain.getRootView();
            final MaterialListView mListView = (MaterialListView) rootview.findViewById(R.id.material_listview);
            if (mListView != null) {
                MaterialListAdapter adapter = (MaterialListAdapter) mListView.getAdapter();
                if (adapter != null) {
                    //图形绘制
                    GraphCard graphCard = (GraphCard) adapter.getCard("Graph_CARD");
                    if (graphCard != null && graphCard.getGraphView() != null) {
                        List temp = graphCard.getGraphView().getSeries();
                        if (temp != null && temp.size() != 0) {
                            LineGraphSeries<DataPoint> series = (LineGraphSeries<DataPoint>) temp.get(0);
                            DataPoint[] temp2 = new DataPoint[100];
                            for (int i = 0; i < temp2.length; i++) {
                                temp2[i] = new DataPoint(i, result[i]);
                            }
                            series.resetData(temp2);
                        }
                    }
                }
            }
        }
    }

    public static class DecoderCardTask extends AsyncTask<String, String, String> {
        String value;

        @Override
        protected String doInBackground(String... params) {
            value = params[0];
            return value;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        // 这是执行在GUI线程context
        protected void onPostExecute(String result) {
            View rootview = FragmentMain.getRootView();
            final MaterialListView mListView = (MaterialListView) rootview.findViewById(R.id.material_listview);
            if (mListView != null) {
                MaterialListAdapter adapter = (MaterialListAdapter) mListView.getAdapter();
                if (adapter != null) {
                    //测试频率译码
                    AudioDecoderCard audioDecoderCard = (AudioDecoderCard) adapter.getCard("AudioRec_CARD");
                    if (audioDecoderCard != null && audioDecoderCard.getTextView() != null) {
                        audioDecoderCard.setText(result);
                    }
                }
            }
        }
    }

    private class ToastMessageTask extends AsyncTask<String, String, String> {
        String toastMessage;

        @Override
        protected String doInBackground(String... params) {
            toastMessage = params[0];
            return toastMessage;
        }

        protected void OnProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        // 这是执行在GUI线程context
        protected void onPostExecute(String result) {
            Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

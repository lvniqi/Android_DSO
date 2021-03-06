package com.example.lvniqi.multimeter;

/**
 * Created by lvniqi on 2015-05-16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.dexafree.materialList.cards.BasicButtonsCard;
import com.dexafree.materialList.cards.BasicListCard;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.controller.MaterialListAdapter;
import com.dexafree.materialList.controller.OnDismissCallback;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.example.lvniqi.multimeter.Audio.AudioDecoder;
import com.example.lvniqi.multimeter.Audio.AudioEncoder;
import com.example.lvniqi.multimeter.Audio.AudioReceiver;
import com.example.lvniqi.multimeter.Audio.AudioSender;
import com.example.lvniqi.multimeter.Audio.Mcu_Updater;
import com.example.lvniqi.multimeter.Audio.MessageDecoder;
import com.example.lvniqi.multimeter.Card.AudioDecoderCard;
import com.example.lvniqi.multimeter.Card.AudioEncoderCard;
import com.example.lvniqi.multimeter.Card.GraphCard;
import com.example.lvniqi.multimeter.Card.LedCard;
import com.example.lvniqi.multimeter.Card.SigCard;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class FragmentMain extends Fragment {

    private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";
    private static View rootView;
    private boolean mSearchCheck;
    private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (mSearchCheck) {
                // implement your search here
            }
            return false;
        }
    };
    //测试 LDE显示
    private LEDView ledView;

    static public View getRootView() {
        return rootView;
    }

    public FragmentMain newInstance(String text) {
        FragmentMain mFragment = new FragmentMain();
        Bundle mBundle = new Bundle();
        mBundle.putString(TEXT_FRAGMENT, text);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        showListView(rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);

        //Select search item
        final MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setVisible(true);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(this.getString(R.string.search));

        ((EditText) searchView.findViewById(R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.nliveo_white));
        searchView.setOnQueryTextListener(onQuerySearchView);

        menu.findItem(R.id.menu_add).setVisible(true);

        mSearchCheck = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {

            case R.id.menu_add:
                Toast.makeText(getActivity(), R.string.add, Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_search:
                mSearchCheck = true;
                Toast.makeText(getActivity(), R.string.search, Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    public void showListView(View rootView) {

        //小图片 LIstView
        final MaterialListView mListView = (MaterialListView) rootView.findViewById(R.id.material_listview);

        switch (MainActivity.getMenuPosition()) {
            case 0:
                //mListView.add(getWelcomeCard(rootView.getContext(),mListView));
                mListView.addAll(new Cards(rootView.getContext(), DefinedMessages.MESSURE).getCards());
                mListView.setOnDismissCallback(new OnDismissCallback() {
                    @Override
                    public void onDismiss(Card card, int position) {
                        switch (position) {
                            case 0:
                                if (card.getTag() == "WELCOME_CARD") {
                                    if (MainActivity.audioReceiver != null) {
                                        MainActivity.audioReceiver.stop();
                                    }
                                    MainActivity.audioReceiver = new MessageDecoder();
                                    MainActivity.audioReceiver.start();
                                    LedCard ledCard = (LedCard) ((MaterialListAdapter) card.getcAdapter()).getCard("DC_CARD");
                                    if (ledCard != null) {
                                        ledCard.setBackgroundColorRes(R.color.nliveo_blue_colorPrimary);
                                        ledCard.setLedAll(0, DefinedMessages.DC);
                                    }
                                    ledCard = (LedCard) ((MaterialListAdapter) card.getcAdapter()).getCard("AC_CARD");
                                    if (ledCard != null) {
                                        ledCard.setBackgroundColorRes(R.color.nliveo_blue_colorPrimary);
                                        ledCard.setLedAll(0, DefinedMessages.DC);
                                    }
                                }
                                break;
                        }
                    }
                });
                break;
            case 2:
                //连接选择卡
                BasicButtonsCard basicCard = new BasicButtonsCard(rootView.getContext());
                basicCard.setTitle(getString(R.string.connect_set));
                basicCard.setDescription("Your description");
                basicCard.setLeftButtonText("LEFT");
                basicCard.setRightButtonText("RIGHT");
                basicCard.setTag("connect_set");
                mListView.add(basicCard);
                //升级固件
                final BasicButtonsCard updateCard = new BasicButtonsCard(rootView.getContext());
                updateCard.setTitle(getString(R.string.update_fireware));
                updateCard.setLeftButtonText("    ");
                updateCard.setRightButtonText("RIGHT");
                updateCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
                    Mcu_Updater updater;

                    @Override
                    public void onButtonPressedListener(View view, Card card) {
                        //未启用
                        if (updateCard.getBackgroundColor() !=
                                view.getResources().getColor(R.color.nliveo_blue_colorPrimaryDark)) {
                            updater = new Mcu_Updater(view.getContext());
                            updater.start();
                            updateCard.setBackgroundColorRes(R.color.nliveo_blue_colorPrimaryDark);

                        } else {
                            updater.stop();
                            updateCard.setBackgroundColorRes(R.color.white);
                        }
                    }
                });
                updateCard.setTag("update_fireware");
                mListView.add(updateCard);
                //基本listview
                BasicListCard listCard2 = new BasicListCard(rootView.getContext());
                listCard2.setDescription("description");
                listCard2.setTitle("title");
                BasicListAdapter adapter = new BasicListAdapter(rootView.getContext());
                adapter.add("Text1");
                adapter.add("Text2");
                adapter.add("Text3");
                listCard2.setTag("LIST_CARD");
                listCard2.setAdapter(adapter);
                listCard2.setDismissible(true);
                mListView.add(listCard2);
                break;
            case 3:
                mListView.addAll(new Cards(rootView.getContext(), DefinedMessages.TOOLS).getCards());
                //LedListAdapter ledListAdapter = new LedListAdapter(rootView.getContext());
                //ledListAdapter.add("12");
                break;
        }
    }

}

class Cards {
    ArrayList<Card> cards;

    Cards(Context context, int Tag) {
        cards = new ArrayList<Card>();
        switch (Tag) {
            case DefinedMessages.MESSURE:
                cards.add(getWelcomeCard(context));
                cards.add(getLedCard(context, "DC"));
                cards.add(getLedCard(context, "AC"));
                break;
            case DefinedMessages.TOOLS:
                cards.add(getSigCard(context));
                cards.add(getGraphCard(context));
                cards.add(getAudioDecoderCard(context));
                cards.add(getAudioEncoderCard(context));
                break;
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    private Card getLedCard(Context context, String title) {
        //LED listview
        LedCard ledCard = new LedCard(context);
        ledCard.setTitleColorRes(R.color.white);
        ledCard.setDismissible(true);
        ledCard.setTag(title + "_CARD");
        switch (title) {
            case "DC":
                ledCard.setTitle(context.getString(R.string.dc));
                break;
            case "AC":
                ledCard.setTitle(context.getString(R.string.ac));
                break;
            default:
                ledCard.setTitle("UN KNOW");
                break;
        }
        ledCard.setBackgroundColorRes(R.color.nliveo_red_colorPrimaryDark);
        return ledCard;
    }

    private Card getSigCard(Context context) {
        //自添加测试card
        SigCard temp = new SigCard(context);
        temp.setTitle("信号发生器");
        temp.setTag("SIG_CARD");
        temp.setRightButtonText("开启");
        temp.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card2) {
                final SigCard card = (SigCard) card2;
                //final SigCard card = (SigCard)((IMaterialListAdapter)card1.getcAdapter()).getCard("SIG_CARD");
                //未启用
                if (card.getBackgroundColor() !=
                        view.getResources().getColor(R.color.nliveo_blue_colorPrimaryDark)) {
                    if (MainActivity.audioSender != null) {
                        MainActivity.audioSender.stop();
                        MainActivity.audioSender = null;
                    }
                    MainActivity.audioSender = new AudioSender();
                    MainActivity.audioSender.start();
                    int progress = card.getSeekBar().getProgress();
                    if (progress == 0) {
                        progress = 1;
                    }
                    card.getSeekBar().setProgress(progress);
                    card.setLedAll(120 * progress, DefinedMessages.FREQ);
                    MainActivity.audioSender.setFrequency(120 * progress);
                    card.setBackgroundColorRes(R.color.nliveo_blue_colorPrimaryDark);
                    card.setRightButtonText("关闭");
                    card.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            card.setLedValue(120 * progress);
                            MainActivity.audioSender.setFrequency(120 * progress);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            Log.i("frequency", "" + MainActivity.audioSender.getFrequency());
                        }
                    });
                } else {
                    card.setBackgroundColorRes(R.color.white);
                    card.setRightButtonText("开启");
                    if (MainActivity.audioSender != null) {
                        MainActivity.audioSender.stop();
                        MainActivity.audioSender = null;
                    }
                    card.setLedAll(0, DefinedMessages.UNKNOW);
                    card.getSeekBar().setOnSeekBarChangeListener(null);
                }
            }
        });
        return temp;
    }

    private Card getWelcomeCard(Context context) {
        //welcome listview
        SmallImageCard wCard = new SmallImageCard(context);
        wCard.setTitle(context.getString(R.string.welcome));
        wCard.setDescription(context.getString(R.string.welcome_description));
        wCard.setTag("WELCOME_CARD");
        //wCard.setButtonText("Okay!");
        wCard.setDismissible(true);
        /*wCard.setOnButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show();
                MaterialListAdapter adapter = (MaterialListAdapter) mListView.getAdapter();
                Card temp = adapter.getCard("DC_CARD");
                if (temp != null) {
                    mListView.remove(temp);
                }

            }
        });*/
        wCard.setDescriptionColorRes(R.color.white);
        wCard.setBackgroundColorRes(R.color.material_deep_teal_500);
        return wCard;
    }

    private Card getGraphCard(Context context) {
        GraphCard graphCard = new GraphCard(context);
        graphCard.setTitle("GraphCard");
        graphCard.setTitleColorRes(R.color.nliveo_black);
        graphCard.setTag("Graph_CARD");
        graphCard.setRightButtonText("测试");
        graphCard.setDismissible(true);
        graphCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card2) {
                final GraphCard card = (GraphCard) card2;
                //已启用
                if (card.getBackgroundColor() ==
                        view.getResources().getColor(R.color.nliveo_blue_colorPrimaryDark)) {
                    card.setBackgroundColorRes(R.color.white);
                    LineGraphSeries<DataPoint> series =
                            (LineGraphSeries<DataPoint>) card.getGraphView().getSeries().get(0);
                    series.resetData(new DataPoint[0]);
                    if (MainActivity.audioReceiver != null) {
                        MainActivity.audioReceiver.stop();
                        MainActivity.audioReceiver = null;
                    }
                    card.setShowGraphView(false);
                } else {
                    if (MainActivity.audioReceiver != null) {
                        MainActivity.audioReceiver.stop();
                    }
                    MainActivity.audioReceiver = new AudioReceiver();
                    MainActivity.audioReceiver.start();
                    card.setBackgroundColorRes(R.color.nliveo_blue_colorPrimaryDark);
                    card.setShowGraphView(true);
                }
            }
        });
        //graphCard.setDescriptionColorRes(R.color.white);
        //graphCard.setBackgroundColorRes(R.color.material_deep_teal_500);
        return graphCard;
    }

    private Card getAudioDecoderCard(Context context) {
        AudioDecoderCard audioDecoderCard = new AudioDecoderCard(context);
        audioDecoderCard.setTitle("AudioDecoderCard");
        audioDecoderCard.setTitleColorRes(R.color.nliveo_black);
        audioDecoderCard.setTag("AudioRec_CARD");
        audioDecoderCard.setDismissible(true);
        audioDecoderCard.setRightButtonText("测试");
        audioDecoderCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card2) {
                final AudioDecoderCard card = (AudioDecoderCard) card2;
                //已启用
                if (card.getBackgroundColor() ==
                        view.getResources().getColor(R.color.nliveo_blue_colorPrimaryDark)) {
                    card.setBackgroundColorRes(R.color.white);
                    if (MainActivity.audioReceiver != null) {
                        MainActivity.audioReceiver.stop();
                        MainActivity.audioReceiver = null;
                    }
                } else {
                    if (MainActivity.audioReceiver == null) {
                        MainActivity.audioReceiver = new AudioDecoder();
                        MainActivity.audioReceiver.start();
                    }
                    card.setBackgroundColorRes(R.color.nliveo_blue_colorPrimaryDark);
                }
            }
        });
        //graphCard.setDescriptionColorRes(R.color.white);
        //graphCard.setBackgroundColorRes(R.color.material_deep_teal_500);
        return audioDecoderCard;
    }

    private Card getAudioEncoderCard(Context context) {
        AudioEncoderCard audioEncoderCard = new AudioEncoderCard(context);
        audioEncoderCard.setTitle("AudioEncoderCard");
        audioEncoderCard.setTitleColorRes(R.color.nliveo_black);
        audioEncoderCard.setTag("AudioSend_CARD");
        audioEncoderCard.setRightButtonText("测试");
        audioEncoderCard.setDismissible(true);
        audioEncoderCard.setOnRightButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card2) {
                final AudioEncoderCard card = (AudioEncoderCard) card2;
                //已启用
                if (card.getBackgroundColor() ==
                        view.getResources().getColor(R.color.nliveo_blue_colorPrimaryDark)) {
                    card.setBackgroundColorRes(R.color.white);
                    if (MainActivity.audioSender != null) {
                        MainActivity.audioSender.stop();
                    }
                    MainActivity.audioSender = null;
                } else {
                    if (MainActivity.audioSender != null) {
                        MainActivity.audioSender.stop();
                    }
                    MainActivity.audioSender = new AudioEncoder();
                    MainActivity.audioSender.start();
                    String texts = card.getEditText().getText().toString();
                    ((AudioEncoder) MainActivity.audioSender).addDatas(texts);
                    /*for(int i=0;i<256;i++){
                        ((AudioEncoder) MainActivity.audioSender).addData((int)i);
                    }*/
                    card.setBackgroundColorRes(R.color.nliveo_blue_colorPrimaryDark);
                }
            }
        });
        //graphCard.setDescriptionColorRes(R.color.white);
        //graphCard.setBackgroundColorRes(R.color.material_deep_teal_500);
        return audioEncoderCard;
    }

}
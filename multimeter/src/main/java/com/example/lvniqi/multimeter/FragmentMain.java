package com.example.lvniqi.multimeter;

/**
 * Created by lvniqi on 2015-05-16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dexafree.materialList.cards.BasicButtonsCard;
import com.dexafree.materialList.cards.BasicCard;
import com.dexafree.materialList.cards.BasicListCard;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.cards.WelcomeCard;
import com.dexafree.materialList.controller.MaterialListAdapter;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;

public class FragmentMain extends Fragment {

    private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";
    private boolean mSearchCheck;
    private View rootView;

    public View getRootView() {
        return rootView;
    }
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

    public FragmentMain newInstance(String text) {
        FragmentMain mFragment = new FragmentMain();
        Bundle mBundle = new Bundle();
        mBundle.putString(TEXT_FRAGMENT, text);
        mFragment.setArguments(mBundle);
        return mFragment;
    }
    private Context mContext;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = rootView.getContext();
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
    public void showListView(View rootView){

        //小图片 LIstView
        final MaterialListView mListView = (MaterialListView) rootView.findViewById(R.id.material_listview);

        switch(MainActivity.getMenuPosition()){
            case 0:
                mListView.add(getWelcomeCard(rootView.getContext(),mListView));
                mListView.add(getLedCard(rootView.getContext()));
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
                basicCard = new BasicButtonsCard(rootView.getContext());
                basicCard.setTitle(getString(R.string.update_fireware));
                basicCard.setLeftButtonText("LEFT");
                basicCard.setRightButtonText("RIGHT");
                basicCard.setTag("update_fireware");
                mListView.add(basicCard);
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
                //自添加测试card
                SigCard temp = new SigCard(rootView.getContext());
                LedListAdapter ledListAdapter = new LedListAdapter(rootView.getContext());
                ledListAdapter.add("12");
                temp.setTitle("信号发生器");
                temp.setTag("TEMP_CARD");
                mListView.add(temp);
                break;
        }
    }
    private Card getWelcomeCard(Context context,final MaterialListView mListView){
        //welcome listview
        WelcomeCard wCard = new WelcomeCard(context);
        wCard.setTitle(getString(R.string.welcome));
        wCard.setDescription(getString(R.string.welcome_description));
        wCard.setTag("WELCOME_CARD");
        wCard.setButtonText("Okay!");
        wCard.setDismissible(true);
        wCard.setOnButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(mContext, "Welcome!", Toast.LENGTH_SHORT).show();
                MaterialListAdapter adapter = (MaterialListAdapter)mListView.getAdapter();
                Card temp = adapter.getCard("DC_CARD");
                if(temp != null) {
                    mListView.remove(temp);
                }

            }
        });
        wCard.setDescriptionColorRes(R.color.white);
        wCard.setBackgroundColorRes(R.color.nliveo_blue_colorPrimaryDark);
        return wCard;
    }
    private Card getLedCard(Context context){
        //LED listview
        BasicListCard listCard = new BasicListCard(context);
        LedListAdapter ledListAdapter = new LedListAdapter(context);
        listCard.setTag("DC_CARD");
        ledListAdapter.add("Text1");
        listCard.setTitle(getString(R.string.voltage));
        listCard.setTitleColorRes(R.color.white);
        listCard.setAdapter(ledListAdapter);
        listCard.setDismissible(true);
        listCard.setBackgroundColorRes(R.color.nliveo_blue_colorPrimaryDark);
        return listCard;
    }
}
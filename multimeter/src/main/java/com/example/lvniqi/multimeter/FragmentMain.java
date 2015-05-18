package com.example.lvniqi.multimeter;

/**
 * Created by lvniqi on 2015-05-16.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.cards.BasicListCard;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.cards.WelcomeCard;
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
        /*
        SmallImageCard card = new SmallImageCard(rootView.getContext());
        card.setDescription("description");
        card.setTitle("title");
        card.setDrawable(R.drawable.ic_launcher);
        mListView.add(card);
        //基本listview
        BasicListCard listCard = new BasicListCard(rootView.getContext());
        listCard.setDescription("description");
        listCard.setTitle("title");
        BasicListAdapter adapter = new BasicListAdapter(rootView.getContext());
        adapter.add("Text1");
        adapter.add("Text2");
        adapter.add("Text3");
        listCard.setTag("LIST_CARD");
        listCard.setAdapter(adapter);
        listCard.setDismissible(true);
        mListView.add(listCard);*/
        //welcome listview

        final WelcomeCard wCard = new WelcomeCard(rootView.getContext());
        wCard.setTitle(getString(R.string.welcome));
        wCard.setDescription(getString(R.string.welcome_description));
        wCard.setTag("WELCOME_CARD");
        wCard.setButtonText("Okay!");
        wCard.setDismissible(true);
        wCard.setOnButtonPressedListener(new OnButtonPressListener() {
            @Override
            public void onButtonPressedListener(View view, Card card) {
                Toast.makeText(mContext, "Welcome!", Toast.LENGTH_SHORT).show();
            }
        });
        wCard.setBackgroundColorRes(R.color.background_material_dark);
        mListView.add(wCard);
        //LED listview
        BasicListCard listCard = new BasicListCard(rootView.getContext());
        listCard.setDescription("description");
        listCard.setTitle("title");
        LedListAdapter ledListAdapter = new LedListAdapter(rootView.getContext());
        ledListAdapter.add("Text1");
        listCard.setTag("LED_LIST_CARD");
        listCard.setAdapter(ledListAdapter);
        listCard.setDismissible(true);
        listCard.setBackgroundColorRes(R.color.background_material_dark);
        mListView.add(listCard);
    }
}
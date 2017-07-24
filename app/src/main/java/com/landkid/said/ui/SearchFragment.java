package com.landkid.said.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.landkid.said.R;
import com.landkid.said.util.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.blurry.Blurry;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSearchCompleteListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SEARCH_MODE = "SEARCH_MODE";
    private static final String CX = "CX";
    private static final String CY = "CY";
    public static final String SEARCH_KEYWORD = "SEARCH_KEYWORD";

    // TODO: Rename and change types of parameters
    private String mSearchMode;

    @BindView(R.id.ll_search_area) ConstraintLayout mLlSearchArea;
    @BindView(R.id.et_search) EditText mEtSearch;
    @BindView(R.id.search_button) ImageButton mBtSearch;

    int cx;
    int cy;


    private OnSearchCompleteListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String searchMode, int cx, int cy) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH_MODE, searchMode);
        args.putInt(CX, cx);
        args.putInt(CY, cy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSearchMode = getArguments().getString(SEARCH_MODE);
            cx = getArguments().getInt(CX);
            cy = getArguments().getInt(CY);
        }
    }

    View mRootView;
    InputMethodManager mInputMethodManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, mRootView);
        mLlSearchArea.setVisibility(View.INVISIBLE);
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int finalRadius = Math.max(mLlSearchArea.getWidth(), mLlSearchArea.getHeight());

                Animator anim = ViewAnimationUtils.createCircularReveal(mLlSearchArea, cx, cy, 0, finalRadius);

                mLlSearchArea.setVisibility(View.VISIBLE);
                anim.start();

                mInputMethodManager.showSoftInput(mEtSearch, InputMethodManager.SHOW_IMPLICIT);

            }
        }, 300);


        mLlSearchArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            completeSearch();
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                switch (i){
                    case EditorInfo.IME_ACTION_DONE:
                        search();
                        return true;
                }

                return false;
            }
        });

        mBtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search();
            }
        });

        mLlSearchArea.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // inset the toolbar down by the status bar height

                mLlSearchArea.setPadding(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        0);

                mLlSearchArea.setOnApplyWindowInsetsListener(null);

                return insets.consumeSystemWindowInsets();
            }
        });

        return mRootView;
    }

    public void search(){
        String keyword = mEtSearch.getText().toString();
        if(!keyword.isEmpty()) {
            Message message = new Message();
            message.what = MainActivity.TO_SEARCH_RESULT;

            Bundle bundle = new Bundle();
            bundle.putString(SEARCH_KEYWORD, mEtSearch.getText().toString());
            message.setData(bundle);

            ((MainActivity) getContext()).transitionHandler.sendMessage(message);
            completeSearch();
        }
    }

    public void completeSearch(){

        int initialRadius = Math.max(mLlSearchArea.getWidth(), mLlSearchArea.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(mLlSearchArea, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mLlSearchArea.setVisibility(View.INVISIBLE);
                mInputMethodManager.hideSoftInputFromWindow(mEtSearch.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                getActivity().onBackPressed();
            }
        });

        anim.start();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchCompleteListener) {
            mListener = (OnSearchCompleteListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSearchCompleteListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSearchCompleteListener {
        void onFragmentInteraction();
    }

    public void onBackPressed() {

    }
}

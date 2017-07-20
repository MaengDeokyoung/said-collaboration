package com.landkid.said.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.landkid.said.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
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

    // TODO: Rename and change types of parameters
    private String mSearchMode;

    @BindView(R.id.ll_search_area) FrameLayout mLlSearchArea;

    int cx;
    int cy;


    private OnFragmentInteractionListener mListener;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this, mRootView);
        mLlSearchArea.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int finalRadius = Math.max(mLlSearchArea.getWidth(), mLlSearchArea.getHeight());

                Animator anim = ViewAnimationUtils.createCircularReveal(mLlSearchArea, cx, cy, 0, finalRadius);

                mLlSearchArea.setVisibility(View.VISIBLE);
                anim.start();
            }
        }, 300);

        mLlSearchArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int initialRadius = Math.max(mLlSearchArea.getWidth(), mLlSearchArea.getHeight());

                Animator anim = ViewAnimationUtils.createCircularReveal(mLlSearchArea, cx, cy, initialRadius, 0);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mLlSearchArea.setVisibility(View.INVISIBLE);
                        getActivity().onBackPressed();

                    }
                });

                anim.start();
            }
        });

        return mRootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onBackPressed() {

    }
}

package com.landkid.said.data.api;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Created by landkid on 2017. 6. 3..
 */

public abstract class BaseDataManager<T> {

    private AtomicInteger loadingCount = new AtomicInteger(0);
    private ArrayList<Integer> mPageIndexes;
    protected static ArrayList<Call> inflight;

    protected BaseDataManager(){
        inflight= new ArrayList<>();
    }


    public abstract void onDataLoaded(T items);

    static void loadCancel(){
        for(Call call : inflight){
            call.cancel();
        }
        inflight.clear();
    }

    protected void loadStarted() {
        if (0 == loadingCount.getAndIncrement()) {

        }
    }

    protected void loadFinished() {
        if (0 == loadingCount.decrementAndGet()) {

        }
    }
    public boolean isDataLoading() {
        return loadingCount.get() > 0;
    }

    public void resetNextPageIndexes(){
        mPageIndexes = new ArrayList<>();
        mPageIndexes.add(0);
    }

    protected int getNextPageIndexes(){
        return mPageIndexes.size();
    }

    public void updatePageIndexes(){
        mPageIndexes.add(mPageIndexes.size());
    }
}

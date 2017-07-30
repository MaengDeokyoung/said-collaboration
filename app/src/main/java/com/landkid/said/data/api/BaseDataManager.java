package com.landkid.said.data.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;

/**
 * Created by landkid on 2017. 6. 3..
 */

public abstract class BaseDataManager<T> {

    protected AtomicInteger loadingCount = new AtomicInteger(0);
    protected ArrayList<Integer> mPageIndexes;
    protected static ArrayList<Call> inflight;

    protected BaseDataManager(){
        inflight= new ArrayList<>();
    }


    public abstract void onDataLoaded(T items);

    protected static void loadCancel(){
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

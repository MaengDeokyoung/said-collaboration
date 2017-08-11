package com.landkid.said.data.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Created by landkid on 2017. 6. 3..
 */

public abstract class BaseDataManager<T> implements DataLoadingSubject {

    private AtomicInteger loadingCount = new AtomicInteger(0);
    private ArrayList<Integer> mPageIndexes;
    protected static ArrayList<Call> inflight;
    private List<DataLoadingCallbacks> loadingCallbacks;


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
            dispatchLoadingStartedCallbacks();
        }
    }

    protected void loadFinished() {
        if (0 == loadingCount.decrementAndGet()) {
            dispatchLoadingFinishedCallbacks();
        }
    }

    protected void dispatchLoadingStartedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (DataLoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.dataStartedLoading();
        }
    }

    protected void dispatchLoadingFinishedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (DataLoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.dataFinishedLoading();
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

    @Override
    public void registerCallback(DataLoadingSubject.DataLoadingCallbacks callback) {
        if (loadingCallbacks == null) {
            loadingCallbacks = new ArrayList<>(1);
        }
        loadingCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(DataLoadingSubject.DataLoadingCallbacks callback) {
        if (loadingCallbacks != null && loadingCallbacks.contains(callback)) {
            loadingCallbacks.remove(callback);
        }
    }
}

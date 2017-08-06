package com.landkid.said.data.api.model;

/**
 * referred from https://github.com/nickbutcher/plaid
 *
 * Base class for all model types
 */
public class SaidItem {

    public final long id;
    public final String title;
    public String url; // can't be final as some APIs use different serialized names

    public boolean isHeaderItem = false;
    public boolean isSkeletonItem = false;
    public String headerTitle;

    public static SaidItem getHeaderInstance(String headerTitle){
        SaidItem instance = new SaidItem(-1, null, null);
        instance.isHeaderItem = true;
        instance.headerTitle = headerTitle;
        return instance;
    }

    public static SaidItem getSkeletonInstance(){
        SaidItem instance = new SaidItem(-1, null, null);
        instance.isSkeletonItem = true;
        return instance;
    }

    public SaidItem(long id,
                    String title,
                    String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    @Override
    public String toString() {
        return title;
    }
}

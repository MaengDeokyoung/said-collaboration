/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

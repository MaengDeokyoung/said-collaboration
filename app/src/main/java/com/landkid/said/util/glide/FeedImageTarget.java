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

package com.landkid.said.util.glide;


import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class FeedImageTarget extends GlideDrawableImageViewTarget {

    private final boolean autoplayGifs;

    public FeedImageTarget(ImageView view, boolean autoplayGifs) {
        super(view);
        this.autoplayGifs = autoplayGifs;
    }

    @Override
    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable>
            animation) {
        super.onResourceReady(resource, animation);
        if (!autoplayGifs) {
            resource.stop();
        }
    }

    @Override
    public void onStart() {
        if (autoplayGifs) {
            super.onStart();
        }
    }

    @Override
    public void onStop() {
        if (autoplayGifs) {
            super.onStop();
        }
    }
}

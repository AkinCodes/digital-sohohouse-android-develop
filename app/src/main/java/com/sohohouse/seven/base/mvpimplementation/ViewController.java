/*
 * Copyright (C) SYMBILITY SOLUTIONS INC. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * This content is proprietary and confidential.
 */

package com.sohohouse.seven.base.mvpimplementation;

import android.content.Context;

public interface ViewController {

    Presenter createPresenter();

    @SuppressWarnings("unused")
    Presenter getPresenter();

    /**
     * This is called when the layout is created.
     */
    @SuppressWarnings("EmptyMethod")
    void onCreated();

    Context getContext();

    void setScreenName(String screenName);
}
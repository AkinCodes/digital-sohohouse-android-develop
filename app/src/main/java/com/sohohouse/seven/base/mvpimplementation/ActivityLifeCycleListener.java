/*
  Copyright (C) SYMBILITY SOLUTIONS INC. - All Rights Reserved
  Unauthorized copying of this file, via any medium is strictly prohibited
  This content is proprietary and confidential
 */

package com.sohohouse.seven.base.mvpimplementation;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * Callbacks specifically for an activity.
 * <p>
 * {@inheritDoc}
 */
public interface ActivityLifeCycleListener extends LifeCycleListener {

    void onPostCreated(Activity activity, @Nullable Bundle savedInstanceState);

}

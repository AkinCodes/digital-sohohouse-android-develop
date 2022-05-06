/*
  Copyright (C) SYMBILITY SOLUTIONS INC. - All Rights Reserved
  Unauthorized copying of this file, via any medium is strictly prohibited
  This content is proprietary and confidential
 */

package com.sohohouse.seven.base.mvpimplementation;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

/**
 * Helps listens for callbacks that made on
 * {@link Fragment} or
 * {@link android.app.Activity}.
 * <p>
 * This will help centralized logic into one class instead of being repeated on the base activity, fragment,
 * or dialog fragment
 */
interface LifeCycleListener {

    void onResume();

    void onPause();

    void onDestroy();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onSaveInstanceState(Bundle outState);

}

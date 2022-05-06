package com.sohohouse.seven.connect.match

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity

class RecommendationListActivity : InjectableActivity() {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, RecommendationListActivity::class.java)
        }
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_all_recommendation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.setFragmentResultListener(
            RecommendationListFragment.GO_BACK,
            this
        ) { key, _ ->
            if (key == RecommendationListFragment.GO_BACK) finish()
        }
    }
}
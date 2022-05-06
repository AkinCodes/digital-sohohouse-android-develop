package com.sohohouse.seven.more.profile.crop

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.sohohouse.seven.R
import com.sohohouse.seven.base.InjectableActivity
import com.sohohouse.seven.more.profile.crop.edit.CropEditFragment

class ProfileImageCropActivity : InjectableActivity() {

    companion object {
        const val EXTRA_UNCROPPED_IMAGE_FILE = "ProfileImageCropActivity.uncropped_image_file"
    }

    override fun getContentLayout() = R.layout.activity_profile_image_crop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.extras?.get(EXTRA_UNCROPPED_IMAGE_FILE) as? Uri

        val firstFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        (firstFragment as? NavHostFragment)?.navController?.setGraph(
            R.navigation.profile_image_cropper_navigation_graph,
            bundleOf(CropEditFragment.ARGS_UNCROPPED_IMAGE_URI_KEY to uri)
        )
    }

}
package com.sohohouse.seven.houseboard.post

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jakewharton.rxbinding4.widget.textChanges
import com.sohohouse.seven.R
import com.sohohouse.seven.base.error.ErrorDialogHelper.showErrorDialogByErrorCode
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.extensions.*
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.CustomDialogFactory
import com.sohohouse.seven.common.views.LoadingView
import com.sohohouse.seven.common.views.showSafe
import com.sohohouse.seven.databinding.ActivityHouseBoardPostBinding
import com.sohohouse.seven.houseboard.post.HouseBoardPostViewModel.Companion.ERROR_DIALOG_GENERIC
import com.sohohouse.seven.houseboard.post.HouseBoardPostViewModel.Companion.ERROR_DIALOG_NETWORK
import com.sohohouse.seven.houseboard.post.HouseBoardPostViewModel.Companion.ERROR_DIALOG_REJECTED
import com.sohohouse.seven.houseboard.post.HouseBoardPostViewModel.HouseBoardUserData
import com.sohohouse.seven.more.profile.crop.Event
import com.sohohouse.seven.more.profile.crop.ImageGalleryCropFlowManager
import com.sohohouse.seven.more.profile.crop.ImageSourceChooserBottomSheet

class HouseBoardPostActivity : BaseMVVMActivity<HouseBoardPostViewModel>(),
    Loadable.View {

    private val binding by viewBinding(ActivityHouseBoardPostBinding::bind)

    private val croppingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let {
                    viewModel.updateProfileImage(it.toString())
                }
                binding.componentProfile.houseBoardPostAdd.setVisible(!viewModel.isUserProfileImageSet())
                setResult(Activity.RESULT_OK)
            }
        }

    private lateinit var imageGalleryCropFlowManager: ImageGalleryCropFlowManager

    override val viewModelClass: Class<HouseBoardPostViewModel>
        get() = HouseBoardPostViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.activityHouseBoardPostLoadingView

    override fun getContentLayout(): Int = R.layout.activity_house_board_post

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        imageGalleryCropFlowManager = ImageGalleryCropFlowManager(activityResultRegistry)

        setupViews()
        setupViewModel()

        lifecycle.addObserver(imageGalleryCropFlowManager)
        observeCropManagerEvents()
    }
    //endregion

    private fun setupViews() = with(binding) {
        with(componentToolbar) {
            toolbarTitle.text = getString(R.string.connect_board_title)
            toolbarBackBtn.clicks { onBackPressed() }
        }

        houseBoardName.text = intent.getStringExtra(HOUSE_BOARD_VENUE_POST_NAME)

        componentYourPost.houseBoardPostEditText.textChanges().subscribe {
            submitButton.isEnabled = viewModel.shouldEnableSubmitButton(it)
        }

        componentProfile.houseBoardPostProfileContainer.clicks {
            if (!viewModel.isUserProfileImageSet()) {
                viewModel.onProfileImageEditClicked()
                ImageSourceChooserBottomSheet.newInstance()
                    .withResultListener(ImageSourceChooserBottomSheet.REQUEST_KEY_IMAGE_SOURCE) { requestKey, bundle ->
                        if (requestKey == ImageSourceChooserBottomSheet.REQUEST_KEY_IMAGE_SOURCE)
                            when (bundle.getString(BundleKeys.EVENT)) {
                                ImageSourceChooserBottomSheet.ACTION_CAMERA -> imageGalleryCropFlowManager.onCameraSelected(
                                    this@HouseBoardPostActivity
                                )
                                ImageSourceChooserBottomSheet.ACTION_GALLERY -> imageGalleryCropFlowManager.onGallerySelected()
                            }
                    }
                    .showSafe(supportFragmentManager, ImageSourceChooserBottomSheet.TAG)
            }
        }

        componentProfile.houseBoardPostAdd.setVisible(!viewModel.isUserProfileImageSet())

        submitButton.clicks {
            if (viewModel.postId == null) {
                viewModel.submitPost(componentYourPost.houseBoardPostEditText.text)
            } else {
                CustomDialogFactory.createThemedAlertDialog(this@HouseBoardPostActivity,
                    getString(R.string.connect_board_alert_new_header),
                    getString(R.string.connect_board_alert_new_supporting),
                    getString(R.string.connect_board_alert_new_ok_cta),
                    getString(R.string.connect_board_alert_new_cancel_cta),
                    { _, _ -> viewModel.submitPost(componentYourPost.houseBoardPostEditText.text) })
                    .show()
            }
        }

        deleteButton.clicks {
            CustomDialogFactory.createThemedAlertDialog(this@HouseBoardPostActivity,
                getString(R.string.connect_board_alert_delete_header),
                getString(R.string.connect_board_alert_delete_supporting),
                getString(R.string.connect_board_alert_delete_ok_cta),
                getString(R.string.connect_board_alert_delete_cancel_cta),
                { _, _ -> viewModel.deletePost() })
                .show()
        }

        with(componentYourPost.houseBoardPostEditText) {
            imeOptions = EditorInfo.IME_ACTION_DONE
            setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        }
    }

    private fun setupViewModel() {
        viewModel.venueId = intent.getStringExtra(HOUSE_BOARD_VENUE_POST_ID) ?: ""
        viewModel.userData.observe(this) { loadUserData(it) }
        viewModel.post.observe(this) { loadPost(it) }
        viewModel.postSuccess.observe(this) { onPostSuccess() }
        viewModel.errorDialog.observe(this) {
            when (it) {
                ERROR_DIALOG_REJECTED -> showPostRejectedDialog()
                ERROR_DIALOG_NETWORK -> showFetchErrorDialog()
                ERROR_DIALOG_GENERIC -> showErrorDialogByErrorCode(this)
            }
        }
        viewModel.fetchPostInfo()
    }

    //region HouseBoardViewController
    private fun loadUserData(userData: HouseBoardUserData) {
        binding.componentProfile.houseBoardPostUserName.text =
            getString(R.string.more_membership_name_label).replaceBraces(
                userData.firstName,
                userData.lastName
            )
        binding.componentProfile.houseBoardPostProfileImage.setImageFromUrl(
            userData.imageUrl,
            R.drawable.ic_profile,
            true
        )
    }

    private fun loadPost(post: String) = with(binding) {
        componentYourPost.houseBoardPostEditText.setText(post)
        submitButton.isEnabled = false
        deleteButton.visibility = View.VISIBLE
    }

    private fun onPostSuccess() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    private fun showPostRejectedDialog() {
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.error_post_hours_header),
            getString(R.string.error_post_hours_supporting),
            getString(R.string.error_post_hours_cta)
        )
            .show()
    }

    private fun showFetchErrorDialog() {
        CustomDialogFactory.createThemedAlertDialog(
            this,
            getString(R.string.connect_post_error_header),
            getString(R.string.connect_post_error_supporting),
            getString(R.string.connect_post_error_ok_cta)
        )
            .show()
    }
    //endregion

    private fun observeCropManagerEvents() {
        imageGalleryCropFlowManager.cropManagerEvent.collectLatest(this) { event ->
            when (event) {
                is Event.ImageChosen -> croppingLauncher.launch(
                    imageGalleryCropFlowManager.createImageCropperIntent(this, event.imageUri)
                )
                is Event.PermissionGranted -> imageGalleryCropFlowManager.onCameraSelected(this)
            }
        }
    }

    companion object {
        const val HOUSE_BOARD_VENUE_POST_NAME = "HouseBoardVenuePostName"
        const val HOUSE_BOARD_VENUE_POST_COLOR = "HouseBoardVenuePostColor"
        const val HOUSE_BOARD_VENUE_POST_ID = "HouseBoardVenuePostId"
        const val HOUSE_BOARD_VENUE_POST_IMAGE = "HouseBoardVenuePostImage"

        const val HOUSE_BOARD_POST_REQUEST_CODE = 1773
    }
}

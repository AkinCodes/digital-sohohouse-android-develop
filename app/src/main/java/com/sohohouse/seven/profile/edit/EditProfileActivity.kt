package com.sohohouse.seven.profile.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.sohohouse.seven.R
import com.sohohouse.seven.base.mvvm.BaseMVVMActivity
import com.sohohouse.seven.base.mvvm.ErrorDialogViewController
import com.sohohouse.seven.base.mvvm.ErrorViewStateViewController
import com.sohohouse.seven.base.mvvm.Loadable
import com.sohohouse.seven.common.BundleKeys
import com.sohohouse.seven.common.analytics.AnalyticsManager
import com.sohohouse.seven.common.extensions.clicks
import com.sohohouse.seven.common.extensions.hideKeyboard
import com.sohohouse.seven.common.extensions.stringProvider
import com.sohohouse.seven.common.extensions.withResultListener
import com.sohohouse.seven.common.utils.collectLatest
import com.sohohouse.seven.common.views.*
import com.sohohouse.seven.databinding.ActivityEditProfileBinding
import com.sohohouse.seven.more.contact.MoreContactActivity
import com.sohohouse.seven.more.profile.crop.Event
import com.sohohouse.seven.more.profile.crop.ImageGalleryCropFlowManager
import com.sohohouse.seven.more.profile.crop.ImageSourceChooserBottomSheet
import com.sohohouse.seven.more.profile.crop.ImageSourceChooserBottomSheet.Companion.ACTION_CAMERA
import com.sohohouse.seven.more.profile.crop.ImageSourceChooserBottomSheet.Companion.ACTION_GALLERY
import com.sohohouse.seven.more.profile.crop.ImageSourceChooserBottomSheet.Companion.REQUEST_KEY_IMAGE_SOURCE
import com.sohohouse.seven.network.core.models.Interest
import com.sohohouse.seven.network.core.models.Occupation
import com.sohohouse.seven.profile.ProfileField
import com.sohohouse.seven.profile.edit.EditProfileFlowManager.Companion.REQ_CODE_EDIT_ASK_ME_ABOUT
import com.sohohouse.seven.profile.edit.EditProfileFlowManager.Companion.REQ_CODE_EDIT_PHONE
import com.sohohouse.seven.profile.edit.interests.EditInterestsBottomSheet
import com.sohohouse.seven.profile.edit.pronouns.EditPronounsFragment
import com.sohohouse.seven.profile.edit.socialmedia.EditSocialMediaBottomSheet
import java.lang.ref.WeakReference
import javax.inject.Inject

class EditProfileActivity : BaseMVVMActivity<EditProfileViewModel>(),
    EditProfileListener, TextAreaBottomSheet.Listener, EditSocialMediaBottomSheet.Listener,
    EditOccupationBottomSheet.Listener, EditCityBottomSheet.Listener,
    EditInterestsBottomSheet.Listener,
    ErrorDialogViewController, Loadable.View, ErrorViewStateViewController {

    companion object {
        const val FRAG_TAG_PROFILE_EDIT_MODAL = "FRAG_TAG_PROFILE_EDIT_MODAL"
    }

    val binding by viewBinding(ActivityEditProfileBinding::bind)

    @Inject
    lateinit var flowManager: EditProfileFlowManager

    override fun getContentLayout() = R.layout.activity_edit_profile

    override val viewModelClass: Class<EditProfileViewModel>
        get() = EditProfileViewModel::class.java

    override val loadingView: LoadingView
        get() = binding.activityEditProfileLoadingView

    private val adapter = EditProfileAdapter(this)

    private val changesMade get() = viewModel.changesMade.value == true

    private val croppingLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let {
                viewModel.updateProfileImage(it.toString())
            }
            viewModel.logSaveProfileImage()
        }
    }

    private lateinit var imageGalleryCropFlowManager: ImageGalleryCropFlowManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageGalleryCropFlowManager = ImageGalleryCropFlowManager(activityResultRegistry)
        setUpToolbar()
        setUpRecyclerView()
        observeDataItems()
        observeOpenEditorEvents()
        observeSaveEvent()
        observeErrorDialogEvents()
        observeErrorViewEvents()
        observeLoadingState(this)
        observeChangesMadeStatus()
        observeShowDiscardChangesMsgEvent()
        observeCloseScreenEvent()
        observeConfirmQuestionChange()
        setFragmentResultListener()
        viewModel.fetchData()

        lifecycle.addObserver(imageGalleryCropFlowManager)
        observeCropManagerEvents()

        viewModel.setScreenName(name= AnalyticsManager.Screens.EditProfile.name)
    }

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

    private fun setFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(
            EditPronounsFragment.REQ_CODE,
            lifecycleOwner
        )
        { _, result ->
            viewModel.onPronounsUpdated(
                result.getStringArrayList(BundleKeys.PRONOUNS) ?: emptyList()
            )
        }
    }

    private fun observeConfirmQuestionChange() {
        viewModel.showConfirmChangeQuestionDialog.observe(this) { listener ->
            CustomDialogFactory.createThemedAlertDialog(context = this,
                title = getString(R.string.confirm_change_profile_question_title),
                message = getString(R.string.confirm_change_profile_question_message),
                positiveButtonText = getString(R.string.cta_change),
                negativeButtonText = getString(R.string.cta_cancel),
                positiveClickListener = { _, _ -> listener(true) },
                negativeClickListener = { _, _ -> listener(false) })
                .show()
        }
    }

    private fun observeCloseScreenEvent() {
        viewModel.closeScreenEvent.observe(this) {
            finish()
        }
    }

    private fun observeShowDiscardChangesMsgEvent() {
        viewModel.showDiscardChangesMsgEvent.observe(this) {
            showDiscardChangesDialog()
        }
    }

    private fun observeChangesMadeStatus() {
        viewModel.changesMade.observe(this) { changesMade ->
            binding.editProfileSaveBtn.isEnabled = changesMade ?: false
            if (changesMade == true) {
                binding.editProfileSaveBtn.setText(R.string.profile_save_changes_cta)
            }
        }
    }

    private fun observeSaveEvent() {
        binding.editProfileSaveBtn.clicks {
            viewModel.onSaveClick()
        }
        viewModel.saveSuccessEvent.observe(this) {
            setResult(Activity.RESULT_OK)
            finish()
        }
        viewModel.profilePhotoUpdatedEvent.observe(this) {
            setResult(Activity.RESULT_OK)
        }
    }

    private fun observeOpenEditorEvents() {
        viewModel.openEditorEvent.observe(this) { profileField ->
            if (profileField != null) {
                openEditorForField(profileField)
            }
        }
    }

    private fun openEditorForField(profileField: ProfileField<*>) {
        val factory = flowManager.createEditorBottomSheet(profileField, stringProvider) ?: return
        val bottomSheet = factory.create()
        bottomSheet.show(supportFragmentManager, FRAG_TAG_PROFILE_EDIT_MODAL)
    }

    private fun observeDataItems() {
        viewModel.profileAdapterItems.observe(this) {
            adapter.submitList(it ?: emptyList())
        }

        viewModel.itemChangeEvent.observe(this, Observer {
            if (it == null) return@Observer
            adapter.notifyItemChanged(it.index, it.payload)
        })
    }

    private val onScrollListener by lazy {
        object : ToolbarElevationScrollListener(WeakReference(binding.editProfileAppbar)) {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard()
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        with(binding.editProfileRv) {
            itemAnimator = DefaultItemAnimator().apply { supportsChangeAnimations = false }
            layoutManager = LinearLayoutManager(this@EditProfileActivity)
            adapter = this@EditProfileActivity.adapter
            addOnScrollListener(onScrollListener)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.editProfileToolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun onEditProfileFieldClick(field: EditProfileAdapterItem.Field<*>) {
        viewModel.onEditProfileFieldClick(field)
    }

    override fun onDropdownOptionSelected(
        field: ProfileField<out PickerItem?>,
        option: PickerItem?
    ) {
        viewModel.onOptionSelected(field, option)
    }

    override fun onTextAreaInputConfirmed(value: String, requestCode: Int?) {
        when (requestCode) {
            REQ_CODE_EDIT_ASK_ME_ABOUT -> viewModel.onAskMeAboutUpdated(value)
            REQ_CODE_EDIT_PHONE -> viewModel.onPhoneUpdated(value)
        }
    }

    override fun onConnectedAccountsConfirmed(field: ProfileField.SocialMedia) {
        viewModel.onConnectedAccountsUpdated(field)
    }

    override fun onOccupationConfirmed(occupation: Occupation?) {
        viewModel.onOccupationUpdated(occupation)
    }

    override fun onCityConfirmed(value: String?) {
        viewModel.onCityUpdated(value)
    }

    override fun onInterestsConfirmed(interests: List<Interest>) {
        viewModel.onInterestsUpdated(interests)
    }

    override fun onBackPressed() {
        val editModal = supportFragmentManager.findFragmentByTag(FRAG_TAG_PROFILE_EDIT_MODAL)
        if (editModal != null) {
            supportFragmentManager.popBackStack()
        } else {
            viewModel.onAttemptGoBack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home && changesMade) {
            viewModel.onAttemptGoBack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDiscardChangesDialog() {
        CustomDialogFactory.createThemedAlertDialog(this,
            title = getString(R.string.profile_cancel_header),
            message = getString(R.string.profile_cancel_supporting),
            positiveButtonText = getString(R.string.profile_cancel_yes_cta),
            negativeButtonText = getString(R.string.profile_cancel_no_cta),
            positiveClickListener = { _, _ -> finish() })
            .show()
    }

    override fun onEditPhotoClick() {
        viewModel.logEditPhotoClick()

        ImageSourceChooserBottomSheet.newInstance()
            .withResultListener(REQUEST_KEY_IMAGE_SOURCE) { requestKey, bundle ->
                if (requestKey == REQUEST_KEY_IMAGE_SOURCE)
                    when (bundle.getString(BundleKeys.EVENT)) {
                        ACTION_CAMERA -> imageGalleryCropFlowManager.onCameraSelected(this)
                        ACTION_GALLERY -> imageGalleryCropFlowManager.onGallerySelected()
                    }
            }
            .showSafe(supportFragmentManager, ImageSourceChooserBottomSheet.TAG)
    }

    override fun onContactUsClick() {
        startActivity(Intent(this, MoreContactActivity::class.java))
    }

    override fun onUserFocusQuestion(question: EditProfileAdapterItem.Question) {
        viewModel.onUserFocusQuestion(question)
    }

    override fun onQuestionAnswerChange(question: EditProfileAdapterItem.Question) {
        viewModel.onQuestionAnswerChange(question)
    }

    override fun getErrorStateView(): ReloadableErrorStateView {
        return binding.errorState
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.editProfileRv.removeOnScrollListener(onScrollListener)
    }
}
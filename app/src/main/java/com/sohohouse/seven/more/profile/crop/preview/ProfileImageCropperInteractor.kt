package com.sohohouse.seven.more.profile.crop.preview

import android.graphics.Bitmap
import android.util.Base64
import com.sohohouse.seven.common.interactors.AccountInteractor
import com.sohohouse.seven.common.refresh.ZipRequestsUtil
import com.sohohouse.seven.common.user.UserManager
import com.sohohouse.seven.network.base.error.ServerError
import com.sohohouse.seven.network.base.model.Either
import com.sohohouse.seven.network.core.models.Profile
import com.sohohouse.seven.network.core.request.PatchProfilePhotoRequest
import timber.log.Timber
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileImageCropperInteractor @Inject constructor(
    private val zipRequestsUtil: ZipRequestsUtil,
    private val userManager: UserManager,
    private val accountInteractor: AccountInteractor,
) {

    suspend fun uploadImage(bitmap: Bitmap): Either<ServerError, Profile> {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val payload = Base64.encodeToString(byteArray, Base64.DEFAULT).replace("\n", "")

        val result = zipRequestsUtil.issueApiCallV2(
            PatchProfilePhotoRequest(
                userManager.profileID,
                "data:image/jpeg;base64,$payload"
            )
        )

        when (result) {
            is Either.Value -> {
                userManager.profileImageURL = result.value.imageUrl
                accountInteractor.updateCachedProfile(result.value)
            }
            is Either.Error -> Timber.e(result.error.toString())
            else -> Timber.i("Photo upload returned empty result")
        }

        return result
    }

}
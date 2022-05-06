package com.sohohouse.seven.network.common.utils

internal object NetworkVariantConfig {
    private const val ACCEPT_HEADER = "Accept:"
    private const val MOCK_PASSTHROUGH_HEADER = "x-forward-staging:"

    // CURRENTL API BREAKS WHEN SPECIFYING THE VERSION
    const val CORE_API_VERSION_HEADER = "$ACCEPT_HEADER " //"$ACCEPT_HEADER application/vnd.api-v1+json"

    // REQUIRED FOR PAYMENTS
    const val CORE_API_FORMS_VERSION_HEADER = "$ACCEPT_HEADER application/vnd.api-v3+json"

    const val MOCK_STAGING_PASSTHROUGH = "$MOCK_PASSTHROUGH_HEADER Android"
}
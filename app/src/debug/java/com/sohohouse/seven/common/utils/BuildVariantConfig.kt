package com.sohohouse.seven.common.utils

object BuildVariantConfig {
    private const val PROTOCOL = "https://"

    //    Soho Staging
    const val STAGING_CORE_API_HOSTNAME = PROTOCOL + "api.staging.sohohousedigital.com"
    const val STAGING_SOHO_WEB_HOSTNAME = PROTOCOL + "dih-master.staging.sohohousedigital.com"
    const val STAGING_AUTH_HOSTNAME = PROTOCOL + "identity-master.staging.sohohousedigital.com/"
    const val STAGING_AUTH_APPLICATION_ID =
        "c5b656450b07b5cb9540b5b9de133c76249f71b737ec100d1989047568cbc7be"
    const val STAGING_AUTH_CLIENT_SECRET =
        "ea17035c3ed73d551b6322db7c3336e4433cba8f281ad5f88be50c103d681234"
    const val STAGING_SOHO_FORGOT_PW_URL =
        PROTOCOL + "identity-master.staging.sohohousedigital.com/passwords/request_resets/new"
    const val STAGING_FORCE_UPDATE_HOSTNAME =
        PROTOCOL + "version-check.production.sohohousedigital.com/force_update/"
    const val STAGING_SEND_BIRD_BASE_URL =
        "https://api-5758ABB3-685F-43C9-A79C-5D54AC3A431E.sendbird.com/v3/"
    const val STAGING_SEND_BIRD_APP_KEY = "5758ABB3-685F-43C9-A79C-5D54AC3A431E"
    const val STAGING_UX_CAM_APP_KEY = "onfm75hv1m46sba"

    //Soho Production
    const val CORE_API_HOSTNAME = PROTOCOL + "api.production.sohohousedigital.com/"
    const val SOHO_WEB_HOSTNAME = PROTOCOL + "members.sohohouse.com"
    const val AUTH_HOSTNAME = PROTOCOL + "identity.sohohouse.com/"
    const val AUTH_APPLICATION_ID =
        "e7f9c1e1584911fcdd1d9ceb9f1ffac8e175e1ba639e5bcbc58ca76b9ea084f2"
    const val AUTH_CLIENT_SECRET =
        "0b816c944c156faec033bf45cd24e72953aceec2c2b51324e69cabaa392671ae"
    const val FORCE_UPDATE_HOSTNAME =
        PROTOCOL + "version-check.production.sohohousedigital.com/force_update/"
    const val SOHO_FORGOT_PW_URL = PROTOCOL + "identity.sohohouse.com/passwords/request_resets/new"
    const val SEND_BIRD_BASE_URL =
        "https://api-D3222584-1AF1-4F67-A66B-3457693B39F5.sendbird.com/v3/"
    const val SEND_BIRD_APP_KEY = "D3222584-1AF1-4F67-A66B-3457693B39F5"
    const val UX_CAM_APP_KEY = "c1frqbn7x0mndyt"

    /**
     * Hockey App
     */
    const val HOCKEY_APP_ID = "de027ac676ab4a6890ab1cd40de97cf3"

    /**
     * GA (Using our debug GA account)
     */
    const val GA_TRACKING_ID = "UA-129112310-1"

    /**
     * Raygun (staging)
     */
    const val RAYGUN_API_KEY = "XSvMAPwsKJybbgWiCqhbCQ"

    /**
     * Places API (staging)
     */
    const val PLACES_API_KEY = "AIzaSyBbjbesZmvdz_DpauqcSC_fL1gnNZhwZCA"
    const val PERKS_HOUSE_NOTE_ID = "16D42549-0862-4726-B4DF-E2651B706BD7"

    /**
     * Salesforce
     */
    const val SALESFORCE_APP_ID = "40b01e8e-287f-4739-a88b-09c9f02920fc"
    const val SALESFORCE_ACCESS_TOKEN = "RCdtfnbeliUPJSr5GWu6tAVu"
    const val SALESFORCE_SENDER_ID = "640636956918"
    const val SALESFORCE_ENDPOINT_URL =
        "https://mc7xzst97fy8wl1n33hh1k15gnbm.device.marketingcloudapis.com/"
    const val SALESFORCE_MID = "100017733"
}
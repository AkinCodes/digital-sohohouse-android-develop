package com.sohohouse.seven.common.utils

/**
 * Created by victoriamartin on 4/10/17.
 *
 *
 * All particular information related to the "prod" flavour should be placed in here
 * Endpoints, SDK Keys and any particular method needed
 */
object BuildVariantConfig {
    private const val PROTOCOL = "https://"
    const val SOHO_SIGN_UP_URL = PROTOCOL + "sohohouse.com/account/create"

    //    Soho Staging
    const val STAGING_CORE_API_HOSTNAME = PROTOCOL + "api.staging.sohohousedigital.com/"
    const val STAGING_SOHO_WEB_HOSTNAME = PROTOCOL + "dih-master.staging.sohohousedigital.com"
    const val STAGING_AUTH_HOSTNAME = PROTOCOL + "identity-master.staging.sohohousedigital.com/"
    const val STAGING_AUTH_APPLICATION_ID =
        "c5b656450b07b5cb9540b5b9de133c76249f71b737ec100d1989047568cbc7be"
    const val STAGING_AUTH_CLIENT_SECRET =
        "ea17035c3ed73d551b6322db7c3336e4433cba8f281ad5f88be50c103d681234"
    const val STAGING_SOHO_FORGOT_PW_URL =
        PROTOCOL + "identity-master.staging.sohohousedigital.com/passwords/request_resets/new"

    //Soho Production
    const val CORE_API_HOSTNAME = PROTOCOL + "api.production.sohohousedigital.com/"
    const val SOHO_WEB_HOSTNAME = PROTOCOL + "members.sohohouse.com"
    const val AUTH_HOSTNAME = PROTOCOL + "identity.sohohouse.com/"
    const val AUTH_APPLICATION_ID =
        "127feda9fa5999da02b19bce84b43c9b9da25013fe38f9d28562e46f976a097d"
    const val AUTH_CLIENT_SECRET =
        "c84a1e9a458a81f8aa1cbba0fced3196e5b6299ef1c8d7f3aebbd0ad55ccb694"
    const val SOHO_FORGOT_PW_URL = PROTOCOL + "identity.sohohouse.com/passwords/request_resets/new"
    const val STAGING_FORCE_UPDATE_HOSTNAME = PROTOCOL + "vcs-master.staging.sohohousedigital.com/"
    const val FORCE_UPDATE_HOSTNAME =
        PROTOCOL + "version-check.production.sohohousedigital.com/force_update/"
    const val STAGING_SEND_BIRD_BASE_URL =
        "https://api-5758ABB3-685F-43C9-A79C-5D54AC3A431E.sendbird.com/v3/"
    const val STAGING_SEND_BIRD_APP_KEY = "5758ABB3-685F-43C9-A79C-5D54AC3A431E"
    const val SEND_BIRD_BASE_URL =
        "https://api-D3222584-1AF1-4F67-A66B-3457693B39F5.sendbird.com/v3/"
    const val SEND_BIRD_APP_KEY = "D3222584-1AF1-4F67-A66B-3457693B39F5"
    const val STAGING_UX_CAM_APP_KEY = "onfm75hv1m46sba"
    const val UX_CAM_APP_KEY = "c1frqbn7x0mndyt"

    /**
     * Hockey App
     */
    const val HOCKEY_APP_ID = "de027ac676ab4a6890ab1cd40de97cf3"

    /**
     * GA (using Soho Production UA account)
     */
    const val GA_TRACKING_ID = "UA-128492442-1"

    /**
     * Raygun (prod)
     */
    const val RAYGUN_API_KEY = "usrqC2AqTzQagA5urmwLpw"

    /**
     * Places API (prod)
     */
    const val PLACES_API_KEY = "AIzaSyBbjbesZmvdz_DpauqcSC_fL1gnNZhwZCA"
    const val PERKS_HOUSE_NOTE_ID = "C8D3B459-1E8B-4275-A3CD-1C93C5ECEEA2"

    /**
     * Salesforce
     */
    const val SALESFORCE_APP_ID = "fff7b2a7-9bb8-4aaf-bb4e-f93bb2db6fb5"
    const val SALESFORCE_ACCESS_TOKEN = "QwNcfrKAiUpkcTMzl8jYuuSe"
    const val SALESFORCE_SENDER_ID = "500597056812"
    const val SALESFORCE_ENDPOINT_URL =
        "https://mc7xzst97fy8wl1n33hh1k15gnbm.device.marketingcloudapis.com/"
    const val SALESFORCE_MID = "100008926"
}
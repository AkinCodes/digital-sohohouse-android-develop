package com.sohohouse.seven.profile.edit.pronouns

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Suppress("EnumEntryName", "SpellCheckingInspection")
@Parcelize
enum class GenderPronoun : Parcelable {
    co,
    cos,
    e,
    ey,
    em,
    eir,
    fae,
    faer,
    mer,
    mers,
    he,
    him,
    his,
    she,
    her,
    hers,
    they,
    them,
    theirs,
    ne,
    nir,
    nirs,
    nee,
    ner,
    ners,
    per,
    pers,
    thon,
    thons,
    ve,
    ver,
    vis,
    vi,
    vir,
    xe,
    xem,
    xyr,
    ze,
    zie,
    zir,
    hir;

    val type: Type
        get() = when (this) {
            e, ey, fae, he, per, she, they, ve, xe, ze, zie, co, ne, nee, vi -> {
                Type.NOMINATIVE
            }
            faer, em, him, her, them, ver, xem, hir, nir, ner, mer, vir, zir, thon -> {
                Type.ACCUSATIVE
            }
            cos, eir, mers, his, hers, theirs, nirs, ners, pers, thons, vis, xyr -> {
                Type.POSSESSIVE
            }
        }

    enum class Type {
        NOMINATIVE,
        ACCUSATIVE,
        POSSESSIVE;

        val order: Int
            get() = when (this) {
                NOMINATIVE -> 0
                ACCUSATIVE -> 1
                POSSESSIVE -> 2
            }
    }
}
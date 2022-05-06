package com.sohohouse.seven.common.user

enum class GymMembership {
    NONE,
    ACTIVE,
    ACTIVE_PLUS;

    fun isActive(): Boolean = this == ACTIVE

    fun isActivePlus(): Boolean = this == ACTIVE_PLUS

    fun hasMembership(): Boolean = this != NONE

    companion object {
        fun get(name: String): GymMembership {
            return try {
                valueOf(name)
            } catch (e: IndexOutOfBoundsException) {
                NONE
            }
        }
    }
}


package com.sohohouse.seven.common.dagger.module

import com.sohohouse.seven.common.dagger.scope.OpenCheckScope
import com.sohohouse.seven.housepay.checkdetail.open.pay.*
import com.sohohouse.seven.housepay.discounts.DiscountManager
import com.sohohouse.seven.housepay.discounts.DiscountManagerImpl
import com.sohohouse.seven.housepay.housecredit.HouseCreditManager
import com.sohohouse.seven.housepay.housecredit.HouseCreditManagerImpl
import com.sohohouse.seven.housepay.housecredit.HouseCreditRepo
import com.sohohouse.seven.housepay.housecredit.HouseCreditRepoImpl
import com.sohohouse.seven.housepay.payment.CheckPaymentMethodManager
import com.sohohouse.seven.housepay.payment.CheckPaymentMethodManagerImpl
import com.sohohouse.seven.housepay.tips.CheckTipsManager
import com.sohohouse.seven.housepay.tips.CheckTipsManagerImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class OpenCheckModule {

    @Binds
    @OpenCheckScope
    abstract fun bindHouseCreditManager(impl: HouseCreditManagerImpl): HouseCreditManager

    @Binds
    @OpenCheckScope
    abstract fun bindCheckTipsManager(impl: CheckTipsManagerImpl): CheckTipsManager

    @Binds
    @OpenCheckScope
    abstract fun bindCheckDiscountManager(impl: DiscountManagerImpl): DiscountManager

    @Binds
    abstract fun bindPayCheck(impl: PayCheckImpl): PayCheck

    @Binds
    abstract fun bindValidateCheck(impl: ValidateCheckImpl): ValidateCheck

}
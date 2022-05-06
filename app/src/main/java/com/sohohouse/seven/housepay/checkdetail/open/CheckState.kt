package com.sohohouse.seven.housepay.checkdetail.open

enum class CheckState {
    Undetermined,   //When we do not know the state of the check
    Working,        //When the check is open and discounts have not yet been applied
    Paying,         //When the check is open and discounts have been applied, payment options showing
    Paid            //When the check has been closed or paid
}
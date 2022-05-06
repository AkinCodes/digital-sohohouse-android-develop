package com.sohohouse.seven.housenotes.detail

import com.sohohouse.seven.base.error.ErrorViewStateViewController
import com.sohohouse.seven.base.load.LoadViewController
import com.sohohouse.seven.base.mvpimplementation.ViewController
import com.sohohouse.seven.housenotes.detail.model.HouseNoteDetailBaseItem
import com.sohohouse.seven.perks.details.PerkDetailClipboardItem

interface HouseNoteDetailViewController : ViewController, LoadViewController,
    ErrorViewStateViewController {
    fun onDataReady(dataList: List<HouseNoteDetailBaseItem>)
    fun showBottomTab(perksInfoItem: PerkDetailClipboardItem)
}
package com.sohohouse.seven.housenotes.detail.model

import com.sohohouse.seven.housenotes.detail.HouseNoteDetailItemType
import com.sohohouse.seven.network.core.models.Body

class HouseNoteDetailBodyItem(val bodyItem: Body) :
    HouseNoteDetailBaseItem(HouseNoteDetailItemType.BODY)
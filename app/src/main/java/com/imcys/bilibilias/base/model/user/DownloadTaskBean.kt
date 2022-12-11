package com.imcys.bilibilias.base.model.user

import com.imcys.bilibilias.home.ui.model.DashVideoPlayBean
import com.imcys.bilibilias.home.ui.model.VideoPageListData
import com.imcys.bilibilias.home.ui.model.VideoPlayBean

data class DownloadTaskDataBean(
    val cid: String,
    val pageTitle: String,
    //分辨率
    val qn: String,
    //视频获取方式选择
    val fnval: String = "80",
    //
    val platform: String = "pc",
    val dashVideoPlayBean: DashVideoPlayBean,
    val videoPageDataData: VideoPageListData.DataBean,
)
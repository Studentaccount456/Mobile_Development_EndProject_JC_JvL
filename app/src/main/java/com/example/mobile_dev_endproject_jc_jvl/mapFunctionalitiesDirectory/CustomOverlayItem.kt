package com.example.mobile_dev_endproject_jc_jvl.mapFunctionalitiesDirectory

import org.osmdroid.api.IGeoPoint
import org.osmdroid.views.overlay.OverlayItem

class CustomOverlayItem(title: String?, snippet: String?, point: IGeoPoint?) :
    OverlayItem(title, snippet, point) {
    var extraData: HashMap<String, String?> = HashMap()
}
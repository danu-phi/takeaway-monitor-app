package com.phsmk.id.takeaway_monitor.util

object Constants {
    const val LOGO_FOLDER = "logos"
    const val EMPTY = ""

    interface ORDER_TYPE {
        companion object {
            const val DELIVERY = "D"
            const val TAKEAWAY = "C"
            const val DINE_IN = "I"
            const val EAT_IN = "E"
            const val AGGREGATOR = "A"
        }
    }
}

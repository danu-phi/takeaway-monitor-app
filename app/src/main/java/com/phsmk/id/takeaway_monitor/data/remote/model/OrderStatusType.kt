package com.phsmk.id.takeaway_monitor.data.remote.model

enum class OrderStatusType(var statusNumber: String) {
    ALL("0"),
    FINISHED("1"),
    PARKED("2"),
    ORDERED("3"), //Edit
    CHECKOUT("4"),
    CANCELED("5"), //
    COOKED("6"),
    DELIVERING("7"),
    EDITING("8"),
    DELIVERED("9"),
    COOKING("10"),
    PICKEDUP("11");


    override fun toString(): String {
        return when (this) {
            ALL -> "All"
            FINISHED -> "Finished"
            PARKED -> "Parked"
            ORDERED -> "Ordered"
            CHECKOUT -> "Checkout"
            CANCELED -> "Canceled"
            COOKED -> "Cooked"
            DELIVERING -> "Delivering"
            EDITING -> "Editing"
            DELIVERED -> "Delivered"
            COOKING -> "Cooking"
            PICKEDUP -> "PickedUp"
        }
    }

    companion object {
        fun statusOf(statusNum: String?): OrderStatusType {
            var result = ORDERED
            for (status in OrderStatusType.values()) {
                if (status.statusNumber == statusNum) {
                    result = status
                    break
                }
            }
            return result
        }
    }
}

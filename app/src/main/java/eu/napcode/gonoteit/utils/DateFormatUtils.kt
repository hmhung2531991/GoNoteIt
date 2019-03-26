package eu.napcode.gonoteit.utils

import java.text.SimpleDateFormat
import java.util.*

public var dateFormatWithTime =
        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())

public var dateFormat =
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


public var timeFormat =
        SimpleDateFormat("HH:mm", Locale.getDefault())

public fun getTimestampLong(timestamp: Long?) : Long? {

    if (timestamp == null) {
        return null
    }

    return timestamp * 1000
}

public fun getTimestampShort(timestamp: Long?) : Long? {

    if (timestamp == null) {
        return null
    }

    return timestamp / 1000
}
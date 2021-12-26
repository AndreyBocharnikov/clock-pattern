package timer

import java.time.Instant

interface Clock {
    val now: Instant
}


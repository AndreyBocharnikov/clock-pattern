package timer

import java.time.Instant

class ClockImpl : Clock {
    override val now: Instant
        get() = Instant.now()
}

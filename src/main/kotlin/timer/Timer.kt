package timer

import java.time.Instant
import java.time.temporal.TemporalAmount
import java.util.concurrent.atomic.AtomicReference


class Timer(initTime: Instant) : Clock {
    private val nowTime: AtomicReference<Instant> = AtomicReference(initTime)

    override val now: Instant
        get() {
            return nowTime.get()
        }

    fun add(amount: TemporalAmount): Instant {
        return nowTime.updateAndGet { t: Instant -> t.plus(amount) }
    }
}
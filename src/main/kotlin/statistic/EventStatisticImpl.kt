package statistic

import timer.Clock
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

private val DELETE_TIME_INTERVAL = Duration.ofHours(1)
private val MIN_IN_DELETE_TIME_INTERVAL = DELETE_TIME_INTERVAL.toMinutes().toDouble()


class EventStatisticImpl(private val clock: Clock) : EventStatistic<Double> {
    private class Event(val name: String, val time: Instant)

    private val eventQueue: Queue<Event> = ArrayDeque()
    private val eventCounter: MutableMap<String, Int> = HashMap()

    private fun removeOldEvents(time: Instant) {
        val instant: Instant = time.minus(DELETE_TIME_INTERVAL)
        while (!eventQueue.isEmpty() &&
                eventQueue.peek().time.isBefore(instant)) {
            val eventName = eventQueue.poll().name
            eventCounter.computeIfPresent(eventName)
            { _: String?, oldVal: Int -> (oldVal - 1).coerceAtLeast(0)}
        }
    }

    override fun incEvent(name: String) {
        eventQueue.add(Event(name, clock.now))
        eventCounter.compute(name)
        { _: String?, value: Int? -> value?.inc() ?: 1 }
    }

    override fun getEventStatisticByName(name: String): Double {
        removeOldEvents(clock.now)
        return eventCounter.getOrDefault(name, 0) / MIN_IN_DELETE_TIME_INTERVAL
    }

    override fun getAllEventStatistic(): Map<String, Double> {
        removeOldEvents(clock.now)
        return eventCounter.entries.
               stream().
               collect(Collectors.toMap(
                { it.key },
                { it.value / MIN_IN_DELETE_TIME_INTERVAL }
               ))
    }

    override fun printStatistic() {
        getAllEventStatistic().forEach {
            println("Event name: ${it.key}, rpm: ${it.value}")
        }
    }
}

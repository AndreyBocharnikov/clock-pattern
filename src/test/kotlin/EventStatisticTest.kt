
import junit.framework.Assert.assertEquals
import org.junit.Test
import statistic.EventStatisticImpl
import timer.ClockImpl
import timer.Timer
import java.time.Duration
import java.time.Instant

private const val MIN_PER_HOUR = 60

class EventStatisticTest {

    @Test
    fun test_simple() {
        val eventStatistic = EventStatisticImpl(ClockImpl())
        eventStatistic.incEvent("a")
        eventStatistic.incEvent("b")
        eventStatistic.incEvent("c")
        assertEquals(eventStatistic.getEventStatisticByName("a"), 1.0 / MIN_PER_HOUR)
        assertEquals(eventStatistic.getEventStatisticByName("b"), 1.0 / MIN_PER_HOUR)
        assertEquals(eventStatistic.getEventStatisticByName("c"), 1.0 / MIN_PER_HOUR)
        assertEquals(eventStatistic.getEventStatisticByName("kek"), 0.0 / MIN_PER_HOUR)

    }

    @Test
    fun test_simple2() {
        val eventStatistic = EventStatisticImpl(ClockImpl())
        eventStatistic.incEvent("1")
        eventStatistic.incEvent("2")
        eventStatistic.incEvent("1")
        eventStatistic.incEvent("3")
        eventStatistic.incEvent("2")
        assertEquals(
            eventStatistic.getAllEventStatistic(), mapOf(
                Pair("1", 2.0 / MIN_PER_HOUR),
                Pair("2", 2.0 / MIN_PER_HOUR),
                Pair("3", 1.0 / MIN_PER_HOUR)
            )
        )
    }

    @Test
    fun test_simple3() {
        val timer = Timer(Instant.now())
        val eventStatistic = EventStatisticImpl(timer)
        eventStatistic.incEvent("a")
        eventStatistic.incEvent("a")
        timer.add(Duration.ofMinutes(500))
        eventStatistic.incEvent("b")
        eventStatistic.incEvent("b")
        assertEquals(eventStatistic.getEventStatisticByName("b"), 2.0 / MIN_PER_HOUR,)
        assertEquals(eventStatistic.getEventStatisticByName("a"), 0.0)
        assertEquals(
            eventStatistic.getAllEventStatistic(), mapOf(
                Pair("b", 2.0 / MIN_PER_HOUR),
                Pair("a", 0.0)
            )
        )
    }

    @Test
    fun test_hard3() {
        val timer = Timer(Instant.now())
        val eventStatistic = EventStatisticImpl(timer)
        eventStatistic.incEvent("1")
        eventStatistic.incEvent("2")
        eventStatistic.incEvent("1")
        timer.add(Duration.ofMinutes(30))
        assertEquals(
            eventStatistic.getAllEventStatistic(), mapOf(
                Pair("1", 2.0 / MIN_PER_HOUR),
                Pair("2", 1.0 / MIN_PER_HOUR)
            )
        )
        eventStatistic.incEvent("2")
        eventStatistic.incEvent("3")
        timer.add(Duration.ofMinutes(30))
        assertEquals(
            eventStatistic.getAllEventStatistic(), mapOf(
                Pair("1", 2.0 / MIN_PER_HOUR),
                Pair("2", 2.0 / MIN_PER_HOUR),
                Pair("3", 1.0 / MIN_PER_HOUR)
            )
        )
        timer.add(Duration.ofMinutes(15))
        assertEquals(
            eventStatistic.getAllEventStatistic(), mapOf(
                Pair("1", 0.0),
                Pair("2", 1.0 / MIN_PER_HOUR),
                Pair("3", 1.0 / MIN_PER_HOUR)
            )
        )
    }
}
package me.elsiff.morefish.fishing.competition

import me.elsiff.morefish.fishing.Fish
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.min

/**
 * Created by elsiff on 2018-12-25.
 */
class FishingCompetition {
    var state: State = State.DISABLED
    private val records: TreeSet<Record> = sortedSetOf(Comparator.reverseOrder())

    val ranking: List<Record>
        get() = records.toList()

    fun enable() {
        checkStateDisabled()

        state = State.ENABLED
    }

    fun disable() {
        checkStateEnabled()

        state = State.DISABLED
    }

    fun isEnabled(): Boolean =
        state == State.ENABLED

    fun isDisabled(): Boolean =
        state == State.DISABLED

    fun willBeNewFirst(catcher: Player, fish: Fish): Boolean {
        return records.isEmpty() || records.first().let { fish.length > it.fish.length && it.fisher != catcher }
    }

    fun putRecord(record: Record) {
        checkStateEnabled()

        if (containsContestant(record.fisher)) {
            val oldRecord = recordOf(record.fisher)
            if (record.fish.length > oldRecord.fish.length) {
                records.remove(oldRecord)
                records.add(record)
            }
        } else {
            records.add(record)
        }
    }

    fun containsContestant(contestant: Player): Boolean =
        records.any { it.fisher == contestant }

    fun recordOf(contestant: Player): Record {
        for (record in records) {
            if (record.fisher == contestant) {
                return record
            }
        }
        throw IllegalStateException("Record not found")
    }

    fun rankedRecordOf(contestant: Player): Pair<Int, Record> {
        for ((index, record) in records.withIndex()) {
            if (record.fisher == contestant) {
                return Pair(index + 1, record)
            }
        }
        throw IllegalStateException("Record not found")
    }

    fun recordOf(rankNumber: Int): Record {
        require(rankNumber >= 1 && rankNumber <= records.size) { "Rank number is out of records size" }
        return records.elementAt(rankNumber - 1)
    }

    fun top(size: Int): List<Record> =
        records.toList().subList(0, min(size, records.size))

    fun clearRecords() =
        records.clear()

    private fun checkStateEnabled() =
        check(state == State.ENABLED) { "Fishing competition hasn't enabled" }

    private fun checkStateDisabled() =
        check(state == State.DISABLED) { "Fishing competition hasn't disabled" }

    enum class State { ENABLED, DISABLED }
}
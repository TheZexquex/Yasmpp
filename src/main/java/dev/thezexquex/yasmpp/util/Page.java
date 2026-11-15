package dev.thezexquex.yasmpp.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * One page of a ranked list.
 *
 * @param selfAbove the viewer's own entry if it is ranked before this page
 * @param selfBelow the viewer's own entry if it is ranked after this page
 */
public record Page<T>(int number, int maxPage, List<Ranked<T>> entries, Optional<Ranked<T>> selfAbove, Optional<Ranked<T>> selfBelow) {
    public static final int SIZE = 10;

    /**
     * @param sorted list in ranking order
     * @param number 1-based page number
     * @param isSelf matches the viewer's own entry
     * @return the page, or empty if it does not exist
     */
    public static <T> Optional<Page<T>> of(List<T> sorted, int number, Predicate<T> isSelf) {
        var maxPage = Math.max(1, Math.ceilDiv(sorted.size(), SIZE));
        if (number < 1 || number > maxPage) {
            return Optional.empty();
        }

        var from = (number - 1) * SIZE;
        var to = Math.min(from + SIZE, sorted.size());

        var entries = new ArrayList<Ranked<T>>(to - from);
        for (var i = from; i < to; i++) {
            entries.add(ranked(sorted, i, isSelf));
        }

        Optional<Ranked<T>> selfAbove = Optional.empty();
        Optional<Ranked<T>> selfBelow = Optional.empty();
        for (var i = 0; i < sorted.size(); i++) {
            if (isSelf.test(sorted.get(i))) {
                if (i < from) {
                    selfAbove = Optional.of(ranked(sorted, i, isSelf));
                } else if (i >= to) {
                    selfBelow = Optional.of(ranked(sorted, i, isSelf));
                }
                break;
            }
        }

        return Optional.of(new Page<>(number, maxPage, entries, selfAbove, selfBelow));
    }

    public boolean hasPrevious() {
        return number > 1;
    }

    public boolean hasNext() {
        return number < maxPage;
    }

    private static <T> Ranked<T> ranked(List<T> sorted, int index, Predicate<T> isSelf) {
        var value = sorted.get(index);
        return new Ranked<>(index + 1, value, isSelf.test(value));
    }

    public record Ranked<T>(int rank, T value, boolean self) {
    }
}

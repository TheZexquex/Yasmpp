package dev.thezexquex.yasmpp.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class PageTest {

    private static List<Integer> items(int count) {
        return IntStream.rangeClosed(1, count).boxed().toList();
    }

    @Test
    void firstPageHasTenEntries() {
        var page = Page.of(items(25), 1, item -> false).orElseThrow();

        assertEquals(10, page.entries().size());
        assertEquals(1, page.entries().getFirst().rank());
        assertEquals(10, page.entries().getLast().rank());
        assertEquals(3, page.maxPage());
        assertFalse(page.hasPrevious());
        assertTrue(page.hasNext());
    }

    @Test
    void lastPageHasRemainingEntries() {
        var page = Page.of(items(25), 3, item -> false).orElseThrow();

        assertEquals(List.of(21, 22, 23, 24, 25), page.entries().stream().map(Page.Ranked::value).toList());
        assertTrue(page.hasPrevious());
        assertFalse(page.hasNext());
    }

    @Test
    void noEmptyTrailingPageForFullPages() {
        assertEquals(2, Page.of(items(20), 1, item -> false).orElseThrow().maxPage());
        assertTrue(Page.of(items(20), 3, item -> false).isEmpty());
    }

    @Test
    void outOfRangePagesDoNotExist() {
        assertTrue(Page.of(items(5), 0, item -> false).isEmpty());
        assertTrue(Page.of(items(5), 2, item -> false).isEmpty());
    }

    @Test
    void emptyListHasOneEmptyPage() {
        var page = Page.of(List.<Integer>of(), 1, item -> false).orElseThrow();

        assertTrue(page.entries().isEmpty());
        assertEquals(1, page.maxPage());
    }

    @Test
    void selfOnPageIsMarkedInPlace() {
        var page = Page.of(items(25), 1, item -> item == 4).orElseThrow();

        assertTrue(page.entries().get(3).self());
        assertTrue(page.selfAbove().isEmpty());
        assertTrue(page.selfBelow().isEmpty());
    }

    @Test
    void selfBeforePageIsShownAbove() {
        var page = Page.of(items(25), 2, item -> item == 4).orElseThrow();

        assertEquals(4, page.selfAbove().orElseThrow().rank());
        assertTrue(page.selfBelow().isEmpty());
    }

    @Test
    void selfAfterPageIsShownBelow() {
        var page = Page.of(items(25), 1, item -> item == 23).orElseThrow();

        assertEquals(23, page.selfBelow().orElseThrow().rank());
        assertTrue(page.selfAbove().isEmpty());
    }

    @Test
    void unrankedViewerIsNotShown() {
        var page = Page.of(items(25), 1, item -> false).orElseThrow();

        assertTrue(page.selfAbove().isEmpty());
        assertTrue(page.selfBelow().isEmpty());
        assertTrue(page.entries().stream().noneMatch(Page.Ranked::self));
    }
}

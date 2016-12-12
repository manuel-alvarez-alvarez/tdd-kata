package es.manuel.tdd;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Character.toLowerCase;

/**
 * Representation of a cell
 */
public class Cell {

    private static final char ALIVE_CELL = 'O';
    private static final char DEATH_CELL = '.';
    private static final String COLUMN_SEPARATOR = "";
    private static final String ROW_SEPARATOR = "\n";

    private final int x;
    private final int y;
    private final boolean alive;

    private Cell(final int x, final int y, final boolean alive) {
        this.x = x;
        this.y = y;
        this.alive = alive;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isAlive() {
        return alive;
    }

    public Cell kill() {
        if (!alive) {
            throw new IllegalStateException("Can't kill a dead cell...");
        }
        return of(x, y, false);
    }

    public Cell resurrect() {
        if (alive) {
            throw new IllegalStateException("Can't resurrect a living cell...");
        }
        return of(x, y, true);
    }

    @Override
    public String toString() {
        return String.format("Cell{x=%s,y=%s,alive=%s}", x, y, alive);
    }

    public static Cell of(final int x, final int y, final boolean alive) {
        return new Cell(x, y, alive);
    }

    public static Cell dead(final int x, final int y) {
        return of(x, y, false);
    }

    public static Cell alive(final int x, final int y) {
        return of(x, y, true);
    }

    public static Collection<Cell> ofWorld(final String world) {
        if (world.isEmpty()) {
            return Collections.emptyList();
        }
        String[] lines = world.split(ROW_SEPARATOR);
        return IntStream.range(0, lines.length)
                .mapToObj(y -> IntStream.range(0, lines[y].length()).mapToObj(x -> toCell(x, y, toLowerCase(lines[y].charAt(x)))))
                .flatMap(Function.identity())
                .filter(it -> it != null)
                .collect(Collectors.toList());
    }

    public static String toWorld(final Collection<Cell> cells) {
        if (cells.isEmpty()) {
            return "";
        }
        return cells.stream()
                .collect(Collectors.groupingBy(Cell::getY)).values().stream()
                .map(row -> row.stream()
                        .map(it -> it.isAlive() ? ALIVE_CELL : DEATH_CELL)
                        .map(String::valueOf)
                        .collect(Collectors.joining(COLUMN_SEPARATOR)))
                .collect(Collectors.joining(ROW_SEPARATOR));
    }

    private static Cell toCell(final int x, final int y, final char state) {
        switch (state) {
            case Cell.ALIVE_CELL:
                return Cell.alive(x, y);
            case Cell.DEATH_CELL:
                return Cell.dead(x, y);
            case ' ':
                return null;
            default:
                throw new IllegalArgumentException("Unknown cell state : " + state);
        }
    }
}

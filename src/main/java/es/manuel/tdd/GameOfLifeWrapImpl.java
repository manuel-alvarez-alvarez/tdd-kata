package es.manuel.tdd;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static es.manuel.tdd.Cell.dead;

/**
 * Implementation of the game of life with wrapping
 */
@Component(value = "wrap")
public class GameOfLifeWrapImpl implements GameOfLife {

    @Override
    public final Collection<Cell> nextGeneration(final Collection<Cell> currentGeneration) {
        if (currentGeneration.isEmpty()) {
            return currentGeneration;
        }
        return currentGeneration.stream().map(mappingFunction(currentGeneration)).collect(Collectors.toList());
    }

    private Function<Cell, Cell> mappingFunction(final Collection<Cell> world) {
        return (Cell cell) -> newState(cell, neighbours(cell, world).filter(Cell::isAlive).count());
    }

    private Stream<Cell> neighbours(final Cell cell, final Collection<Cell> world) {
        if (world.isEmpty()) {
            return Stream.empty();
        }
        List<Cell> list = new LinkedList<>(world);
        int width = list.stream().mapToInt(Cell::getX).max().orElse(0) + 1;
        int height = list.size() / width;

        // @formatter:off
        return Stream.of(
                dead(cell.getX() - 1, cell.getY() - 1), dead(cell.getX(), cell.getY() - 1), dead(cell.getX() + 1, cell.getY() - 1),
                dead(cell.getX() - 1, cell.getY()),                                         dead(cell.getX() + 1, cell.getY()),
                dead(cell.getX() - 1, cell.getY() + 1), dead(cell.getX(), cell.getY() + 1), dead(cell.getX() + 1, cell.getY() + 1)
        )
                .map(it -> Cell.of(
                        it.getX() < 0 ? width - 1 : it.getX() >= width ? 0 : it.getX(),
                        it.getY() < 0 ? height - 1 : it.getY() >= height ? 0 : it.getY(),
                        it.isAlive()))
                .mapToInt(item -> item.getX() + (width) * item.getY())
                .mapToObj(list::get);
        // @formatter:om
    }

    private Cell newState(final Cell cell, final long neighbours) {
        if (cell.isAlive()) {
            return neighbours == 2 || neighbours == 3 ? cell : cell.kill();
        } else {
            return neighbours == 3 ? cell.resurrect() : cell;
        }
    }


}

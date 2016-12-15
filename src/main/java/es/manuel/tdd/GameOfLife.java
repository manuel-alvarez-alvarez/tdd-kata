package es.manuel.tdd;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static es.manuel.tdd.Cell.dead;

/**
 * Interface of the business service to build.
 */
public interface GameOfLife {

    Collection<Cell> nextGeneration(Collection<Cell> currentGeneration);

    abstract class AbstractGameOfLife implements GameOfLife {

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
            int width = list.get(list.size() - 1).getX() + 1;
            int height = list.size() / width;

            // @formatter:off
            return Stream.of(
                    dead(cell.getX() - 1, cell.getY() - 1), dead(cell.getX(), cell.getY() - 1), dead(cell.getX() + 1, cell.getY() - 1),
                    dead(cell.getX() - 1, cell.getY()),                                         dead(cell.getX() + 1, cell.getY()),
                    dead(cell.getX() - 1, cell.getY() + 1), dead(cell.getX(), cell.getY() + 1), dead(cell.getX() + 1, cell.getY() + 1)
            )
                    .filter(it -> it.getX() >= 0 && it.getX() < width
                               && it.getY() >= 0 && it.getY() < height)
                    .mapToInt(item -> item.getX() + (width) * item.getY())
                    .mapToObj(list::get);
            // @formatter:om
        }

        protected abstract Cell newState(final Cell cell, final long neighbours);
    }

}

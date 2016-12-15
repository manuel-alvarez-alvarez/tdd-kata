package es.manuel.tdd;

import java.util.Collection;

/**
 * Interface of the business service to build.
 */
public interface GameOfLife {

    Collection<Cell> nextGeneration(Collection<Cell> currentGeneration);

}

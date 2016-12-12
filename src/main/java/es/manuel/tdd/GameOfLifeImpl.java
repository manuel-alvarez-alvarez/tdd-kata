package es.manuel.tdd;

import org.springframework.stereotype.Component;

/**
 * Implementation of the game of life algorithm
 */
@Component
public class GameOfLifeImpl extends GameOfLife.AbstractGameOfLife {

    @Override
    protected Cell newState(final Cell cell, final long neighbours) {
        throw new UnsupportedOperationException();
    }
}

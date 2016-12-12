package es.manuel.tdd;

import org.springframework.stereotype.Component;

/**
 * Client for the game of life service
 */
@Component
public class GameOfLifeImpl extends GameOfLife.AbstractGameOfLife {

    @Override
    protected Cell newState(final Cell cell, final long neighbours) {
        if (cell.isAlive()) {
            return neighbours == 2 || neighbours == 3 ? cell : cell.kill();
        } else {
            return neighbours == 3 ? cell.resurrect() : cell;
        }
    }

}

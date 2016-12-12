package es.manuel.tdd;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Unit test for the game of life client.
 */
@SpringBootTest(classes = GameOfLifeClient.class)
@RunWith(SpringRunner.class)
public class GameOfLifeTest {

    @Autowired
    private GameOfLife gameOfLife;

    @Test
    public void anEmptyWorldStaysEmpty() {
        Cell[][] world = toMatrix(gameOfLife.nextGeneration(Cell.ofWorld("")));

        assertThat(world.length, equalTo(0));
    }

    //@Test
    //public void anyLiveCellWithFewerThantTwoLiveNeighboursDies() {
    //}

    //@Test
    //public void anyLiveCellWithTwoLiveNeighboursLives() {
    //}

    //@Test
    //public void anyLiveCellWithThreeLiveNeighboursLives() {
    //}

    //@Test
    //public void anyLiveCellWithMoreThanThreeLiveNeighboursDies() {
    //}

    //@Test
    //public void anyDeadCellWithThreeLiveNeighboursResurrects() {
    //}


    private Cell[][] toMatrix(Collection<Cell> cells) {
        if (cells.isEmpty()) {
            return new Cell[0][0];
        }
        return cells.stream()
                .collect(Collectors.groupingBy(Cell::getY)).values().stream()
                .sorted((c1, c2) -> Integer.compare(c1.get(0).getY(), c2.get(0).getY()))
                .map(list -> list.stream().sorted((c1, c2) -> Integer.compare(c1.getX(), c2.getY())).toArray(Cell[]::new))
                .toArray(Cell[][]::new);
    }

}

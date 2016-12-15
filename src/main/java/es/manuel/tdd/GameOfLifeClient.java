package es.manuel.tdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Client app for the TDD-KATA
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GameOfLifeClient {

    @Controller
    public class GameOfLifeController {

        @Resource
        public GameOfLife gameOfLife;

        @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        @ResponseBody
        public List<String> nextGenerations(@RequestBody final String request, @RequestParam final int frames) throws IOException {
            return IntStream.range(0, frames).collect(
                    LinkedList::new,
                    (list, i) -> list.add(Cell.toWorld(gameOfLife.nextGeneration(Cell.ofWorld(i <= 0 ? request : list.get(list.size() - 1))))),
                    LinkedList::addAll);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(GameOfLifeClient.class, args);
    }
}

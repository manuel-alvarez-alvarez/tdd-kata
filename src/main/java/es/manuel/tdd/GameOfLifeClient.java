package es.manuel.tdd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collection;

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

        @PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
        @ResponseBody
        public String nextGeneration(@RequestBody final String currentGeneration) {
            return Cell.toWorld(gameOfLife.nextGeneration(Cell.ofWorld(currentGeneration)));
        }
    }

     public static void main(String[] args) {
        SpringApplication.run(GameOfLifeClient.class, args);
    }
}

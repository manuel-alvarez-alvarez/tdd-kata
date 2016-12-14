package es.manuel.tdd;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Client app for the TDD-KATA
 */
@SpringBootApplication
@EnableEurekaServer
public class GameOfLifeMaster {

    @Controller
    public class GameOfLifeController {

        @Resource
        private EurekaServerContext eurekaServerContext;

        @Resource
        private GameOfLife gameOfLife;

        @GetMapping(value = "/service", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        @ResponseBody
        public List<String> findServices() {
            return Optional.ofNullable(eurekaServerContext.getRegistry().getApplication("GAME-OF-LIFE-CLIENT"))
                    .map(Application::getInstances)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(InstanceInfo::getInstanceId)
                    .collect(Collectors.toList());
        }

        @PostMapping(value = "/service", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        @ResponseBody
        public Result invokeDefaultService(@RequestBody final String request, @RequestParam final int frames) throws IOException {
            List<String> right = IntStream.range(0, frames).collect(
                    LinkedList::new,
                    (list, i) -> list.add(Cell.toWorld(gameOfLife.nextGeneration(Cell.ofWorld(i == 0 ? request : list.get(list.size() - 1))))),
                    LinkedList::addAll);
            return Result.ok(right, right);
        }

        @PostMapping(value = "/service/{id}", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
        @ResponseBody
        public Result invokeService(@RequestBody final String request, @PathVariable final String id, @RequestParam final int frames) throws IOException {
            String serviceId = new String(Base64.getDecoder().decode(id));
            List<String> right = IntStream.range(0, frames).collect(
                    LinkedList::new,
                    (list, i) -> list.add(Cell.toWorld(gameOfLife.nextGeneration(Cell.ofWorld(i == 0 ? request : list.get(list.size() - 1))))),
                    LinkedList::addAll);
            return Optional.ofNullable(eurekaServerContext.getRegistry().getApplication("GAME-OF-LIFE-CLIENT"))
                    .map(application -> application.getByInstanceId(serviceId))
                    .map(instanceInfo -> {
                        RestTemplate template = new RestTemplate();
                        Map<String, Object> variables = new LinkedHashMap<>();
                        variables.put("frames", frames);
                        @SuppressWarnings("unchecked")
                        List<String> computed = template.postForObject(instanceInfo.getHomePageUrl(), request, List.class, variables);
                        return Result.ok(right, computed);
                    })
                    .orElse(Result.fail(HttpStatus.NOT_FOUND.value(), String.format("No service %s found", serviceId)));
        }
    }

    public static class Result {

        private final boolean error;
        private final int statusCode;
        private final String errorMessage;
        private final List<String> right;
        private final List<String> computed;

        public Result(boolean error, int statusCode, String errorMessage, List<String> right, List<String> computed) {
            this.error = error;
            this.statusCode = statusCode;
            this.errorMessage = errorMessage;
            this.right = right;
            this.computed = computed;
        }

        public static Result ok(List<String> right, List<String> computed) {
            return new Result(false, HttpStatus.OK.value(), null, right, computed);
        }

        public static Result fail(int statusCode, String errorMessage) {
            return new Result(true, statusCode, errorMessage, null, null);
        }

        public boolean isError() {
            return error;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public List<String> getRight() {
            return right;
        }

        public List<String> getComputed() {
            return computed;
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(GameOfLifeMaster.class, args);
    }
}

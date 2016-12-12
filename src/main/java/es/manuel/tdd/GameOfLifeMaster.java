package es.manuel.tdd;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        public Map<String, Object> invokeService(@RequestBody final String request) throws IOException {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("", Cell.toWorld(gameOfLife.nextGeneration(Cell.ofWorld(request))));
            Optional.ofNullable(eurekaServerContext.getRegistry().getApplication("GAME-OF-LIFE-CLIENT")).ifPresent(application ->
                    application.getInstances().forEach(instanceInfo -> {
                        RestTemplate template = new RestTemplate();
                        try {
                            String generation = template.postForObject(instanceInfo.getHomePageUrl(), request, String.class);
                            result.put(instanceInfo.getInstanceId(), generation);
                        } catch (HttpStatusCodeException e) {
                            Map<String, String> error = new LinkedHashMap<>();
                            error.put("error", Boolean.TRUE.toString());
                            error.put("message", e.getMessage());
                            result.put(instanceInfo.getInstanceId(), error);
                        }
                    }));

            return result;
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(GameOfLifeMaster.class, args);
    }
}

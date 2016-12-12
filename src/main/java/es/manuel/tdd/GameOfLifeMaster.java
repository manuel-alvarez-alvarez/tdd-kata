package es.manuel.tdd;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.eureka.EurekaServerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Client app for the TDD-KATA
 */
@SpringBootApplication
@EnableEurekaServer
public class GameOfLifeMaster {

    @Controller
    @RequestMapping(value = "/gol")
    public class GameOfLifeController {

        @Resource
        private EurekaServerContext eurekaServerContext;

        @GetMapping(value = "/service", produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseBody
        public List<InstanceInfo> getServices() {
            return eurekaServerContext.getRegistry().getApplication("GAME-OF-LIFE-CLIENT").getInstances();
        }

        @PostMapping(value = "/service/{instanceId}", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
        public void invokeService(@PathVariable final String instanceId, @RequestBody final String request, final HttpServletResponse response) throws IOException {
            InstanceInfo instanceInfo = eurekaServerContext.getRegistry()
                    .getApplication("GAME-OF-LIFE-CLIENT")
                    .getByInstanceId(new String(Base64.getDecoder().decode(instanceId)));
            RestTemplate template = new RestTemplate();
            try {
                String result = template.postForObject(instanceInfo.getHomePageUrl(), request, String.class);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(result);
            } catch (HttpStatusCodeException e) {
                response.setStatus(e.getStatusCode().value());
                response.getWriter().write(e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(GameOfLifeMaster.class, args);
    }
}

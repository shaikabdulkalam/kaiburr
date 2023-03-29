use mydb
db.createCollection("servers")


package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}


package com.example.demo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "servers")
public class Server {

    @Id
    private String id;

    private String name;

    private String language;

    private String framework;

    public Server() {
    }

    public Server(String name, String language, String framework) {
        this.name = name;
        this.language = language;
        this.framework = framework;
    }

    // getters and setters
}



package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServerRepository extends MongoRepository<Server, String> {

    Server findByNameContainingIgnoreCase(String name);

}



package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/servers")
public class ServerController {

    @Autowired
    private ServerRepository serverRepository;

    @GetMapping
    public ResponseEntity<List<Server>> getServers(@RequestParam(required = false) String id) {
        if (id != null) {
            Optional<Server> server = serverRepository.findById(id);
            if (server.isPresent()) {
                return ResponseEntity.ok().body(List.of(server.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.ok().body(serverRepository.findAll());
        }
    }

    @PutMapping
    public ResponseEntity<Void> putServer(@RequestBody Server server) {
        serverRepository.save(server);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServer(@PathVariable String id) {
        serverRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Server>> findServersByName(@RequestParam String name) {
        Server server = serverRepository.findByNameContainingIgnoreCase(name);
        if (server != null) {
            return ResponseEntity.ok().body(List.of(server));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

package dev.diagrammer.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder for public diagram URLs. Anyone can request them. There is no
 * diagram store yet, so every id answers 404.
 */
@RestController
public class PublicDiagramController {

    @RequestMapping(value = {"/diagrams", "/diagrams/**"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> missingDiagram() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Diagram not found");
    }
}

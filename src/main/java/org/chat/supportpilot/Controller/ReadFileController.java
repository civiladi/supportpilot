package org.chat.supportpilot.Controller;

import org.chat.supportpilot.Service.ReadFileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class ReadFileController {
    private static final List<String> allowedDocs = List.of("pdf","md","txt");
    private final ReadFileService readService;

    public ReadFileController(ReadFileService service){
        this.readService=service;
    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        if(file.isEmpty() || original==null){
            return ResponseEntity.badRequest().body(Map.of("error","empty file"));
        }
        String filename = original.replaceAll("[^A-Za-z0-9._-]", "_");
        String ext = original.substring(original.lastIndexOf(".")+1);
        if(!allowedDocs.contains(ext)){
            return ResponseEntity.badRequest().body(Map.of("error","unsupported file format"));
        }
        int chunks = readService.readFile(filename,file.getBytes());
        return ResponseEntity.ok().body(Map.of("source",filename,"chunks",chunks));
    }
}

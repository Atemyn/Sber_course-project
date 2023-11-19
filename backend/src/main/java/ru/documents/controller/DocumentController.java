package ru.documents.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.documents.controller.dto.DocumentDto;
import ru.documents.controller.dto.IdDto;
import ru.documents.controller.dto.IdsDto;
import ru.documents.controller.dto.Status;
import ru.documents.service.DocumentService;
import ru.documents.service.DocumentServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private DocumentService service;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto save(@Valid @RequestBody DocumentDto dto) {
        return service.save(dto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DocumentDto> get() {
        return service.findAll();
    }
    @PostMapping(
            path = "send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto send(@RequestBody IdDto id) {
        DocumentDto document = service.findById(id.getId());
        return service.update(document);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    @DeleteMapping
    public void deleteAll(@RequestBody IdsDto idsDto) {
        service.deleteAll(idsDto.getIds());
    }
}

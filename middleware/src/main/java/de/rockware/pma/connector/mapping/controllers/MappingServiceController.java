package de.rockware.pma.connector.mapping.controllers;

import de.rockware.pma.connector.common.services.ReadWriteService;
import de.rockware.pma.connector.execution.beans.AddValuesRequestBody;
import de.rockware.pma.connector.mapping.entities.Mapping;
import java.util.Collection;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("service/mapping")
@CrossOrigin(origins = "*")
@Slf4j
public class MappingServiceController {
  private final ReadWriteService<Mapping> mappingService;

  @Autowired
  public MappingServiceController(ReadWriteService<Mapping> mappingService) {
    this.mappingService = mappingService;
  }

  @GetMapping("getAll")
  public Collection<Mapping> getAll() {
    return mappingService.getAll();
  }

  @PostMapping("setAll")
  public void setAll(@RequestBody AddValuesRequestBody<Mapping> body) {
    if (Objects.isNull(body)) {
      log.debug("Null body: skipped");
      return;
    }
    mappingService.setAll(body.getValues());
  }

  @PostMapping("clear")
  public void clear() {
    mappingService.clear();
  }
}

package de.rockware.pma.connector.execution.controllers;

import de.rockware.pma.connector.execution.services.ExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("service/execution")
@CrossOrigin(origins = "*")
@Slf4j
public class ExecutionController {

  private final ExecutionService executionService;

  @Autowired
  public ExecutionController(ExecutionService executionService) {
    this.executionService = executionService;
  }

  @GetMapping("run")
  public void run() {
    executionService.run();
  }
}

package de.rockware.pma.integration.beans;

import java.util.List;

public interface Page<T> {
  List<T> getContent();

  int getTotalElements();
}

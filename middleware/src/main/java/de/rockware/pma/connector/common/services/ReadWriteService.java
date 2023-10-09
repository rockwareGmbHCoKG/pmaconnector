package de.rockware.pma.connector.common.services;

import java.util.Collection;

public interface ReadWriteService<T> {

  Collection<T> getAll();

  void setAll(T... values);

  void clear();
}

package de.rockware.pma.connector.common.factories;

public interface Factory<T, F> {

  T create(F context);
}

package org.skrymer.qrbuilder.decorator;

/**
 * Implement this interface to create custom decorators.
 *
 * Ordering of decorators might matter
 */
public interface Decorator<T> {

  T decorate(T qrCode);
}

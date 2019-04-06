package org.skrymer.qrbuilder.decorator;

/**
 * Implement this interface to create custom decorators
 */
public interface Decorator<T> {

  T decorate(T qrcode);
}

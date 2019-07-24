package de.unisaar.faphack.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObservable<T> implements Observable<T> {
  private List<Observer<T>> observers;

  public void register(Observer<T> observer) {
    // lazy initialization
    if (observers == null) {
      observers = new ArrayList<>();
    }
    observers.add(observer);
  };

  public void notifyObservers(T object) {
    if (observers != null)
      for(Observer<T> o: observers) { o.update(object); }
  }
}

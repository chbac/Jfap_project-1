package de.unisaar.faphack.model;

import de.unisaar.faphack.model.map.Tile;

public interface ObservableTile extends Observable<Tile>, TraitOwner {

  int getX();
  int getY();

}

package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Game;
import de.unisaar.faphack.model.MarshallingContext;
import de.unisaar.faphack.model.Storable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 *
 */
public class World implements Storable {
  public Game g;

  private List<Room> mapElements = new ArrayList<Room>();

  public World() {}

  @Override
  public void marshal(MarshallingContext c) {
	  c.write("game", g);
	  c.write("mapElements", mapElements);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    g = c.read("game");
    c.readAll("mapElements", mapElements);
  }

  public List<Room> getMapElements(){
    return mapElements;
  }
}

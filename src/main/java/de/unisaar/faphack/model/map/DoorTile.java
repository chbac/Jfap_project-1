package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 *
 */
public class DoorTile extends WallTile implements Storable, Observable<DoorTile> {
  private boolean open = false;

  private boolean locked = false;

  private Hallway hallway;

  private List<Observer<DoorTile>> observers;

  /**
   * To be opened by an item (key) the Effect of that item needs to create a m
   * atching ID.
   */
  private int keyId;

  public DoorTile() {
  }

  public DoorTile(int x, int y, Room room){
    super(x, y, room);
  }

  @Override
  public Tile willTake(Character c) {
	/* try to open the door; if it is locked, try to destroy*/
	if (locked && destructible != 0 && destructible < c.getPower()) {
		return this.hallway.fromTile;
	} else if (!locked) {
		return this.hallway.toTile;
	}
    return null;
  }

  @Override
  public void marshal(MarshallingContext c) {
    super.marshal(c);
    c.write("open", open? 1: 0);
    c.write("locked", locked? 1: 0);
    c.write("hallway", hallway);
    c.write("keyId", keyId);
  }

  @Override
  public void unmarshal(MarshallingContext c) {
    super.unmarshal(c);
    open = c.readInt("open") == 1? true: false;
    locked = c.readInt("locked") == 1? true: false;
    hallway = c.read("hallway");
    keyId = c.readInt("keyId");
  }

  public Hallway getHallway(){
    return hallway;
  }

  @Override
  public String getTrait() { return open ? OPENDOOR : DOOR; }

  @Override
  public void register(Observer<DoorTile> observer) {
    // lazy initialization
    // TODO please implement me!

  }

  @Override
  public void notifyObservers(DoorTile object) {
    // TODO please implement me!
  }


}

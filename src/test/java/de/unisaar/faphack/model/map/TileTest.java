package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Direction;
import de.unisaar.faphack.model.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TileTest {


  @Test
  void getNextTile() {
    Room r = TestUtils.createSimpleRoom(4,4, 1);
    Tile testObject = r.getTiles()[1][1];
    assertEquals(r.getTiles()[2][1],testObject.getNextTile(new Direction(1,0)));
    assertEquals(r.getTiles()[0][1], testObject.getNextTile(new Direction(-1,0)));
  }

}
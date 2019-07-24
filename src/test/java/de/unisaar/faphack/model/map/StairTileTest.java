package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import org.junit.jupiter.api.Test;

import static de.unisaar.faphack.model.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class StairTileTest {

  @Test
  void willTake() {
    Character character = createBaseCharacter("Foo", 3,3);
    StairTile t1 = new StairTile();
    StairTile t2 = new StairTile();
    connectStairTiles(t1,t2, false);
    assertEquals(t2, t1.willTake(character));
    assertEquals(t1, t2.willTake(character));
    connectStairTiles(t1, t2, true);
    assertEquals(t2, t1.willTake(character));
    assertNull(t2.willTake(character));
  }
}
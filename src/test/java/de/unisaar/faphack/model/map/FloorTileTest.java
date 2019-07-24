package de.unisaar.faphack.model.map;

import de.unisaar.faphack.model.Character;
import de.unisaar.faphack.model.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloorTileTest {

  /**
   * A Floor tile is accessible for a character if no other character is already on it.
   */
  @Test
  void willTake() {
    Character foo = TestUtils.createBaseCharacter("Foo", 2, 2);
    Character bar = TestUtils.createBaseCharacter("Bar", 2, 2);
    Room room = TestUtils.createSimpleRoom(8,8,1);
    Tile[][] tiles = room.getTiles();
    TestUtils.placeCharacter(foo, tiles[1][1]);
    TestUtils.placeCharacter(bar, tiles[2][2]);
    // the tile will take character foo as it is not occupied yet
    assertEquals(tiles[1][2], tiles[1][2].willTake(foo));
    // the tile will take character foo as foo already is on even this tile
    assertEquals(tiles[1][1], tiles[1][1].willTake(foo));
    // the tile won't take character foo as it is occupied by bar
    assertNull(tiles[2][2].willTake(foo));
  }
}
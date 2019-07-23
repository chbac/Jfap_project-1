package de.unisaar.faphack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DirectionTest {

  /**
   * Checks the Constructor of the Direction class
   */
  @Test
  public void TestConstructor(){

    Direction d = new Direction(0,0);
    assertNotNull(d);
    assertEquals(0,d.x);
    assertEquals(0,d.y);

    d = new Direction(1,0);
    assertNotNull(d);
    assertEquals(1,d.x);
    assertEquals(0,d.y);

    d = new Direction(0,1);
    assertNotNull(d);
    assertEquals(0,d.x);
    assertEquals(1,d.y);

    d = new Direction(1,1);
    assertNotNull(d);
    assertEquals(1,d.x);
    assertEquals(1,d.y);

    d = new Direction(0,2);
    assertNotNull(d);
    assertEquals(0,d.x);
    assertEquals(2,d.y);

    d = new Direction(0,-2);
    assertNotNull(d);
    assertEquals(0,d.x);
    assertEquals(-2,d.y);

    d = new Direction(-2,1);
    assertNotNull(d);
    assertEquals(-2,d.x);
    assertEquals(1,d.y);
  }

}

package com.vibecoder.vibecore.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationUtilTest {

    @Mock
    private World world;

    @Mock
    private World otherWorld;

    @BeforeEach
    void setUp() {
        lenient().when(world.getName()).thenReturn("world");
        lenient().when(otherWorld.getName()).thenReturn("nether");
    }

    // --- serializeLocation ---

    @Test
    void serializeLocation_validLocation_returnsMap() {
        Location loc = new Location(world, 10.5, 64.0, -20.3, 45.0f, -10.0f);
        Map<String, Object> result = LocationUtil.serializeLocation(loc);

        assertEquals("world", result.get("world"));
        assertEquals(10.5, result.get("x"));
        assertEquals(64.0, result.get("y"));
        assertEquals(-20.3, result.get("z"));
        assertEquals(45.0, result.get("yaw"));
        assertEquals(-10.0, result.get("pitch"));
    }

    @Test
    void serializeLocation_nullWorld_omitsWorldKey() {
        Location loc = new Location(null, 0, 0, 0);
        Map<String, Object> result = LocationUtil.serializeLocation(loc);
        assertFalse(result.containsKey("world"));
    }

    @Test
    void serializeLocation_nullLocation_throws() {
        assertThrows(IllegalArgumentException.class, () -> LocationUtil.serializeLocation(null));
    }

    // --- distance ---

    @Test
    void distance_sameWorld_calculatesCorrectly() {
        Location loc1 = new Location(world, 0, 0, 0);
        Location loc2 = new Location(world, 3, 4, 0);
        assertEquals(5.0, LocationUtil.distance(loc1, loc2), 0.001);
    }

    @Test
    void distance_differentWorlds_returnsNegativeOne() {
        Location loc1 = new Location(world, 0, 0, 0);
        Location loc2 = new Location(otherWorld, 0, 0, 0);
        assertEquals(-1, LocationUtil.distance(loc1, loc2));
    }

    @Test
    void distance_nullWorld_returnsNegativeOne() {
        Location loc1 = new Location(null, 0, 0, 0);
        Location loc2 = new Location(world, 0, 0, 0);
        assertEquals(-1, LocationUtil.distance(loc1, loc2));
    }

    @Test
    void distance_sameLocation_returnsZero() {
        Location loc = new Location(world, 5, 10, 15);
        assertEquals(0, LocationUtil.distance(loc, loc));
    }

    @Test
    void distance_nullLocation_throws() {
        Location loc = new Location(world, 0, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> LocationUtil.distance(null, loc));
        assertThrows(IllegalArgumentException.class, () -> LocationUtil.distance(loc, null));
    }

    // --- isWithinRadius ---

    @Test
    void isWithinRadius_withinRange_returnsTrue() {
        Location center = new Location(world, 0, 0, 0);
        Location target = new Location(world, 3, 4, 0);
        assertTrue(LocationUtil.isWithinRadius(center, target, 10));
    }

    @Test
    void isWithinRadius_exactlyOnBoundary_returnsTrue() {
        Location center = new Location(world, 0, 0, 0);
        Location target = new Location(world, 3, 4, 0);
        assertTrue(LocationUtil.isWithinRadius(center, target, 5));
    }

    @Test
    void isWithinRadius_outsideRange_returnsFalse() {
        Location center = new Location(world, 0, 0, 0);
        Location target = new Location(world, 10, 10, 10);
        assertFalse(LocationUtil.isWithinRadius(center, target, 5));
    }

    @Test
    void isWithinRadius_differentWorlds_returnsFalse() {
        Location center = new Location(world, 0, 0, 0);
        Location target = new Location(otherWorld, 0, 0, 0);
        assertFalse(LocationUtil.isWithinRadius(center, target, 100));
    }

    @Test
    void isWithinRadius_negativeRadius_throws() {
        Location loc = new Location(world, 0, 0, 0);
        assertThrows(IllegalArgumentException.class,
                () -> LocationUtil.isWithinRadius(loc, loc, -1));
    }

    @Test
    void isWithinRadius_nullLocations_throws() {
        Location loc = new Location(world, 0, 0, 0);
        assertThrows(IllegalArgumentException.class,
                () -> LocationUtil.isWithinRadius(null, loc, 10));
        assertThrows(IllegalArgumentException.class,
                () -> LocationUtil.isWithinRadius(loc, null, 10));
    }

    // --- formatLocation ---

    @Test
    void formatLocation_validLocation_formatsCorrectly() {
        Location loc = new Location(world, 10.5, 64.0, -20.3);
        assertEquals("world (10.5, 64.0, -20.3)", LocationUtil.formatLocation(loc));
    }

    @Test
    void formatLocation_nullWorld_showsUnknown() {
        Location loc = new Location(null, 0, 0, 0);
        assertEquals("unknown (0.0, 0.0, 0.0)", LocationUtil.formatLocation(loc));
    }

    @Test
    void formatLocation_nullLocation_returnsUnknown() {
        assertEquals("unknown", LocationUtil.formatLocation(null));
    }
}

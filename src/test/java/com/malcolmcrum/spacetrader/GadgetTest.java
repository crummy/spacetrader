package com.malcolmcrum.spacetrader;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Malcolm on 9/8/2015.
 */
public class GadgetTest {

    @Test
    public void testGetAdjustedRandomGadget() throws Exception {
        for (int i = 0; i < 10; ++i) {
            Gadget g = Gadget.GetAdjustedRandomGadget();
            assertTrue(g != Gadget.FuelCompactor);
        }
    }
}
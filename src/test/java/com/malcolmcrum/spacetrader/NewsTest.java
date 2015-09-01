package com.malcolmcrum.spacetrader;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Malcolm on 9/1/2015.
 */
public class NewsTest {

    @Test
    public void testGetNewspaper() {
        News news = new News(); // ahah what a silly line
        Ship ship = new Ship(ShipType.Beetle);
        Captain captain = new Captain("Bob");
        news.addNotableEvent(News.NotableEvent.ArrivalViaSingularity);
        news.addSpecialEvent(SolarSystem.SpecialEvent.MedicineDelivery);
        List<String> paper = news.getNewspaper(SolarSystem.Acamar, captain, Difficulty.Normal, ship);

        boolean paperContainsSingularityHeadline = false;
        boolean paperContainsMedicineHeadline = false;
        for (String headline : paper) {
            if (headline.equals("Disease Antidotes Arrive! Health Officials Optimistic.")) {
                paperContainsMedicineHeadline = true;
            } else if (headline.equals("Travelers Claim Sighting of Ship Materializing in Orbit!")) {
                paperContainsSingularityHeadline = true;
            }
        }
        assertTrue("medicine headline should exist", paperContainsMedicineHeadline);
        assertTrue("singularity headline should exist", paperContainsSingularityHeadline);

        news.resetNewsEvents();
        paper = news.getNewspaper(SolarSystem.Acamar, captain, Difficulty.Normal, ship);

        paperContainsSingularityHeadline = false;
        paperContainsMedicineHeadline = false;
        for (String headline : paper) {
            if (headline.equals("Disease Antidotes Arrive! Health Officials Optimistic.")) {
                paperContainsMedicineHeadline = true;
            } else if (headline.equals("Travelers Claim Sighting of Ship Materializing in Orbit!")) {
                paperContainsSingularityHeadline = true;
            }
        }
        assertFalse("medicine headline should not exist", paperContainsMedicineHeadline);
        assertFalse("singularity headline should not exist", paperContainsSingularityHeadline);
    }
}
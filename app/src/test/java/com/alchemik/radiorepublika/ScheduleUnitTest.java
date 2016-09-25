package com.alchemik.radiorepublika;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;

/**
 * Created by Alvenir on 21.08.2016.
 */
public class ScheduleUnitTest {
    private static final String REPUBLIKA_URL = "http://arch.telewizjarepublika.pl/program-tv-na-niedziele.html";
    public static Document document;

    @BeforeClass
    public static void setup() throws IOException {
        document = Jsoup.connect(REPUBLIKA_URL).get();
    }

    @Test
    public void isProgramBoxNotEmpty() throws IOException {
        Elements table = document.select("#program-box");

        assertFalse("Program box doesn't contain any data.", table.isEmpty());
    }
}

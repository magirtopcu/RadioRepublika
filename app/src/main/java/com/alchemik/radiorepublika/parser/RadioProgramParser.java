package com.alchemik.radiorepublika.parser;

import android.util.Log;

import com.alchemik.radiorepublika.model.Track;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RadioProgramParser {

    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int HOUR = 3600000;

    private static final String REPUBLIKA_URL = "http://telewizjarepublika.pl/program-tv.html";
    private static final String PREMIERA = "PREMIERA";
    private static final String NA_ZYWO = "NA ŻYWO";
    private static final String TAG = RadioProgramParser.class.getSimpleName();



    public static List<Track> parse() throws IOException, ParseException {
        List<Track> trackList = new ArrayList<>();
        Document doc = Jsoup.connect(REPUBLIKA_URL).get();

        for (Element table : doc.select("#program-box")) {
            for (Element row : table.select("tr")) {
                Track track = new Track();
                for (int column = 0; column < 2; column++) {
                    Element cell = row.select("td").get(column);
                    if (column == FIRST) {
                        for (Element elem : cell.select("span")) {
                            String elemString = elem.text();
                            if (elemString.equals(PREMIERA)) {
                                track.setIsPremiere(true);
                            } else if (elemString.equals(NA_ZYWO)) {
                                track.setIsLive(true);
                            } else {
                                // parse to milis

                                Date date = new Date();
                                String timeInMillisSinceBeginning = new SimpleDateFormat("yyyy:MM:dd", new Locale("pl", "PL")).format(date);
                                //FIXME
                                Date timeInMillis = new SimpleDateFormat("yyyy:MM:dd HH:mm", new Locale("pl", "PL")).parse(timeInMillisSinceBeginning + " " + elemString);
                                track.setStartDateTime(timeInMillis.getTime());
                                Log.d(TAG, "timeInMillisSinceBeginning=" + timeInMillisSinceBeginning + ", time in millis = " + timeInMillis);
                                // set start time
                                track.setStartTime(elemString);
                            }
                        }
                    } else if (column == SECOND) {
                        for (int column_elem = 0; column_elem < cell.select("td > span").size(); column_elem++) {
                            Element elem = cell.select("td > span").get(column_elem);
                            if (column_elem == FIRST) {
                                track.setTrackTitle(elem.text());
                            } else {
                                if (!"".equals(elem.text()) && elem.text() != null) {
                                    track.setTrackSubtitle(elem.text());
                                }
                            }
                            if (elem.select("a").hasAttr("href")) {
                                String url = elem.select("a[href]").attr("abs:href");
                                track.setPicUrl(url);
                            }
                        }
                    }
                }
                trackList.add(track);
                Log.d(TAG, "Parsed record= " + track.toString());
            }
        }

        // Update each track's duration time
        for (int i = 0; i < trackList.size() ; i++) {
            Track currentTrack = trackList.get(i);
            if (i < trackList.size() - 1) {
                currentTrack.setEndDateTime(trackList.get(i + 1).getStartDateTime());
                currentTrack.setDurationTime(trackList.get(i + 1).getDurationTime() - currentTrack.getStartDateTime());
            } else {
                currentTrack.setDurationTime(HOUR); // 1 hour
            }
        }

        return trackList;
    }
}

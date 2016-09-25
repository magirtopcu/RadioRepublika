package com.alchemik.radiorepublika.parser;

import android.util.Log;

import com.alchemik.radiorepublika.model.Track;
import com.alchemik.radiorepublika.util.Constans;

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

public class ScheduleParser {
    private static final String TAG = ScheduleParser.class.getSimpleName();

    private static final int FIRST_COL = 0;
    private static final int SECOND_COL = 1;
    private static final int HOUR = 3600000;

    private static final String PREMIERA = "PREMIERA";
    private static final String NA_ZYWO = "NA ŻYWO";

    // TODO: refactor, in case of e.g. network error, show dummy schedule data
    public static List<Track> parse() throws IOException {
        List<Track> trackList = new ArrayList<>();
        Document doc = Jsoup.connect(Constans.REPUBLIKA_URL).get();
        Log.d(TAG, "parse: " + doc);

        for (Element table : doc.select("#program-box")) {
            for (Element row : table.select("tr")) {

                Track track = new Track();

                for (int column = 0; column < 2; column++) {
                    Element cell = row.select("td").get(column);

                    if (column == FIRST_COL) {
                        for (Element elem : cell.select("span")) {
                            String elemString = elem.text();

                            if (elemString == null) continue;
                            if (elemString.length() == 0) continue;

                            // check first if record contains label
                            if (elemString.equals(PREMIERA)) {
                                track.setIsPremiere(true);
                            } else if (elemString.equals(NA_ZYWO)) {
                                track.setIsLive(true);
                            } else {
                                track.setStartTime(elemString);
                                track.setStartDateTime(setStartDateTime(elemString).getTime());
                                // set start time
                            }
                        }
                    } else if (column == SECOND_COL) {
                        for (int column_elem = 0; column_elem < cell.select("td > span").size(); column_elem++) {
                            Element elem = cell.select("td > span").get(column_elem);

                            if (elem == null) continue;

                            if (column_elem == FIRST_COL) {
                                track.setTrackTitle(elem.text());
                            //} else if (!"".equals(elem.text()) && elem.text() != null) {
                            } else if (!elem.text().isEmpty() && elem.text() != null) {
                                track.setTrackSubtitle(elem.text());
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

        updateDuration(trackList);

        return trackList;
    }

    public static Date setStartDateTime(String time) {
        // parse time to millis
        // FIXME: wrong schedule after 12pm
        Date date = new Date();
        String timeInMillisSinceBeginning = new SimpleDateFormat("yyyy:MM:dd", new Locale("pl", "PL")).format(date);
        Date timeInMillis = null;
        try {
            timeInMillis = new SimpleDateFormat("yyyy:MM:dd HH:mm", new Locale("pl", "PL")).parse(timeInMillisSinceBeginning + " " + time);
        } catch (ParseException e) {
            timeInMillis = date;
            e.printStackTrace();
        }
        Log.d(TAG, "timeInMillisSinceBeginning=" + timeInMillisSinceBeginning + ", time in millis = " + timeInMillis);
        return timeInMillis;
    }

    /**
     *  Update each track's duration time
     */
    private static void updateDuration(List<Track> trackList) {
        //FIXME: don't iterate again over list
        for (int i = 0; i < trackList.size(); i++) {
            Track currentTrack = trackList.get(i);
            if (i < trackList.size() - 1) {
                currentTrack.setEndDateTime(trackList.get(i + 1).getStartDateTime());
                currentTrack.setDurationTime(trackList.get(i + 1).getDurationTime() - currentTrack.getStartDateTime());
            } else {
                currentTrack.setDurationTime(HOUR);
            }
        }
    }
}

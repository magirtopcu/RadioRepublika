package com.alchemik.radiorepublika.mock;

import com.alchemik.radiorepublika.model.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by lesze on 3/20/2016.
 */
public class Playlist {
    private static final int TWO_MINUTES = 120000;
    private static final int ONE_MINUTE = 60000;

    public static List<Track> generate() {
        return generate(ONE_MINUTE);
    }

    private static List<Track> generate(final int interval) {
        List<Track> mTrackList = new ArrayList<>();

        long startTimeMillis = System.currentTimeMillis();
        long endTimeMillis = startTimeMillis + interval;
        String timeString;
        for (int i = 0; i < 50; i++) {
            timeString = new SimpleDateFormat("kk:mm", Locale.getDefault()).format(startTimeMillis);
            mTrackList.add(new Track("Starcie cywilizacji", timeString, startTimeMillis, endTimeMillis));

            startTimeMillis = endTimeMillis;
            endTimeMillis += interval;
        }
        return mTrackList;
    }
}

package me.sleepyfish.CTPT.ui.language;

/**
 * @author SleepyFish
 *
 * @version Version 1.0
 * @since Version 1.0
 * @implNote This enum is used to store all supported languages
 */
public enum Language {

    English   ("English"),   // this is useless everyone knows english
    German    ("German"),    // german also useless there won't be anyone german using korePI
    Portugues ("Portugues"), // the only good one, where people don't speak like they cant
    Chinese   ("Chinese"),   // this might be useful, not for me tough
    Japanese  ("Japanese");  // this also might be useful, but also not for me :3

    public String name;

    Language(final String n) {
        this.name = n;
    }

}
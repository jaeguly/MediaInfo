package net.sourceforge.mediainfo;


/**
 * Give information about a lot of media format.
 */
public class MediaInfo {

    // @remark Don't change it carelessly.
    // This order is from MediaInfo_Const.h
    public enum StreamKind {
        GENERAL,
        VIDEO,
        AUDIO,
        TEXT,
        CHAPTERS,
        IMAGE,
        MENU
    }
    
    // @remark Don't change it carelessly.
    // This order is from MediaInfo_Const.h
    public enum InfoKind {
        NAME,           // Unique name of parameter
        TEXT,           // Value of parameter
        MEASURE,        // Unique name of measure unit of parameter
        OPTIONS,        // See InfoOptionKind
        NAME_TEXT,      // Translated name of parameter
        MEASURE_TEXT,   // Translated name of measure unit
        INFO,           // More information about the parameter
        HOWTO,          // How this parameter is supported, could be N(No), B(Beta), R(Read only),
                        // W (Read/Write)
        DOMAIN          // Domain of this piece of information
    }

    public MediaInfo() {
        peer = create();
    }

    public void destroy() {
        if (peer != 0) {
            destroy(peer);
            peer = 0;
        }
    }

    /**
     * Open a file and collect information about it (technical information and tags)
     * @param finename Full name of file to open
     * @return 0 file not opened
     *         1 file opened
     */
    public int open(String filename) {
        return open(peer, filename);
    }

    /** Close a file opened before with open(). */
    public void close() {
        close(peer);
    }

    /**
     * Get a piece of information about a file. (parameter is an integer)
     * @param streamKind Kind of Stream
     * @param streamNum Stream number in Kind of stream
     * @param parameter Parameter you are looking for in the stream (codec, width, bitrate, ..),
     *                  in integer format
     * @param infoKind Kind of information you want about the parameter (the text, the measure,
     *                 the help, ..)
     * @return a string about information you search
     *         an empty string if there is a problem
     */
    public String get(StreamKind streamKind, int streamNum, int parameter) {
        return getById(peer, streamKind.ordinal(), streamNum, parameter); /* InfoKind.TEXT */
    }

    /**
     * Get a piece of information about a file. (parameter is an string)
     * @param streamKind Kind of Stream (general, video, audio, ..)
     * @param streamNum Stream number in Kind of stream
     * @param parameter Parameter you are looking for in the stream (codec, width, bitrate, ..),
     *                  in string format ("Codec", "Width", ..)
     * @param infoKind Kind of information you want about the parameter (the text, the measure,
     *                 the help, ..)
     * @param searchKind Where to look for the parameter
     * @return a string about information you search
     *         an empty string if there is a problem
     * @see option("Info_Parameters") to have the full list for @p parameter
     */
    public String get(StreamKind streamKind, int streamNum, String parameter) {
        return getByName(peer, streamKind.ordinal(), streamNum, parameter); /*InfoKind.TEXT, InfoKind.NAME*/
    }

    public String get(StreamKind streamKind, int streamNum, String parameter, InfoKind infoKind) {
        return getByNameDetail(peer, streamKind.ordinal(), streamNum, parameter, infoKind.ordinal(), InfoKind.NAME.ordinal());
    }

    public String get(StreamKind streamKind, int streamNum, String parameter, InfoKind infoKind,
            InfoKind searchKind) {
        return getByNameDetail(peer, streamKind.ordinal(), streamNum, parameter, infoKind.ordinal(), searchKind.ordinal());
    }

    public String inform() {
        return informDetail(peer);
    }

    /**
     * Configuration or get information about MediaInfoLib
     *
     * <ul>Known options are:
     * <li>(NOT IMPLEMENTED YET) "BlockMethod": Configure when Open Method must return (default or not command not understood: "1")
     *      <ul>
     *      <li>"0": Immediatly
     *      <li>"1": After geting local information
     *      <li>"2": When user interaction is needed, or whan Internet information is get
     *      </ul>
     * <li>"Complete": For debug, configure if MediaInfoLib::Inform() show all information (doesn't care of InfoOption_NoShow tag): shows all information if true, shows only useful for user information if false (No by default)
     * <li>"Complete_Get": return the state of "Complete"
     * <li>"Language": Configure language (default language, and this object); Value is Description of language (format: "Column1;Colum2...)
     *      <ul>
     *      <li>Column 1: Unique name ("Bytes", "Title")
     *      <li>Column 2: translation ("Octets", "Titre")
     *      </ul>
     * <li>"Language_Get": Get the language file in memory
     * <li>"Language_Update": Configure language of this object only (for optimisation); Value is Description of language (format: "Column1;Colum2...)
     *      <ul>
     *      <li>Column 1: Unique name ("Bytes", "Title")
     *      <li>Column 2: translation ("Octets", "Titre")
     *      </ul>
     * <li>"Inform": Configure custom text, See MediaInfoLib::Inform() function; Description of views (format: "Column1;Colum2...)
     *      <ul>
     *      <li>Column 1: code (11 lines: "General", "Video", "Audio", "Text", "Other", "Begin", "End", "Page_Begin", "Page_Middle", "Page_End")
     *      <li>Column 2: The text to show (exemple: "Audio: %FileName% is at %BitRate/String%")
     *      </ul>
     * <li>"ParseUnknownExtensions": Configure if MediaInfo parse files with unknown extension
     * <li>"ParseUnknownExtensions_Get": Get if MediaInfo parse files with unknown extension
     * <li>"ShowFiles": Configure if MediaInfo keep in memory files with specific kind of streams (or no streams); Value is Description of components (format: "Column1;Colum2...)
     *      <ul>
     *      <li>Column 1: code (available: "Nothing" for unknown format, "VideoAudio" for at least 1 video and 1 audio, "VideoOnly" for video streams only, "AudioOnly", "TextOnly")
     *      <li>Column 2: "" (nothing) not keeping, other for keeping
     *      </ul>
     * <li>(NOT IMPLEMENTED YET) "TagSeparator": Configure the separator if there are multiple same tags (" | " by default)
     * <li>(NOT IMPLEMENTED YET) "TagSeparator_Get": return the state of "TagSeparator"
     * <li>(NOT IMPLEMENTED YET) "Internet": Authorize Internet connection (Yes by default)
     * <li>(NOT IMPLEMENTED YET) "Internet_Title_Get": When State=5000, give all possible titles for this file (one per line)
     *      <ul>
     *      <li>Form: Author TagSeparator Title TagSeparator Year...
     *      </ul>
     * <li>(NOT IMPLEMENTED YET) "Internet_Title_Set": Set the Good title (same as given by Internet_Title_Get)
     *      <ul>
     *      <li>Form: Author TagSeparator Title TagSeparator Year
     *      </ul>
     * <li>"Info_Parameters": Information about what are known unique names for parameters
     * <li>"Info_Parameters_CSV": Information about what are known unique names for parameters, in CSV format
     * </ul>
     *
     * @param name The name of option
     * @param value The value of option
     * @return Depend of the option: "" by default means No, other means Yes
     */
    public String option(String name, String value) {
        return option(peer, name, value);
    }

//    /** Count of streams of a stream kind. */
//    public int count(StreamKind streamKind) {
//        return count(peer, streamKind.ordinal(), -1);
//    }

//    /** Count of piece of information in this stream. */
//    public int count(StreamKind streamKind, int streamNum) {
//        return count(peer, streamKind.ordinal(), streamNum);
//    }


    //
    // private, protected, static
    //
    
    private long peer;

    private native long create();

    private native void destroy(long peer);

    private native int open(long peer, String filename);

    private native void close(long peer);

    private native String getById(long peer, int streamKind, int streamNum, int parameter);

    private native String getByIdDetail(long peer, int streamKind, int streamNum, int parameter,
            int kindOfInfo);

    private native String getByName(long peer, int streamKind, int streamNum, String parameter);

    private native String getByNameDetail(long peer, int streamKind, int streamNum, String parameter,
            int kindOfInfo, int kindOfSearch);

    private native String informDetail(long peer);

    private native String option(long peer, String option, String value);

//    private native int count(long peer, int streamKind, int streamNum);


    protected void finalize() {
        destroy();
    }

    static {
        try {
            System.loadLibrary("mediainfo");
        } catch (Exception e) {
            System.out.println("can't load mediainfo libraries!");
        }
    }
}


package your.microservice.core.system.messaging.bulletin.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import your.microservice.core.dm.dto.system.YourBulletin;
import your.microservice.core.dm.serialization.JsonDateSerializer;
import your.microservice.core.system.messaging.bulletin.dropzone.BulletinZoneWatcherProcessingServiceImpl;
import your.microservice.core.util.TimeStamp;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

/**
 * YourMSBulletinUtility
 * <p>
 * Provides a simple helper main class,
 * to allow creation of a YourBulletin
 * POJO Object serialized to the specified Bulletin Drop Zone.
 *
 * @author jeff.a.schenk@gmail.com on 8/29/15.
 */
public class YourMSBulletinUtility {
    /**
     * File Name Constants
     */
    protected static final String bulletin_filename_prefix =
            "your-microservice-bulletin";
    protected static final String bulletin_filetype_suffix =
            ".json";
    /**
     * Logging
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(YourMSBulletinUtility.class);
    /**
     * Constants
     */
    private static final String MESSAGE = "message";
    private static final String TIME = "time";
    private static final String LEVEL = "level";
    private static final String TYPE = "type";
    private static final String STATUS = "status";
    private static final String URL = "url";
    private static final String HELP = "help";
    private static final String INTERACTIVE = "i";
    private static final String NOW = "now";
    /**
     * Object Mapper
     */
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Utility Main Class,
     * to post a Your Microservice Bulletin Creation from Command Line Prompts.
     *
     * @param arguments Utility Arguments
     */
    public static void main(String[] arguments) {
        /**
         * Construct our Options
         */
        Options options = constructOptions();
        /**
         * Get our Drop Zone File Directory Name.
         * If no Properties file specified, we
         * will not be able to continue...
         */
        String dropZoneFileDirectoryName = getDropZoneFileDirectoryNameFromJavaProperty();
        if (dropZoneFileDirectoryName != null &&
                !BulletinDropZoneFileUtility.isZoneDirectoryValid(new File(dropZoneFileDirectoryName))) {
            LOGGER.error("Specified Bulletin Drop Zone Directory:[" + dropZoneFileDirectoryName + "], is not valid!");
            showUsage(options);
            return;
        }
        /**
         * Check for necessary Arguments, to show usage or not.
         */
        if (arguments == null || arguments.length == 0) {
            showUsage(options);
            return;
        }
        /**
         * Create our Parser and Parse all Arguments
         */
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, arguments);
        } catch (ParseException pe) {
            LOGGER.error("Parsing Exception occurred: " + pe.getMessage());
            return;
        }
        /**
         * Has Help or Usage been specified?
         */
        if (cmd.hasOption(HELP)) {
            showUsage(options);
            return;
        }
        /**
         * Has Interactive been specified?
         */
        YourBulletin yourBulletin;
        if (cmd.hasOption(INTERACTIVE)) {
            yourBulletin = promptForOptions();
        } else {
            /**
             * Validate
             */
            yourBulletin = validateOptions(cmd);
        }
        /**
         * Produce a Serialized POJO Bulletin File, if applicable.
         *
         * Just a note,
         * by the time we have written the Bulletin, the Cloud instance may have
         * already consumed and removed the file we just wrote, depending upon
         * speed and polling Interval of the System Instance Status.
         */
        if (yourBulletin != null && serializeYourMicroserviceBulletin(dropZoneFileDirectoryName, yourBulletin)) {
            LOGGER.info("Successfully Created Your Microservice Bulletin File.");
        } else {
            LOGGER.error("Your Microservice Bulletin File will not be Posted.");
        }
    }

    /**
     * getDropZoneFileDirectoryNameFromJavaProperty
     *
     * @return String either Null or the Drop Zone File Directory Name.
     */
    private static String getDropZoneFileDirectoryNameFromJavaProperty() {
        /**
         * Initialize and
         * Obtain our Environment Runtime Properties.
         */
        String dropZoneFileDirectoryName =
                System.getProperty(
                        BulletinZoneWatcherProcessingServiceImpl.BULLETIN_DROPZONE_DIRECTORY_PROPERTY_NAME);
        /**
         * Return
         */
        return dropZoneFileDirectoryName;
    }

    /**
     * Construct our Command Line Argument Options
     *
     * @return Options required for this Utility.
     */
    private static Options constructOptions() {
        Options options = new Options();
        Option message = new Option(MESSAGE, true, "Message Text of Bulletin");
        message.setArgName("Message Text");
        options.addOption(message);

        Option time = new Option(TIME, true, "Date When Described Event Takes Place, in format: " +
                "YYYY/MM/DYTHH:MM:SS (Assumes UTC)");
        time.setArgName("Event Time");
        options.addOption(time);

        Option level = new Option(LEVEL, true, "Message Level of Bulletin, optional, Defaults to 'WARN'");
        level.setArgName("Message Level");
        options.addOption(level);

        Option type = new Option(TYPE, true, "Message Text of Bulletin, optional, Defaults to 'CLOUD_BULLETIN'");
        type.setArgName("Message Type");
        options.addOption(type);

        Option status = new Option(STATUS, true, "Status of Cloud Instance, optional, Defaults to 'WAITING_TO_SHUTDOWN'");
        status.setArgName("Cloud Microservice Status");
        options.addOption(status);

        Option url = new Option(URL, true, "Additional Informational URL");
        url.setArgName("url");
        options.addOption(url);

        Option help = new Option(HELP, false, "Usage and Help");
        options.addOption(help);

        Option interactive = new Option(INTERACTIVE, false, "Interactive, will prompt for all Options");
        options.addOption(interactive);

        return options;
    }

    /**
     * Show Usage
     *
     * @param options Built Options for Utility.
     */
    private static void showUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(YourMSBulletinUtility.class.getSimpleName() +
                " -Deam.resource=<eam resource url>", options);
    }

    /**
     * Private Helper to Validate our Options.
     *
     * @param cmd Options to be Validated.
     * @return YourBulletin Object constructed from Options.
     */
    private static YourBulletin validateOptions(CommandLine cmd) {
        YourBulletin yourBulletin = new YourBulletin();
        /**
         * Message Level
         */
        if (cmd.hasOption(LEVEL)) {
            yourBulletin.setMessageLevel(cmd.getOptionValue(LEVEL));
        } else {
            yourBulletin.setMessageLevel(YourBulletin.MessageLevel.WARN.toString());
        }
        /**
         * Message Type
         */
        if (cmd.hasOption(TYPE)) {
            yourBulletin.setMessageType(cmd.getOptionValue(TYPE));
        } else {
            yourBulletin.setMessageType(YourBulletin.MessageType.CLOUD_BULLETIN.toString());
        }
        /**
         * Cloud Status
         */
        if (cmd.hasOption(STATUS)) {
            yourBulletin.setCloudStatus(cmd.getOptionValue(STATUS));
        } else {
            yourBulletin.setCloudStatus(YourBulletin.CloudStatusType.WAITING_TO_SHUTDOWN.toString());
        }
        /**
         * URL
         */
        if (cmd.hasOption(URL)) {
            yourBulletin.setMoreInfoUrl(cmd.getOptionValue(URL));
        }
        /**
         * Time
         */
        if (cmd.hasOption(TIME)) {
            Date date = parseDate(cmd.getOptionValue(TIME));
            if (date != null) {
                yourBulletin.setMessageFutureEventTime(date);
            }
        }
        /**
         * Message
         */
        if (cmd.hasOption(MESSAGE)) {
            yourBulletin.setMessage(cmd.getOptionValue(MESSAGE));
        } else {
            LOGGER.error("No Message Specified!");
            return null;
        }
        /**
         *  Return the Parsed Object.
         */
        return yourBulletin;
    }

    /**
     * Private Helper to Prompt for our Options Interactively.
     *
     * @return YourBulletin Object constructed from Options.
     */
    private static YourBulletin promptForOptions() {
        Scanner scanInput = new Scanner(System.in);
        YourBulletin yourBulletin = new YourBulletin();
        /**
         * Set Defaults
         */
        yourBulletin.setMessageLevel(YourBulletin.MessageLevel.WARN.toString());
        yourBulletin.setMessageType(YourBulletin.MessageType.CLOUD_BULLETIN.toString());
        yourBulletin.setCloudStatus(YourBulletin.CloudStatusType.WAITING_TO_SHUTDOWN.toString());
        /**
         * Message Level
         */
        String value = promptConsoleForEntry(scanInput, "Enter the Your Microservice Message Level: ", 16, yourBulletin.getMessageLevel());
        if (value != null && !value.isEmpty()) {
            yourBulletin.setMessageLevel(value.trim());
        }
        /**
         * Message Type
         */
        value = promptConsoleForEntry(scanInput, "Enter the Your Microservice Message Type: ", 64, yourBulletin.getMessageType());
        if (value != null && !value.isEmpty()) {
            yourBulletin.setMessageType(value.trim());
        }
        /**
         * Cloud Status
         */
        value = promptConsoleForEntry(scanInput, "Enter the Your Microservice Instance Status: ", 64, yourBulletin.getCloudStatus());
        if (value != null && !value.isEmpty()) {
            yourBulletin.setCloudStatus(value.trim());
        }
        /**
         * URL
         */
        value = promptConsoleForEntry(scanInput, "Enter the More Info URL: ", 128, null);
        if (value != null && !value.isEmpty()) {
            yourBulletin.setMoreInfoUrl(value.trim());
        }
        /**
         * Time
         */
        value = promptConsoleForEntry(scanInput, "Enter the Time of the Event: Format->YYYY/MM/DYTHH:MM:SS " +
                "or now or now+minutes", 19, null);
        if (value != null && !value.isEmpty()) {
            if (value.equalsIgnoreCase(NOW)) {
                Calendar calendar = Calendar.getInstance();
                yourBulletin.setMessageFutureEventTime(calendar.getTime());
            } else if (value.toLowerCase().startsWith(NOW + "+")) {
                Calendar calendar = Calendar.getInstance();
                try {
                    Integer minutes = Integer.parseInt(value.substring(NOW.length() + 1));
                    calendar.add(Calendar.MINUTE, minutes);
                    yourBulletin.setMessageFutureEventTime(calendar.getTime());
                } catch(Exception e) {
                    LOGGER.error("Unable to Parse Time Value!");
                    return null;
                }
            } else {
                Date date = parseDate(value);
                if (date != null) {
                    yourBulletin.setMessageFutureEventTime(date);
                }
            }
        }
        /**
         * Message
         */
        value = promptConsoleForEntry(scanInput, "Enter the Your Microservice Message Bulletin Text: ", 512, null);
        if (value == null || value.isEmpty()) {
            LOGGER.error("No Message Supplied, unable to Post Bulletin.");
            return null;
        } else {
            yourBulletin.setMessage(value.trim());
        }
        /**
         * Ok, we have obtained all of out Data,
         * now let us show the Console Operator the data
         * to accept or cancel...
         */
        if (!promptConsoleForAcceptingPost(scanInput, yourBulletin)) {
            LOGGER.warn("Bulletin Post has been Cancelled, Post will be Abandoned!");
            yourBulletin = null;
        } else {
            LOGGER.info("Bulletin Post will be Accepted.");
        }
        /**
         * Close the Scanner, as we no longer need to prompt for Input.
         */
        scanInput.close();
        /**
         *  Return the Parsed Object.
         */
        return yourBulletin;
    }

    /**
     * serializeYourMicroserviceBulletin
     *
     * @param dropZoneFileDirectoryName Directory where File will be persisted.
     * @param yourBulletin           Bulletin Object to be Serialized.
     * @return boolean indicator if file was written successfully or not.
     */
    protected static boolean serializeYourMicroserviceBulletin(String dropZoneFileDirectoryName, YourBulletin yourBulletin) {
        /**
         * Formulate the File Name of our Bulletin.
         */
        String bulletin_filename =
                bulletin_filename_prefix + "_" + TimeStamp.getTimeStamp() + bulletin_filetype_suffix;
        File bulletin_file = new File(dropZoneFileDirectoryName + File.separator + bulletin_filename);
        try {
            mapper.writeValue(bulletin_file, yourBulletin);
            return true;
        } catch (IOException ioe) {
            LOGGER.error("Error Persisting Your Microservice Bulletin File:[" + bulletin_file.getAbsolutePath() +
                    "], " + ioe.getMessage());
            return false;
        }
    }

    /**
     * Helper to Parse a String Date Time Stamp to a Date Object.
     *
     * @param dateString String Date.
     * @return Date
     */
    protected static Date parseDate(String dateString) {
        DateFormat formatter = new SimpleDateFormat(YourBulletin.BULLETIN_DATE_FORMAT);
        formatter.setTimeZone(JsonDateSerializer.utcTZ);
        try {
            return formatter.parse(dateString.replace('T', ' '));
        } catch (java.text.ParseException pe) {
            LOGGER.error("Unable to Properly Parse the Date String:[" + dateString + "]");
            return null;
        }
    }

    /**
     * Helper to Parse a Date to a Date String.
     *
     * @param date Object Date.
     * @return String representing Date.
     */
    protected static String parseDate(Date date) {
        DateFormat formatter = new SimpleDateFormat(YourBulletin.BULLETIN_DATE_FORMAT);
        formatter.setTimeZone(JsonDateSerializer.utcTZ);
        return formatter.format(date);
    }

    /**
     * Private Helper method to prompt for Data Entry in Interactive Mode.
     *
     * @param scanInput    Reference to established Scanner.
     * @param prompt       Textual Prompt at Console.
     * @param maxLength    Max Length of String Allowed for this Data Element, if 0 or less, unlimited.
     * @param defaultValue Default Data Value, can be null.
     * @return String of Entered or Defaulted Data.
     */
    private static String promptConsoleForEntry(Scanner scanInput, String prompt, int maxLength, String defaultValue) {
        while (true) {
            /**
             * Data Prompt
             */
            System.out.print(prompt + ": " + (defaultValue == null ? "" : "default value->[" + defaultValue + "] : "));
            String data = scanInput.nextLine();
            /**
             * Validate the Data Entered.
             */
            if (data == null || data.isEmpty()) {
                LOGGER.debug("Using Default Value:[" + defaultValue + "]");
                return defaultValue;
            }
            if (maxLength > 0 && data.length() > maxLength) {
                System.out.println("** Error: Data Value Exceeds Max Length of " + maxLength + ", reenter...");
            } else {
                LOGGER.debug("Console Operator Input Entered:[" + data + "]");
                return data;
            }
        }
    }

    /**
     * Private Helper method to prompt for Acceptance of Data Entry in Interactive Mode.
     *
     * @param scanInput       Reference to established Scanner.
     * @param yourBulletin The JSON Object to be Posted
     * @return boolean Indicator if Post should be Accepted or Not.
     */
    private static boolean promptConsoleForAcceptingPost(Scanner scanInput, YourBulletin yourBulletin) {
        /**
         * Data Prompt
         */
        System.out.print("The following Your Microservice Bulletin JSON will be Posted to " +
                "this Local Running System Instance's Configured Bulletin Drop Zone: ");
        /**
         * Show the JSON to be Posted
         */
        try {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(yourBulletin));
        } catch (JsonProcessingException jpe) {
            LOGGER.error("Error while attempting to render JSON:[" + jpe.getMessage() + "]");
            return false;
        }
        /**
         * Prompt to Accept Post or not?
         */
        String data;
        while (true) {
            System.out.print("Do you wish to continue with Posting this System Instance Bulletin: (y|n): ");
            data = scanInput.nextLine();
            /**
             * Validate the Data Entered.
             */
            if (data != null && !data.isEmpty()) {
                break;
            }
        }
        /**
         * Return Acceptance or not based upon input...
         */
        return data.equalsIgnoreCase("y") || data.equalsIgnoreCase("yes");
    }
}

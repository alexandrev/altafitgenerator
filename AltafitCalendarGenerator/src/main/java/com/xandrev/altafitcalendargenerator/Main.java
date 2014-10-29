/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.altafitcalendargenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author alexa_000
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        CalendarPrinter printer = new CalendarPrinter();
        XLSExtractor extractor = new XLSExtractor();
        if (args != null && args.length > 0) {

            try {
                Options opt = new Options();
                opt.addOption("f", true, "Filepath of the XLS file");
                opt.addOption("t", true, "Type name of activities");
                opt.addOption("m", true, "Month index");
                opt.addOption("o", true, "Output filename of the generated ICS");
                BasicParser parser = new BasicParser();
                CommandLine cliParser = parser.parse(opt, args);
                if (cliParser.hasOption("f")) {
                    String fileName = cliParser.getOptionValue("f");
                    LOG.debug("File name to be imported: " + fileName);

                    String activityNames = cliParser.getOptionValue("t");
                    LOG.debug("Activity type names: " + activityNames);

                    ArrayList<String> nameList = new ArrayList<>();
                    String[] actNames = activityNames.split(",");
                    if (actNames != null) {
                        nameList.addAll(Arrays.asList(actNames));
                    }
                    LOG.debug("Sucessfully activities parsed: " + nameList.size());

                    if (cliParser.hasOption("m")) {
                        String monthIdx = cliParser.getOptionValue("m");
                        LOG.debug("Month index: " + monthIdx);
                        int month = Integer.parseInt(monthIdx) - 1;

                        if (cliParser.hasOption("o")) {
                            String outputfilePath = cliParser.getOptionValue("o");
                            LOG.debug("Output file to be generated: " + monthIdx);
                            
                            LOG.debug("Starting to extract the spreadsheet");
                            HashMap<Integer, ArrayList<TimeTrack>> result = extractor.importExcelSheet(fileName);
                            LOG.debug("Extracted the spreadsheet done");
                            
                            LOG.debug("Starting the filter of the data");
                            HashMap<Date, String> cal = printer.getCalendaryByItem(result, nameList, month);
                            LOG.debug("Finished the filter of the data");
                            
                            LOG.debug("Creating the ics Calendar");
                            net.fortuna.ical4j.model.Calendar calendar = printer.createICSCalendar(cal);
                            LOG.debug("Finished the ics Calendar");
                            
                            
                            LOG.debug("Printing the ICS file to: "+outputfilePath);
                            printer.saveCalendar(calendar, outputfilePath);
                            LOG.debug("Finished the ICS file to: "+outputfilePath);
                        }
                    }
                }
            } catch (ParseException ex) {
                LOG.error("Error parsing the argument list: ",ex);
            }
        }
    }

}

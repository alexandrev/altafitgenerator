/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xandrev.altafitcalendargenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

/**
 *
 * @author alexa_000
 */
public class CalendarPrinter {
    
     public HashMap<Date, String> getCalendaryByItem(HashMap<Integer, ArrayList<TimeTrack>> result, ArrayList<String> name, int month) {
        HashMap<Date, String> out = new HashMap<Date, String>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        if (result != null) {
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            if (month < currentMonth) {
                year++;
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.AM_PM, Calendar.AM);
        int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= maxDays; i++) {
            cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.AM_PM, Calendar.AM);
            cal.set(Calendar.DAY_OF_MONTH, i);
            cal.set(Calendar.AM_PM, Calendar.AM);
            int dWay = cal.get(Calendar.DAY_OF_WEEK);
            ArrayList<TimeTrack> timeTrack = result.get(dWay - 1);
            if (timeTrack != null) {
                for (TimeTrack tTime : timeTrack) {
                    String[] hourArray = tTime.getTime().split(":");
                    if (tTime.getName() != null && name.contains(tTime.getName())) {
                        cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.AM_PM, Calendar.AM);
                        cal.set(Calendar.DAY_OF_MONTH, i);
                        cal.set(Calendar.AM_PM, Calendar.AM);
                        cal.set(Calendar.HOUR, Integer.parseInt(hourArray[0]));
                        cal.set(Calendar.MINUTE, Integer.parseInt(hourArray[1]));
                        cal.set(Calendar.SECOND, 0);
                        out.put(cal.getTime(), tTime.getName());
                    }
                }
            }
        }
        return out;
    }

    public net.fortuna.ical4j.model.Calendar createICSCalendar(HashMap<Date, String> dateList) {
        net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
        cal.getProperties().add(Version.VERSION_2_0);
        cal.getProperties().add(CalScale.GREGORIAN);
        cal.getProperties().add(new ProdId("-//x4ndr3v//AltaFit XLSX Extractor 1.0//EN"));

        for (Date dDate : dateList.keySet()) {
            try {
                String eventName = dateList.get(dDate);
                VEvent tmpEvent = new VEvent(new net.fortuna.ical4j.model.DateTime(dDate.getTime()), new net.fortuna.ical4j.model.DateTime(dDate.getTime() + 45 * 60000), eventName);
                tmpEvent.getProperties().add(new Uid(UUID.randomUUID() + "@" + InetAddress.getLocalHost().getHostName()));
                VAlarm tmpAlarm = new VAlarm(new Dur(0,-1,-30,0));
                tmpAlarm.getProperties().add(Action.DISPLAY);
                tmpAlarm.getProperties().add(new Description(eventName));
                tmpEvent.getAlarms().add(tmpAlarm);
                cal.getComponents().add(tmpEvent);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return cal;
    }

    public void saveCalendar(net.fortuna.ical4j.model.Calendar calendar, String outputfilePath) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(outputfilePath);
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(calendar, fout);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ValidationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fout.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

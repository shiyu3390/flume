/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.interceptor;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatLogDateInterceptor implements Interceptor{
    private static final Logger logger = LoggerFactory.getLogger(FormatLogDateInterceptor.class);
    private SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    @Override
    public void initialize() {
        defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    @Override
    public Event intercept(Event event) {
        Map<String, String> headers = event.getHeaders();
        if (headers.get("logDate") == null) {
            logger.warn("没有获取到logDate");
            headers.put("logDate",defaultFormat.format(new Date()));
        }else {
            String logDate = headers.get("logDate").trim();
            Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{6}");
            Matcher matcher = pattern.matcher(logDate);
            if (matcher.find()) {
                logDate = logDate.substring(0,logDate.length()-3);
            }
            String dateFormat = headers.get("dateFormat").trim();
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            if (dateFormat.contains("MMM")) {
                sdf = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
            }
            try {
                Date date = sdf.parse(logDate);
                if (dateFormat.contains("y")){
                    headers.put("logDate",defaultFormat.format(date));
                }else{
                    defaultFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
                    headers.put("logDate",new SimpleDateFormat("yyyy").format(new Date()) + "-" + defaultFormat.format(date));
                }
            } catch (ParseException e) {
                logger.warn(logDate + "not matching " + dateFormat);
                headers.put("logDate",defaultFormat.format(new Date()));
            }
        }
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> newList = new ArrayList<>();
        for (Event e : events) {
            newList.add(intercept(e));
        }
        return newList;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {

        @Override
        public void configure(Context context) {

        }

        @Override
        public Interceptor build() {
            logger.info("Creating FormatLogDateInterceptor");
            return new FormatLogDateInterceptor();
        }
    }

    public static void main(String[] args) throws ParseException {
        String logDate = "2018-09-06 14:49:18.714761";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{6}");
        Matcher matcher = pattern.matcher(logDate);
        System.out.println(logDate);
        if (matcher.find()) {
            logDate = logDate.substring(0,logDate.length()-3);
            System.out.println(logDate);
        }
        Date date = sdf.parse(logDate);
        String test = "test" + "\\001" + "hello";
        String[] split = test.split("\\001");
        System.out.println(test);
//        String str = "Thu Mar 22 14:30:16.546953 2018";
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss.SSSSSS yyyy",Locale.ENGLISH);
//        try {
//            Date date = sdf.parse(str);
//            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
//        System.out.println(uuid);
//        System.out.println("2018-03-23 09:55:47.664 13135 DEBUG cinder.volume.drivers.ibm.storwize_svc.helpers [req-c5b8ae94-8474-4c99-9d61-0647fc6312fb - - - - -] Enter: create_copy: snapshot volume-851d8eae-db21-4f91-bc77-7e386787672c to volume-a49ffab4-9c29-4403-8789-cd0eb2801120. create_copy".matches(".*Enter: create_copy: snapshot volume-.*"));
//        String message = "Enter: create_copy: snapshot volume-851d8eae-db21-4f91-bc77-7e386787672c to volume-a49ffab4-9c29-4403-8789-cd0eb2801120";
//
//        String tt = message.substring(message.indexOf("volume-") + 7,message.indexOf("to volume") - 1);
//        System.out.println(tt);
    }
}

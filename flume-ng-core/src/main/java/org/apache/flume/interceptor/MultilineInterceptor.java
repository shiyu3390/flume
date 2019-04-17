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
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MultilineInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MultilineInterceptor.class);
    private static final Map<String, LinkedList<Event>> EVENT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, LinkedList<String>> NO_MATCH_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Long> INITIALIZE_TIME_MAP = new ConcurrentHashMap<>();
    private String regexStr;
    //    private static AtomicLong COUNT = new AtomicLong(0);
//    private static Long initCount = 0L;

    public MultilineInterceptor(String regex) {
        this.regexStr = regex;
    }

    @Override
    public void initialize() {
        logger.info("initialize the MultilineInterceptor");
    }

    @Override
    public Event intercept(Event event) {
        Map<String, String> headers = event.getHeaders();
        String key = headers.get("hostIp") == null ? "" : headers.get("hostIp") + "~" + headers.get("hostname") + "~" + headers.get("modulename") + "~" + headers.get("logFileName");
        if (headers.get("modulename") == null && headers.get("role") != null) {
            key = headers.get("hostIp") == null ? "" : headers.get("hostIp") + "~" + headers.get("hostname") + "~" + headers.get("role") + "~" + headers.get("logFileName");
        }
        String keyLower = key.toLowerCase();
        String regex = "[\\s\\S]*";
        if (regexStr != null) {
            regex = regexStr;
        } else if (keyLower.contains("nova") || keyLower.contains("neutron") || keyLower.contains("cinder") ||
                keyLower.contains("glance") || keyLower.contains("keystone") || keyLower.contains("ceph")) {
            regex = "[0-9]+[-][0-9]+[-][0-9]+[\\s]+[0-9]+[:][0-9]+[:][0-9]+[.][0-9]+[\\s\\S]*";
        } else if (keyLower.contains("rabbitmq")) {
            regex = "=[\\s\\S]*====[\\s\\S]*===[\\s\\S]*";
        } else if (keyLower.contains("messages") || keyLower.contains("haproxy") || keyLower.contains("keepalived")) {
            regex = "[a-zA-Z]*[\\s]+[0-9]+[\\s]+[0-9]+[:][0-9]+[:][0-9]+[\\s\\S]*";
        } else if (keyLower.contains("httpd_access") || keyLower.contains("mental_access")
                || keyLower.contains("portal_access")) {
            regex = "[0-9]+[.]+[0-9]+[.]+[0-9]+[.]+[0-9]+[\\s\\S]*";
        } else if (keyLower.contains("httpd_error")) {
            regex = "\\[[a-zA-Z]{3}[\\s]{1}[a-zA-Z]{3}[\\s]{1}[0-9]{1,2}[\\s]{1}[\\s\\S]{20}\\]+[\\s\\S]*";
        } else if (keyLower.contains("portal_error") || keyLower.contains("mental_error")) {
            regex = "[0-9]+[-][0-9]+[-][0-9]+[ ][0-9]+[:][0-9]+[:][0-9]+[,][0-9]+([\\s\\S]*)";
        } else if (keyLower.contains("libvirt")) {
            regex = "[0-9]+[-][0-9]+[-][0-9]+[\\s\\S]+[0-9]+[:][0-9]+[:][0-9]+[.][0-9]+[\\s\\S]*";
        }
        Long currTime = System.currentTimeMillis();
        if (INITIALIZE_TIME_MAP.get(key) == null && keyLower.contains("libvirt")) {
            INITIALIZE_TIME_MAP.put(key, currTime);
        }
        LinkedList<Event> events = EVENT_MAP.get(key);
        if (events != null && events.size() >= 2) {
            Event eventsFirst = events.removeFirst();
            byte[] body = eventsFirst.getBody();
            int length = body.length;
            if (length / 1024 / 1024 >= 1) {
//                logger.warn("大于1M的日志：" + new String(body));
                body = new String(body).substring(0, 2048).getBytes();
            }
            if (!new String(body).matches(regex)) {
                return null;
            }
            try {
                addEventToList(event, key, regex);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            eventsFirst.setBody(body);
            return eventsFirst;
        } else {
            //数据缓存时间设置为10秒钟
            if (events != null && (events.size() == 1 && keyLower.contains("libvirt") && (currTime - INITIALIZE_TIME_MAP.get(key)) >= 10000)) {
                Event eventsFirst = events.removeFirst();
                return eventsFirst;
            } else {
                try {
                    addEventToList(event, key, regex);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> newList = new ArrayList<>();
        for (Event e : events) {
            Event intercept = intercept(e);
            if (intercept != null) {
                newList.add(intercept);
            }
        }
        Long currTime = System.currentTimeMillis();
        for (String key : INITIALIZE_TIME_MAP.keySet()) {
            if ((currTime - INITIALIZE_TIME_MAP.get(key)) >= 10000) {
                if (EVENT_MAP.get(key) != null && EVENT_MAP.get(key).size() >= 1) {
                    newList.add(EVENT_MAP.get(key).removeFirst());
                }
            }
        }
        return newList;
    }

    @Override
    public void close() {
        logger.info("MultilineInterceptor closed");
    }

    /**
     * @param event
     * @author shiyu
     * @date 2017/08/01
     */
    public void addEventToList(Event event, String key, String regex) throws InterruptedException, UnsupportedEncodingException {
        byte[] eventBody = event.getBody();
        String lineStr = new String(eventBody);
        if (lineStr != null && lineStr != "" && lineStr.trim() != "") {
            if (EVENT_MAP.get(key) == null) {
                LinkedList<Event> eventList = new LinkedList<>();
                eventList.add(event);
                EVENT_MAP.put(key, eventList);
            } else {
                if (!"[\\s\\S]*".equals(regex)) {
                    dealEventList(lineStr, regex, event, EVENT_MAP.get(key), key);
                } else {
                    EVENT_MAP.get(key).add(event);
                }
                EVENT_MAP.put(key, EVENT_MAP.get(key));
            }
        }

    }

    private void dealEventList(String lineStr, String regex, Event event, LinkedList<Event> eventList, String key) {
        if (lineStr.matches(regex)) {
            if (!eventList.isEmpty()) {
                String tempStringTwo = new String(eventList.getLast().getBody());
                if (!tempStringTwo.matches(regex)) {
                    eventList.removeLast();
                    tempStringTwo = lineStr.trim() + " <br> " + tempStringTwo.trim();
                    event.setBody(tempStringTwo.getBytes());
                }
            }
            LinkedList<String> noMatchList = NO_MATCH_MAP.get(key);
            if (noMatchList != null) {
                noMatchList.clear();
            }
            eventList.add(event);
        } else {
            LinkedList<String> noMatchList = NO_MATCH_MAP.get(key);
            if (noMatchList == null || noMatchList.isEmpty()) {
                noMatchList = new LinkedList<>();
                noMatchList.add(lineStr.trim());
                NO_MATCH_MAP.put(key, noMatchList);
                if (!eventList.isEmpty()) {
                    String tempStringTwo = new String(eventList.getLast().getBody());
                    eventList.removeLast();
                    tempStringTwo = tempStringTwo.trim() + " <br> " + lineStr.trim();
                    event.setBody(tempStringTwo.getBytes());
                    eventList.add(event);
                } else {
                    eventList.add(event);
                }
            } else {
                String lastStr = noMatchList.removeLast();
                if (!lineStr.trim().equals(lastStr)) {
                    if (!eventList.isEmpty()) {
                        String tempStringTwo = new String(eventList.getLast().getBody());
                        eventList.removeLast();
                        tempStringTwo = tempStringTwo.trim() + " <br> " + lineStr.trim();
                        event.setBody(tempStringTwo.getBytes());
                        eventList.add(event);
                    } else {
                        eventList.add(event);
                    }
                }
                noMatchList.add(lineStr.trim());
            }
        }
    }

    public static class Builder implements Interceptor.Builder {
        private String regex;
        @Override
        public void configure(Context context) {
            regex = context.getString("regex");
        }

        @Override
        public Interceptor build() {
            logger.info("Creating MultilineInterceptor");
            return new MultilineInterceptor(regex);
        }
    }
}

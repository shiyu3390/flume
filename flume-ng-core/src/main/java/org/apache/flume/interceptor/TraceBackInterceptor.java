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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TraceBackInterceptor implements Interceptor{
    private static final Map<String, Map<String,Event>> EVENT_MAP = new ConcurrentHashMap<>();
    //1表示合并完成，待发送；0表示已经发送
    private static final Map<String, String> TRACE_FLAG = new ConcurrentHashMap<>();
    private static final Map<String,Long> INITIALIZE_TIME_MAP = new ConcurrentHashMap<>();
    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        Map<String, String> headers = event.getHeaders();
        String key = headers.get("hostIp") == null ? "" : headers.get("hostIp") + "~" + headers.get("hostname") + "~" + headers.get("module") + "~" + headers.get("logFileName");
        String modulename = headers.get("module");
        //"nova", "neutron", "cinder", "glance", "keystone"
        if (modulename.contains("nova") || modulename.contains("cinder") || modulename.contains("glance") ||
                modulename.contains("neutron") ||
                modulename.contains("keystone")) {
            String line = new String(event.getBody());
            String[] lineArr = line.split("\\s+");
            String date = lineArr[0] + " " + lineArr[1];
            String pid = lineArr[2];
            String level = lineArr[3];
            String pkg = lineArr[4];
            String traceKey = date + pid + level + pkg;
            //如果包含Traceback关键字,记录下来,然后将后续的同文件下相同时间戳和相同包的日志合并
            //直到收到其他日志
            if ("ERROR".equals(level)) {
                dealTraceBack(EVENT_MAP,key,pkg,traceKey,event);
                if (INITIALIZE_TIME_MAP.get(key) == null) {
                    INITIALIZE_TIME_MAP.put(key,System.currentTimeMillis());
                }
                return null;
            }else {
                if (TRACE_FLAG.get(key) == null && INITIALIZE_TIME_MAP.get(key) != null && System.currentTimeMillis() - INITIALIZE_TIME_MAP.get(key) >= 10000) {
                    TRACE_FLAG.put(key,"1");
                }
                return event;
            }
        }else {
            return event;
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
        for (String key:TRACE_FLAG.keySet()) {
            if ("1".equals(TRACE_FLAG.get(key)) && EVENT_MAP.get(key) != null) {
                for (String traceKey:EVENT_MAP.get(key).keySet()) {
                    newList.add(EVENT_MAP.get(key).get(traceKey));
                }
                TRACE_FLAG.clear();
                EVENT_MAP.get(key).clear();
                INITIALIZE_TIME_MAP.clear();
            }
        }
        return newList;
    }

    private void dealTraceBack(Map<String, Map<String,Event>> event_map,String key,String pkg,String traceKey,Event event) {
        if (event_map.get(key) == null) {
            Map<String,Event> map = new HashMap<>();
            map.put(traceKey,event);
            event_map.put(key,map);
        }else {
            if (event_map.get(key).get(traceKey) == null) {
                event_map.get(key).put(traceKey,event);
            }else {
                String pre = new String(event_map.get(key).get(traceKey).getBody());
                String curr = new String(event.getBody());
                curr = curr.substring(curr.indexOf(pkg) + pkg.length() + 1).trim();
                if (curr.startsWith("[instance") || curr.startsWith("[req")) {
                    curr = curr.substring(curr.indexOf("]") + 1).trim();
                }
                event.setBody((pre.trim() + " <br> " + curr).getBytes());
                event_map.get(key).put(traceKey,event);
            }
        }
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
            return new TraceBackInterceptor();
        }
    }
}

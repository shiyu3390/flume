/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MonitorKVToHDFSInterceptor implements Interceptor{
    private Logger logger = LoggerFactory.getLogger(MonitorKVToHDFSInterceptor.class);
    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        String body = new String(event.getBody());
        try {
            JSONObject json = JSON.parseObject(body);
            String metric = json.getString("metric");
            event.getHeaders().put("metric",metric);
        }catch(Exception e) {
            logger.warn(e.getMessage());
            logger.warn("不合规的数据:" + body);
        }
        if (event.getHeaders().get("metric") != null) {
            return event;
        }else {
            return null;
        }
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> newList = new ArrayList<>();
        for (Event e:events) {
            String[] content = new String(e.getBody()).split("\n");
            for (int i = 0;i < content.length; i++){
                Event event = EventBuilder.withBody(content[i].getBytes(),e.getHeaders());
                Event intercept = intercept(event);
                if (intercept != null){
                    newList.add(intercept);
                }
            }
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
            return new MonitorKVToHDFSInterceptor();
        }
    }

    public static void main(String[] args) {
        String str = "{\"metric\":\"kernel.maxproc\", \"endpoint\":\"srmon30-192.168.17.30\", \"hostip\":\"192.168.17.30\", \"timestamp\":\"1519885516\", \"step\":\"60\", \"value\":\"32768.000\", \"counterType\":\"GAUGE\", \"tags\":{}}\n" +
                "{\"metric\":\"kernel.files.allocated\", \"endpoint\":\"srmon30-192.168.17.30\", \"hostip\":\"192.168.17.30\", \"timestamp\":\"1519885516\", \"step\":\"60\", \"value\":\"11072.000\", \"counterType\":\"GAUGE\", \"tags\":{}}";
        System.out.println(str.split("\n").length);
    }
}

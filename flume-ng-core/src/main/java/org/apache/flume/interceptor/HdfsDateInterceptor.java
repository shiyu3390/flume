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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HdfsDateInterceptor implements Interceptor{
    private Logger logger = LoggerFactory.getLogger(HdfsDateInterceptor.class);
    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        String body = new String(event.getBody(), Charset.forName("UTF-8"));
        String logDate;
        try{
            if (body.startsWith("{") && body.endsWith("}")) {
                JSONObject json = JSON.parseObject(body);
                String timestamp = json.getString("timestamp");
                logDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(new Long(timestamp + "000")));
            }else {
                logDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            }
        }catch(Exception e) {
            logger.warn(e.getMessage());
            logDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        event.getHeaders().put("hdfs_day",logDate);
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> newList = new ArrayList<>();
        for (Event e:events) {
            Event intercept = intercept(e);
            if (intercept != null){
                newList.add(intercept);
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
            return new HdfsDateInterceptor();
        }
    }
}

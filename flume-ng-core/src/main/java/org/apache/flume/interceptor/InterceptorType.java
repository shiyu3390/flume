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

public enum InterceptorType {

  TIMESTAMP(TimestampInterceptor.Builder.class),
  HOST(HostInterceptor.Builder.class),
  STATIC(StaticInterceptor.Builder.class),
  REGEX_FILTER(
      RegexFilteringInterceptor.Builder.class),
  REGEX_EXTRACTOR(RegexExtractorInterceptor.Builder.class),
  MULTILINE(org.apache.flume.interceptor.MultilineInterceptor.Builder.class),
  HDFSDATE(HdfsDateInterceptor.Builder.class),
  MONITORKVTOHDFS(org.apache.flume.interceptor.MonitorKVToHDFSInterceptor.Builder.class),
  TRACEBACK(TraceBackInterceptor.Builder.class),
  FORMATLOGDATE(FormatLogDateInterceptor.Builder.class),
  REMOVE_HEADER(org.apache.flume.interceptor.RemoveHeaderInterceptor.Builder.class),
  SEARCH_REPLACE(SearchAndReplaceInterceptor.Builder.class);


  private final Class<? extends Interceptor.Builder> builderClass;

  private InterceptorType(Class<? extends Interceptor.Builder> builderClass) {
    this.builderClass = builderClass;
  }

  public Class<? extends Interceptor.Builder> getBuilderClass() {
    return builderClass;
  }

}

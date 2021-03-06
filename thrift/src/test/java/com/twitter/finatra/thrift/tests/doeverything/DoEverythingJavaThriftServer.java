package com.twitter.finatra.thrift.tests.doeverything;

import com.twitter.app.Flaggable;
import com.twitter.finagle.ThriftMux;
import com.twitter.finagle.tracing.NullTracer$;
import com.twitter.finatra.thrift.AbstractThriftServer;
import com.twitter.finatra.thrift.filters.AccessLoggingFilter;
import com.twitter.finatra.thrift.filters.ExceptionMappingFilter;
import com.twitter.finatra.thrift.filters.LoggingMDCFilter;
import com.twitter.finatra.thrift.filters.StatsFilter;
import com.twitter.finatra.thrift.filters.ThriftMDCFilter;
import com.twitter.finatra.thrift.filters.TraceIdMDCFilter;
import com.twitter.finatra.thrift.routing.JavaThriftRouter;
import com.twitter.finatra.thrift.tests.doeverything.exceptions.BarExceptionMapper;
import com.twitter.finatra.thrift.tests.doeverything.exceptions.FooExceptionMapper;
import com.twitter.util.NullMonitor$;

public class DoEverythingJavaThriftServer extends AbstractThriftServer {
    private String name;

    public DoEverythingJavaThriftServer() {
        this("example-java-server");
    }

    public DoEverythingJavaThriftServer(String name) {
        this.name = name;
        flag().create(
            "magicNum",
            "26",
            "Magic number",
            Flaggable.ofString());
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ThriftMux.Server configureThriftServer(ThriftMux.Server server) {
        return server
            .withMonitor(NullMonitor$.MODULE$)
            .withTracer(NullTracer$.MODULE$);
    }

    @Override
    public void warmup() {
        handle(DoEverythingJavaThriftWarmupHandler.class);
    }

    @Override
    public void configureThrift(JavaThriftRouter router) {
        router
            .filter(LoggingMDCFilter.class)
            .filter(TraceIdMDCFilter.class)
            .filter(ThriftMDCFilter.class)
            .filter(AccessLoggingFilter.class)
            .filter(StatsFilter.class)
            .filter(ExceptionMappingFilter.class)
            .exceptionMapper(DoEverythingJavaExceptionMapper.class)
            .exceptionMapper(FooExceptionMapper.class)
            .exceptionMapper(BarExceptionMapper.class)
            .add(DoEverythingJavaThriftController.class);
    }

}

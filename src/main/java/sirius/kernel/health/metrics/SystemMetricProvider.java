/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package sirius.kernel.health.metrics;

import sirius.kernel.async.CallContext;
import sirius.kernel.di.std.Part;
import sirius.kernel.di.std.Register;
import sirius.kernel.health.MemoryBasedHealthMonitor;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

/**
 * Provides core metrics for the operating system, the Java Virtual Machine and central frameworks.
 */
@Register
public class SystemMetricProvider implements MetricProvider {

    private List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
    private List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();

    @Part
    private MemoryBasedHealthMonitor monitor;

    @Override
    public void gather(MetricsCollector collector) {
        gatherMemoryMetrics(collector);
        gatherGCMetrics(collector);

        collector.differentialMetric("kernel_interactions",
                                     "sys-interactions",
                                     "Interactions",
                                     CallContext.getInteractionCounter().getCount(),
                                     "/min");
        collector.differentialMetric("kernel_log_entries",
                                     "sys-logs",
                                     "Log Messages",
                                     monitor.getNumLogMessages(),
                                     "/min");
        collector.differentialMetric("kernel_incidents",
                                     "sys-incidents",
                                     "Incidents",
                                     monitor.getNumIncidents(),
                                     "/min");
        collector.differentialMetric("kernel_unique_incidents",
                                     "sys-unique-incidents",
                                     "Unique Incidents",
                                     monitor.getNumUniqueIncidents(),
                                     "/min");
    }

    private void gatherGCMetrics(MetricsCollector collector) {
        for (GarbageCollectorMXBean gc : gcs) {
            collector.differentialMetric("jvm_gc_" + gc.getName().toLowerCase(),
                                         "jvm-gc",
                                         "GC - " + gc.getName(),
                                         gc.getCollectionCount(),
                                         "/min");
        }
    }

    private void gatherMemoryMetrics(MetricsCollector collector) {
        for (MemoryPoolMXBean pool : pools) {
            if (pool.getName().toLowerCase().contains("old") && pool.getUsage().getMax() > 0) {
                collector.metric("jvm_old_heap",
                                 "jvm-old-heap",
                                 "JVM Heap (" + pool.getName() + ")",
                                 100d * pool.getUsage().getUsed() / pool.getUsage().getMax(),
                                 "%");
            }
        }
    }
}

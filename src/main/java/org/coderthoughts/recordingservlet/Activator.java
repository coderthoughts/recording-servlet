package org.coderthoughts.recordingservlet;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
    private final Map<String, Date> mapService = new ConcurrentHashMap<>();
    private ServiceTracker<HttpService, HttpService> st;

    @Override
    public void start(BundleContext context) throws Exception {
        Dictionary<String, Object> props = new Hashtable<>();
        props.put("org.coderthoughts.recordingservlet", "org.coderthoughts.recordingservlet");

        @SuppressWarnings("rawtypes")
        ServiceRegistration irreg = context.registerService(Map.class, mapService, props);
        @SuppressWarnings("unchecked")
        ServiceRegistration<Map<String, Date>> reg = irreg;

        st = new ServiceTracker<HttpService, HttpService>(context, HttpService.class, null) {
            @Override
            public HttpService addingService(ServiceReference<HttpService> reference) {
                HttpService svc = super.addingService(reference);
                try {
                    svc.registerServlet("/recording", new RecordingServlet(reg, mapService), null, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return svc;
            }
        };
        st.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        st.close();
    }
}
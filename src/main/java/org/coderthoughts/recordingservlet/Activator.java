package org.coderthoughts.recordingservlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
    private ServiceTracker<HttpService, HttpService> st;

    @Override
    public void start(BundleContext context) throws Exception {
        st = new ServiceTracker<HttpService, HttpService>(context, HttpService.class, null) {
            @Override
            public HttpService addingService(ServiceReference<HttpService> reference) {
                HttpService svc = super.addingService(reference);
                try {
                    svc.registerServlet("/recording", new RecordingServlet(), null, null);
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

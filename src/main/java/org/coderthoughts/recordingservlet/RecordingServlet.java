package org.coderthoughts.recordingservlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RecordingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int MAX_RECORDS = 20;

    ConcurrentMap<String, List<Recording>> topics = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        PrintWriter writer = resp.getWriter();
//        writer.write("<HTML><BODY><H1>Hi THere</H1></BODY></HTML>");

        String pathInfo = req.getPathInfo();
        if (pathInfo == null)
            pathInfo = "";
        if (pathInfo.startsWith("/"))
            pathInfo = pathInfo.substring(1);

        String res;
        String val;
        if (pathInfo.length() == 0) {
            res = usage();
        } else if (req.getParameterMap().size() == 0) {
            res = listRecords(pathInfo);
        } else if ((val = req.getParameter("val")) != null) {
            res = recordValue(pathInfo, val);
        } else if ("getval".equals(req.getParameter("action"))) {
            res = getGetLastValue(pathInfo);
        } else {
            res = usage();
        }

        resp.setHeader("Content-Type", "text/html");
        resp.setHeader("Content-Length", Integer.toString(res.length()));

        resp.getWriter().write(res);

    }

    private String listRecords(String pathInfo) {
        List<Recording> records = topics.get(pathInfo);
        if (records == null)
            return "No records for: " + pathInfo;

        StringBuilder sb = new StringBuilder("Records for");
        sb.append(pathInfo);
        sb.append("<UL>");
        for (Recording r : records) {
            sb.append("<LI>" + r.getValue() + " at " + r.getTimestamp());
        }
        sb.append("</UL>");
        return sb.toString();
    }

    private String recordValue(String pathInfo, String val) {
        List<Recording> records = topics.get(pathInfo);
        if (records == null) {
            records = new ArrayList<>(MAX_RECORDS);
            topics.put(pathInfo, records);
        }

        if (records.size() > MAX_RECORDS)
            records.remove(0);

        records.add(new Recording(val, new Date()));
        return "Recorded: " + val;
    }

    private String getGetLastValue(String pathInfo) {
        List<Recording> records = topics.get(pathInfo);
        if (records == null || records.size() == 0)
            return ""; // TODO 404
        return records.get(records.size() - 1).getValue();
    }

    private String usage() {
        return "The following URLs are supported:"
                + "<UL><LI>/token: list all recordings for this token"
                + "<LI>/token?val=someval: record this value for this token"
                + "<LI>/token?action=getval: get the last value for this token or 404 if there is no value."
                + "</UL>";
    }
}

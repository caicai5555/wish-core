package com.foundation.common.security;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 安全过滤器，过滤xss与sql注入攻击
 * Created by fanqinghui on 2016/8/31.
 */
public class SecurityFilter implements Filter {
    private FilterConfig filterConfig;
    private final int XSS_PROTECT = 0;
    private final int SQL_PROTECT = 0;
    private final int STRUTS_PROTECT = 1;
    private final int FILE_PROTECT = 0;
    private final int DEBUG = 1;
    private final int BLOCK = 1;
    private String REDIRECT = "/";
    private HashMap<String, Integer> protectParams;
    private final String[] XSSAttackPattern = {"script", "iframe", "<a", "a>", "onclick", "onmouse", "=", "(", ")"};
    private final String[] SQLAttackPattern = {"'", ";", "\"", "", "#",
            "create", "alter", "drop", "rename",
            "select", "insert", "update", "delete", "grant",
            "revoke", "char", "int", "@@version", "exec", "rand(", "benchmark",
            "union", "waitfor", "order by"};

    private final String[] FILEAttackPattern = {"../", "/etc", "/proc"};
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SecurityFilter() {
        this.protectParams = new HashMap();
        this.protectParams.put("XSS_PROTECT", Integer.valueOf(0));
        this.protectParams.put("SQL_PROTECT", Integer.valueOf(0));
        this.protectParams.put("FILE_PROTECT", Integer.valueOf(0));
        this.protectParams.put("DEBUG", Integer.valueOf(1));
        this.protectParams.put("BLOCK", Integer.valueOf(1));
    }

    public void destroy() {
    }

    private int doCheckPattern(String time, String clientIP, String path, String attackType, String[] attackPattern, String paramName, String[] paramValues, int debug) {
        for (int aidx = 0; aidx < attackPattern.length; aidx++) {
            if (paramName.toLowerCase().indexOf(attackPattern[aidx].toLowerCase()) > -1) {
                if (debug == 1) {
                    System.out.printf("[%s] %s ATTACK PATH: %s ATTACK TYPE: %s ATTACK paraName： %s ATTACK name %s \n", new Object[]{time, clientIP, path, attackType, paramName, paramName});
                }
                return -1;
            }
            for (int pidx = 0; pidx < paramValues.length; pidx++) {
                if (paramValues[pidx].toLowerCase().indexOf(attackPattern[aidx].toLowerCase()) > -1) {
                    if (debug == 1) {
                        System.out.printf("[%s] %s ATTACK PATH: %s ATTACK TYPE: %s ATTACK paraName： %s ATTACK value %s \n", new Object[]{time, clientIP, path, attackType, paramName, paramValues[pidx]});
                    }
                    return -1;
                }
            }
        }

        return 0;
    }

    private int doProtect(ServletRequest request) {
        Enumeration paramNames = request.getParameterNames();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        String clientIP = request.getRemoteAddr();
        int debug = ((Integer) this.protectParams.get("DEBUG")).intValue();
        String time = this.df.format(new Date());
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();

            String[] paramValues = request.getParameterValues(paramName);
            if ((((Integer) this.protectParams.get("XSS_PROTECT")).intValue() == 1) && (doCheckPattern(time, clientIP, path, "XSS", this.XSSAttackPattern, paramName, paramValues, debug) < 0)) {
                return -1;
            }
            if ((((Integer) this.protectParams.get("SQL_PROTECT")).intValue() == 1) && (doCheckPattern(time, clientIP, path, "SQL", this.SQLAttackPattern, paramName, paramValues, debug) < 0)) {
                return -1;
            }
            if ((((Integer) this.protectParams.get("FILE_PROTECT")).intValue() == 1) && (doCheckPattern(time, clientIP, path, "FILE", this.FILEAttackPattern, paramName, paramValues, debug) < 0)) {
                return -1;
            }
        }
        return 0;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if ((doProtect(request) < 0) && (((Integer) this.protectParams.get("BLOCK")).intValue() == 1)) {
            HttpServletResponse redirect = (HttpServletResponse) response;
            redirect.sendRedirect(this.REDIRECT);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void init(FilterConfig fConfig)
            throws ServletException {
        this.filterConfig = fConfig;
        for (Enumeration e = fConfig.getInitParameterNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            if (name.equals("REDIRECT")) {
                this.REDIRECT = fConfig.getInitParameter(name);
            } else {
                if (this.protectParams.get(name) == null)
                    continue;
                try {
                    int value = Integer.parseInt(fConfig.getInitParameter(name));
                    this.protectParams.put(name, Integer.valueOf(value));
                } catch (NumberFormatException nfe) {
                    if (((Integer) this.protectParams.get("DEBUG")).intValue() == 1)
                        System.out.println("ERROR FORMAT: " + name + "   " + fConfig.getInitParameter(name));
                }
            }
        }
        if (((Integer) this.protectParams.get("DEBUG")).intValue() == 1) {
            Iterator iter = this.protectParams.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                //System.out.println("CONFIG key: " + key + " value: " + val);
            }
            //System.out.println("CONFIG key : REDIRECT value: " + this.REDIRECT);
        }
    }
}


// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServletDelegate.java

package com.sun.xml.rpc.server.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ServletDelegate {

    public abstract void init(ServletConfig servletconfig) throws ServletException;

    public abstract void destroy();

    public abstract void doPost(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse) throws ServletException;

    public abstract void doGet(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse) throws ServletException;
}

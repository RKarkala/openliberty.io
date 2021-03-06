/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.website;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class TLSFilter implements Filter {
    public void destroy() {
    }

    public void init(FilterConfig cfg) {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = ((HttpServletResponse)resp);

        String servletPath = ((HttpServletRequest)req).getServletPath();
        String serverName = req.getServerName();
        
        if(!Constants.API_SERVLET_PATH.equals(servletPath) &&
        		(serverName.equals(Constants.OPEN_LIBERTY_GREEN_APP_HOST)
				 || serverName.equals(Constants.OPEN_LIBERTY_BLUE_APP_HOST))) {   
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else if ("http".equals(req.getScheme())) {
          response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
          response.setHeader("Location", ((HttpServletRequest)req).getRequestURL().replace(0, 4, "https").toString());
        } else if ("https".equals(req.getScheme())) {
          response.setHeader("Strict-Transport-Security", "max-age=3600");

          String uri = ((HttpServletRequest)req).getRequestURI();
          if(uri.startsWith("/img/")) {
        	  response.setHeader("Cache-Control", "max-age=604800");
          } else {
        	  response.setHeader("Cache-Control", "no-cache");
          }

        }

        chain.doFilter(req, resp);
    }
}



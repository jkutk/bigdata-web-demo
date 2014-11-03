package at.jku.tk.steinbauer.bigdata.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharacterEncodingFilter implements Filter {

	public static final String GLOBAL_ENCODING = "UTF-8";
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {
		request.setCharacterEncoding(GLOBAL_ENCODING);
		response.setCharacterEncoding(GLOBAL_ENCODING);
		next.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// no need for configuration
	}

	@Override
	public void destroy() {
		// no need to tear down
	}
}

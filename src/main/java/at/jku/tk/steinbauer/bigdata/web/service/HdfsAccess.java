package at.jku.tk.steinbauer.bigdata.web.service;

import java.io.IOException;
import java.util.List;

public interface HdfsAccess {

	public List<String> list(String path) throws IOException;
	
	public long contentSize(String path) throws IOException;
	
	public byte[] retrieveContent(String path)	throws IOException;

}

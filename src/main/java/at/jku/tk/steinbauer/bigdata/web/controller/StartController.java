package at.jku.tk.steinbauer.bigdata.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import at.jku.tk.steinbauer.bigdata.web.service.HdfsAccess;

public class StartController extends MultiAction implements Serializable {

	private static final long serialVersionUID = -2637651955332466338L;
	
	public static final String RESULT_PATH = "hdfs://127.0.0.1/user/hue/out";
	
	public static final String F_RESULT_LIST = "resultList";
	
	public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 mb
	
	public static boolean sandbox = false;
	
	@Autowired
	private HdfsAccess hdfsAccess;
	
	private String selectedPath;
	
	public Event listResults(RequestContext context) throws IOException {
		List<String> children = hdfsAccess.list(RESULT_PATH);
		context.getFlowScope().put(F_RESULT_LIST, children);
		return success();
	}
	
	public Event loadResult(RequestContext context) throws IOException {
		String p = RESULT_PATH + "/" + selectedPath;
		long fileSize = hdfsAccess.contentSize(p);
		if(fileSize < MAX_FILE_SIZE) {
			byte[] data;
			if(sandbox) {
				data = hdfsAccess.retrieveContent(p);
			}else{
				data = loadDummy();
			}
			System.out.println(data.length);
		}
		return error();
	}
	
	private byte[] loadDummy() {
		return new byte[10];
	}

	public String getSelectedPath() {
		return selectedPath;
	}

	public void setSelectedPath(String selectedPath) {
		this.selectedPath = selectedPath;
	}

}

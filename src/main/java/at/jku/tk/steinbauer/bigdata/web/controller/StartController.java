package at.jku.tk.steinbauer.bigdata.web.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import at.jku.tk.steinbauer.bigdata.web.service.HdfsAccess;

public class StartController extends MultiAction implements Serializable {

	private static final long serialVersionUID = -2637651955332466338L;
	
	public static final String RESULT_PATH = "hdfs://sandbox/user/hue/out";
	
	public static final String F_RESULT_LIST = "resultList";
	
	public static final String F_HIGHCHARTS_DATA = "highchartsData";
	
	public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 mb
	
	public static final int MAX_LINES = 10;
	
	@Value("${useLocalStubs}")
	public static String useLocalStubs = "false";
	
	@Autowired
	private HdfsAccess hdfsAccess;
	
	private String selectedPath;
	
	public Event listResults(RequestContext context) throws IOException {
		List<String> children;
		if("true".equals(useLocalStubs)) {
			children = new ArrayList<>();
			children.add("local-dummy-data");
		}else{
			children = hdfsAccess.list(RESULT_PATH);
		}
		context.getFlowScope().put(F_RESULT_LIST, children);
		return success();
	}
	
	public Event loadResult(RequestContext context) throws IOException {
		String p = RESULT_PATH + "/" + selectedPath;
		long fileSize = "true".equals(useLocalStubs) ? 1 : hdfsAccess.contentSize(p);
		if(fileSize < MAX_FILE_SIZE) {
			byte[] data;
			if("true".equals(useLocalStubs)) {
				data = loadDummy();
			}else{
				data = hdfsAccess.retrieveContent(p);
			}
			String json = parseCsvToHighchartsJson(data);
			context.getFlowScope().put(F_HIGHCHARTS_DATA, json);
			return success();
		}
		return error();
	}
	
	private String parseCsvToHighchartsJson(byte[] csvData) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(csvData);
		BufferedReader reader = new BufferedReader(new InputStreamReader(bin));
		StringBuffer dataBuffer = new StringBuffer();
		StringBuffer labelBuffer = new StringBuffer();
		dataBuffer.append("[");
		labelBuffer.append("[");
		String line;
		int count = 0;
		while((line = reader.readLine()) != null && count <= MAX_LINES) {
			if(dataBuffer.length() > 1) {
				dataBuffer.append(",");
				labelBuffer.append(",");
			}
			line = line.replace('\001', ' '); // fix hive delimiters
			String[] csvLine = line.split("\\s+");
			if(csvLine.length > 1 && !"undefined".equals(csvLine[1])) {
				dataBuffer.append("['");
				dataBuffer.append(csvLine[1]);
				dataBuffer.append("',");
				dataBuffer.append(csvLine[0]);
				dataBuffer.append("]");
				labelBuffer.append("'");
				labelBuffer.append(csvLine[1]);
				labelBuffer.append("'");
				count ++;
			}
		}
		dataBuffer.append("]");
		labelBuffer.append("]");
		return "{chartData: " + dataBuffer.toString() + ", labels: " + labelBuffer.toString() + "}";
	}
	
	private byte[] loadDummy() throws IOException {
		InputStream inputStream = StartController.class.getResourceAsStream("/dummy2.csv");
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, bout);
		return bout.toByteArray();
	}

	public String getSelectedPath() {
		return selectedPath;
	}

	public void setSelectedPath(String selectedPath) {
		this.selectedPath = selectedPath;
	}

}

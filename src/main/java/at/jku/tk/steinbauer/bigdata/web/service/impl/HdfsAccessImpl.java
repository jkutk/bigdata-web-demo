package at.jku.tk.steinbauer.bigdata.web.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.springframework.stereotype.Service;

import at.jku.tk.steinbauer.bigdata.web.service.HdfsAccess;

@Service("hdfsAccess")
public class HdfsAccessImpl implements Serializable, HdfsAccess {

	private static final long serialVersionUID = -8350299605162963471L;
	
	private static Configuration configuration = null;
	
	private static FileSystem filesystem = null;
	
	private Configuration getConfiguration() {
		if(configuration == null) {
			configuration = new Configuration();
			configuration.set("fs.defaultFS", "hdfs://127.0.0.1");
		}
		return configuration;
	}
	
	private FileSystem getFileSystem() throws IOException {
		if(filesystem == null) {
			filesystem = FileSystem.get(getConfiguration());
		}
		return filesystem;
	}
	
	private void checkPathExists(Path p, FileSystem fs) throws IOException {
		if(!fs.exists(p)) {
			throw new IOException("Path " + p.toString() + " not found");
		}
	}

	@Override
	public List<String> list(String resultPath) throws IOException {
		Path p = new Path(resultPath);
		checkPathExists(p, getFileSystem());
		ArrayList<String> list = new ArrayList<>();
		for(FileStatus fs : getFileSystem().listStatus(p)) {
			if(fs.isDirectory()) {
				list.add(fs.getPath().getName());
			}
		}
		return list;
	}

	@Override
	public long contentSize(String path) throws IOException {
		long size = 0;
		Path p = new Path(path);
		checkPathExists(p, getFileSystem());
		RemoteIterator<LocatedFileStatus> files = getFileSystem().listFiles(p, false);
		while(files.hasNext()) {
			LocatedFileStatus fs = files.next();
			ContentSummary summary = getFileSystem().getContentSummary(fs.getPath());
			size += summary.getLength();
		}
		return size;
	}

	@Override
	public byte[] retrieveContent(String path) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Path p = new Path(path);
		checkPathExists(p, getFileSystem());
		RemoteIterator<LocatedFileStatus> files = getFileSystem().listFiles(p, false);
		while(files.hasNext()) {
			LocatedFileStatus fs = files.next();
			if(fs.isFile()) {
				FSDataInputStream input = getFileSystem().open(fs.getPath());
				IOUtils.copy(input, buffer);
			}
		}
		return buffer.toByteArray();
	}

}

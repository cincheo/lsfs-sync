package lsfs;

import java.util.regex.Pattern;

public class Options {

	public String in;
	public String out;
	public String root;

	public String includeFilter;
	public String includeDirFilter;
	public String excludeFilter;
	public String excludeDirFilter;

	public Pattern includePattern;
	public Pattern includeDirPattern;
	public Pattern excludePattern;
	public Pattern excludeDirPattern;

	public void init() {
		includePattern = includeFilter == null ? null
				: Pattern.compile(includeFilter.replace(".", "\\.").replace("*", ".*"));
		includeDirPattern = includeDirFilter == null ? null
				: Pattern.compile(includeDirFilter.replace(".", "\\.").replace("*", ".*"));
		excludePattern = excludeFilter == null ? null
				: Pattern.compile(excludeFilter.replace(".", "\\.").replace("*", ".*"));
		excludeDirPattern = excludeDirFilter == null ? null
				: Pattern.compile(excludeDirFilter.replace(".", "\\.").replace("*", ".*"));
	}

	@Override
	public String toString() {
		return "{ in=" + in + ", includeFilter=" + includeFilter + ", includeDirFilter=" + includeDirFilter
				+ ", excludeFilter=" + excludeFilter + ", excludeDirFilter=" + excludeDirFilter + " }";
	}

}

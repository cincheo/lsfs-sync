package lsfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

/**
 * Creates a LocalStorageFS image of the given directory, to the given image
 * file.
 */

public class Main {

	public final static String EXTENSION = ".lsfs.js";
	public final static String CONF = "lsfs.json";

	/**
	 * The entry point.
	 * 
	 * @param args
	 *            args[0] contains the directory to create an image for, arg[1]
	 *            contains the path of the image file to be created·
	 */

	public static void main(String[] args) throws Exception {

		File workingDirectory = new File(".");

		if (!workingDirectory.exists()) {
			System.err.println("working directory does not exist: '" + workingDirectory.getAbsolutePath() + "'");
			System.exit(-1);
		}

		if (!workingDirectory.isDirectory()) {
			System.err.println("not a directory: '" + workingDirectory.getAbsolutePath() + "'");
			System.exit(-1);
		}

		File conf = new File(CONF);
		if (!conf.exists()) {
			System.err.println("cannot find configuration file '" + conf + "' in working directory '"
					+ workingDirectory.getAbsolutePath() + "'");
			System.exit(-1);
		}

		String confJson = FileUtils.readFileToString(conf);
		Options options = new Gson().fromJson(confJson, Options.class);
		System.out.println("conf: " + options);
		options.init();
		if (options.in == null) {
			System.err.println("missing mandatory parameter 'in'");
			System.exit(-1);
		}
		if (options.out == null) {
			System.err.println("missing mandatory parameter 'out'");
			System.exit(-1);
		}

		String imageName = options.out + EXTENSION;
		try (PrintWriter printWriter = new PrintWriter(imageName)) {
			System.out.println("creating image: " + imageName);
			Printer out = new Printer(printWriter);
			UUID uuid = UUID.randomUUID();
			out.printLine("if(localStorage.getItem('lsfs.id') !== '" + uuid.toString() + "') {");
			out.startIndent();
			out.printLine("java.io.File.fs.clear();");
			out.printLine("localStorage.setItem('lsfs.id', '" + uuid.toString() + "');");
			File f = new File(options.in);
			if (f.exists()) {
				appendFileToImage(options, out, f);
			}
			out.endIndent();
			out.printLine("}");
			System.out.println("image '" + imageName + "' created successfully");
		}
	}

	/**
	 * A helper class to print out the image file content.
	 */
	public static class Printer {
		private PrintWriter out;
		private static int indent = 0;

		public Printer(PrintWriter out) {
			super();
			this.out = out;
		}

		public Printer startIndent() {
			indent++;
			return this;
		}

		public Printer endIndent() {
			indent--;
			return this;
		}

		public Printer printIndent() {
			for (int i = 0; i < indent; i++) {
				out.print("    ");
			}
			return this;
		}

		public Printer print(String string) {
			out.print(string);
			return this;
		}

		public Printer println(String string) {
			out.println(string);
			return this;
		}

		public Printer printLine(String string) {
			printIndent().println(string);
			return this;
		}

		public Printer println() {
			out.println();
			return this;
		}

	}

	/**
	 * Appends a file to the given image writer.
	 * 
	 * @param out
	 *            the writer to write into the image file
	 * @param f
	 *            the file to be written
	 */

	private static void appendFileToImage(Options options, Printer out, File f) {
		if (f.getName().endsWith(EXTENSION)) {
			return;
		}
		String path = f.getPath().equals(options.in) ? "" : f.getPath().substring(options.in.length() + 1);
		if (f.isFile()) {
			if (options.includePattern != null && !options.includePattern.matcher(path).matches()) {
				return;
			}
			if (options.excludePattern != null && options.excludePattern.matcher(path).matches()) {
				return;
			}
		}
		if (f.isDirectory()) {
			if (options.includeDirPattern != null && !options.includeDirPattern.matcher(path).matches()) {
				return;
			}
			if (options.excludeDirPattern != null && options.excludeDirPattern.matcher(path).matches()) {
				return;
			}
		}
		String fullPath = (options.root == null ? path : ("".equals(path) ? options.root : options.root + "/" + path));
		System.out.println("adding: " + fullPath);
		out.printLine("f = new java.io.File(\"" + fullPath + "\");");
		if (f.isDirectory()) {
			out.printLine("f.mkdirs();");
			for (File child : f.listFiles()) {
				if (child.isFile()) {
					appendFileToImage(options, out, child);
				}
			}
			for (File child : f.listFiles()) {
				if (child.isDirectory()) {
					appendFileToImage(options, out, child);
				}
			}
		} else {
			try (FileInputStream fis = new FileInputStream(f)) {
				out.printLine("f.createNewFile();");
				byte[] content = new byte[fis.available()];
				fis.read(content);
				out.printLine("e = java.io.File.fs.getEntry(f.getAbsolutePath());");
				out.printLine("e.data = '" + Base64.getEncoder().encodeToString(content) + "';");
				out.printLine("java.io.File.fs.putEntry(f.getAbsolutePath(), e);");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

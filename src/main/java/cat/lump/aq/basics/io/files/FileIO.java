package cat.lump.aq.basics.io.files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class FileIO {
	public final static String separator = System.getProperty("file.separator");

	static final int BUFF_SIZE = 100000;
	static final byte[] buffer = new byte[BUFF_SIZE];

	public static String md5(File file) throws FileNotFoundException,
			NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance("MD5");

		FileInputStream input = new FileInputStream(file);
		FileChannel inputChannel = input.getChannel();
		try {
			ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4096);
			byte[] buf = new byte[4096];
			int len;
			long c = 0;
			while ((len = inputChannel.read(inputBuffer)) >= 0) {
				inputBuffer.flip();
				inputBuffer.get(buf, 0, len);
				digest.update(buf, 0, len);
				inputBuffer.flip();
				c += len;
			}
			if (c != inputChannel.size()) {
				throw new IllegalStateException(c + " != " + len);
			}
		} finally {
			inputChannel.close();
			input.close();
		}

		String result = (new BigInteger(1, digest.digest())).toString(16);
		return result.length() == 31 ? "0" + result : result;
	}

	public static String md5(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < md5hash.length; i++) {
			int halfbyte = (md5hash[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = md5hash[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static List<String> getFilesRecursively(File dir, final String ext) {
		return getFilesRecursively(dir, ext, Long.MIN_VALUE, Long.MAX_VALUE);
	}

	public static void writeObject(Object x, File f) {
		if (f.getParent() != null && !(new File(f.getParent()).exists()))
			new File(f.getParent()).mkdirs();

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(x);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Object readObject(File f) {
		FileInputStream fis;
		Object result = null;
		try {
			fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);
			result = ois.readObject();
			ois.close();
			bis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static List<String> getFilesRecursively(File dir, final String ext,
			long timeMin, long timeMax) {
		List<String> fileList = null;
		if (dirCanBeRead(dir)) {
			fileList = new LinkedList<String>();
			String[] list = dir.list();

			for (String s : list) {
				String fileName = dir.getAbsolutePath() + separator + s;
				File f = new File(fileName);
				Long lastMod = f.lastModified();

				if (f.isDirectory()) {
					fileList.addAll(getFilesRecursively(f, ext, timeMin,
							timeMax));
				} else if ((s.toLowerCase().endsWith(ext)
						&& !s.toLowerCase().startsWith("stat")
						&& timeMin <= lastMod && timeMax >= lastMod)) {
					fileList.add(dir.getAbsolutePath() + separator + s);
				}
			}
		}
		if (fileList != null)
			Collections.sort(fileList, String.CASE_INSENSITIVE_ORDER);

		return fileList;
	}

	/**
	 * Gets all the files with a given extension {@code ext}
	 * @param dir
	 * @param ext
	 * @return 
	 *       A List<String> with the files
	 */
	public static List<String> getFilesExt(File dir, final String ext) {
		List<String> fileList = null;
		if (dirCanBeRead(dir)) {
			fileList = new LinkedList<String>();
			String[] list = dir.list();

			for (String s : list) {
				String fileName = dir.getAbsolutePath() + separator + s;
				File f = new File(fileName);
				if (!f.isDirectory() && s.toLowerCase().endsWith(ext)) {
					fileList.add(fileName);
				}
			}
		}
		if (fileList != null)
			Collections.sort(fileList, String.CASE_INSENSITIVE_ORDER);

		return fileList;
	}
	
	// ///////////BEGIN ABC /////

	/**
	 * Check whether the directory exists and can be read.
	 * 
	 * @param dir  
	 * @return true if directory can be read
	 */
	public static boolean dirCanBeRead(File dir) {
		if (dir.isDirectory() && dir.canRead())
			return true;
		return false;
	}

	public static List<String> getSpecificFilesRecursively(File dir,
			final String pref, final String ext) {
		return getSpecificFilesRecursively(dir, pref, ext, Long.MIN_VALUE,
				Long.MAX_VALUE);
	}

	public static List<String> getSpecificFilesRecursively(File dir,
			final String pref, final String ext, long timeMin, long timeMax) {
		List<String> fileList = null;
		if (dir.isDirectory() && dir.canRead()) {
			fileList = new LinkedList<String>();
			String[] list = dir.list();

			for (String s : list) {
				String fileName = dir.getAbsolutePath() + separator + s;
				File f = new File(fileName);
				Long lastMod = f.lastModified();

				if (f.isDirectory()) {
					fileList.addAll(getFilesRecursively(f, ext, timeMin,
							timeMax));
					// } else if((s.toLowerCase().startsWith(pref) &&
					// s.toLowerCase().endsWith(ext) &&
					// !s.toLowerCase().startsWith("stat") && timeMin<=lastMod
					// && timeMax>=lastMod)) {
				} else if ((s.startsWith(pref) && s.endsWith(ext)
						&& !s.startsWith("stat") && timeMin <= lastMod && timeMax >= lastMod)) {
					fileList.add(dir.getAbsolutePath() + separator + s);
				}
			}
		}
		if (fileList != null)
			Collections.sort(fileList, String.CASE_INSENSITIVE_ORDER);

		return fileList;
	}

	public static List<String> getSpecificDirs(File dir, final String pref,
			final String ext) {
		List<String> fileList = null;
		if (dir.isDirectory() && dir.canRead()) {
			fileList = new LinkedList<String>();
			String[] list = dir.list();

			for (String s : list) {
				String fileName = dir.getAbsolutePath() + separator + s;
				File f = new File(fileName);

				if (f.isDirectory()
						&& (s.startsWith(pref) && s.endsWith(ext) && !s
								.startsWith("stat"))) {
					fileList.add(dir.getAbsolutePath() + separator + s);
				}
			}
		}
		if (fileList != null)
			Collections.sort(fileList, String.CASE_INSENSITIVE_ORDER);

		return fileList;
	}

	public static void createDir(File dir) throws IOException {
		if (dir.isDirectory())
			return;
		dir.mkdir();
	}

	/**
	 * Opens a gziped file and returns the lines it contains
	 * 
	 * @param fileName
	 *            The input gziped file
	 * @return A BufferedReader useful for the efficient reading of the long
	 *         file
	 * @throws IOException
	 */
	public static BufferedReader gZipToString(String fileName)
			throws IOException {
		FileInputStream fin = new FileInputStream(fileName);
		GZIPInputStream gzipIS = new GZIPInputStream(fin);
		InputStreamReader isReader = new InputStreamReader(gzipIS);
		BufferedReader bReader = new BufferedReader(isReader);
		return bReader;
	}

	/**
	 * Opens a file and returns the lines in it
	 * 
	 * @param f
	 * @return array of
	 * @throws IOException
	 */
	public static String[] fileToLines(File f) throws IOException {
		List<String> lines = new ArrayList<String>();
		String line;

		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		try {
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} finally {
			br.close();
			fr.close();
		}
		return lines.toArray(new String[lines.size()]);
	}

	// /////////////END ABC /////

	/**
	 * Counts the lines in a file
	 * http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
	 * @param f
	 * @return number of lines
	 * @throws IOException
	 */
	public static int fileCountLines(File f) throws IOException {
		LineNumberReader  lnr = new LineNumberReader(new FileReader(f));
		lnr.skip(Long.MAX_VALUE);
		int count = lnr.getLineNumber();
		lnr.close();
		return count;		
	}

//	public static int fileCountLinesSlow(File f) throws IOException {
//		String line;
//		FileReader fr = new FileReader(f);
//		BufferedReader br = new BufferedReader(fr);
//		int count = 0;
//		try {
//			while ((line = br.readLine()) != null) {
//				count++;
//			}
//		} finally {
//			br.close();
//			fr.close();
//		}
//		return count;		
//	}

	
	public static String fileToString(File f) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream input = new FileInputStream(f);
		FileChannel inputChannel = input.getChannel();
		try {
			ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4096);
			byte[] buf = new byte[4096];

			int len;
			long c = 0;
			while ((len = inputChannel.read(inputBuffer)) >= 0) {
				inputBuffer.flip();
				inputBuffer.get(buf, 0, len);
				// Dont read UTF-8 Header
				if (c == 0 && buf[0] == -17 && buf[1] == -69 && buf[2] == -65)
					baos.write(buf, 3, len - 3);
				else
					baos.write(buf, 0, len);
				inputBuffer.flip();
				c += len;
			}
			if (c != inputChannel.size()) {
				throw new IllegalStateException(c + " != " + len);
			}
		} finally {
			inputChannel.close();
			input.close();
		}

		String result = baos.toString("UTF-8");
		baos.close();
		return result;
	}

	public static void stringToFile(File f, String text, boolean bom)
			throws IOException {
		if ((f.getParent()) != null && !(new File(f.getParent()).exists()))
			new File(f.getParent()).mkdirs();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Write UTF-8 BOM
		if (bom)
			baos.write(new byte[] { -17, -69, -65 });
		PrintStream printer = new PrintStream(baos, true, "UTF-8");
		printer.print(text);
		printer.close();
		// log.debug("new FileOutputStream: " + f.getAbsolutePath());
		FileOutputStream fos = new FileOutputStream(f);
		baos.writeTo(fos);
		baos.close();
		fos.close();
		// log.debug("close FileOutputStream: " + f.getAbsolutePath());

	}

	public static void appendStringToFile(File f, String text, boolean bom)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Write UTF-8 BOM
		if (bom)
			baos.write(new byte[] { -17, -69, -65 });
		PrintStream printer = new PrintStream(baos, true, "UTF-8");
		printer.print(text);
		printer.close();
		FileOutputStream fos = new FileOutputStream(f, true);   //this is the only difference for appending
		baos.writeTo(fos);
		baos.close();
		fos.close();
	}

	public static void copy(File from, File to) throws IOException {
		if (!(new File(to.getParent()).exists()))
			new File(to.getParent()).mkdirs();

		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1) {
						break;
					}
					out.write(buffer, 0, amountRead);
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	public static void move(File in, File out) throws IOException {
		new File(out.getAbsolutePath().substring(0,
				out.getAbsolutePath().lastIndexOf(separator))).mkdirs();
		copy(in, out);
		in.delete();
	}

	/**
	 * Deletes all files and subdirectories under "dir".
	 * 
	 * @param dir
	 *            Directory to be deleted
	 * @return boolean Returns "true" if all deletions were successful. If a
	 *         deletion fails, the method stops attempting to delete and returns
	 *         "false".
	 */
	public static boolean deleteDir(File dir) {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public static boolean deleteFile(File file) {
		return file.delete();
	}

	public static String changeFileSuffix(File filename, String newSuffix) {
		return changeFileSuffix(filename.toString(), newSuffix);
	}

	/**
	 * 
	 * Given a filename, whether relative or absolute, it substitutes the suffix
	 * for a newSuffix.
	 * 
	 * If current filename does not contain suffix, newSuffix is appended at the
	 * end
	 * 
	 * Note that this method is able to handle absolute paths with dots in the
	 * middle of the parent directories.
	 * 
	 * 
	 * @param filename
	 * @param newSuffix
	 * @return file with the new suffix
	 */
	public static String changeFileSuffix(String filename, String newSuffix) {
		int lastDot = filename.lastIndexOf(".");
		int lastSeparator = filename.lastIndexOf(separator);
		StringBuffer sb = new StringBuffer();

		if (lastDot == -1 || // the file name does not contain any dot
				lastDot < lastSeparator) // the dot is in a directory, not in
											// the file
			sb.append(filename).append(".");
		else
			sb.append(filename.substring(0, lastDot + 1));

		sb.append(newSuffix);

		return sb.toString();
	}

}

/*
 * StringTool.java
 * 
 * Created on September 22, 2004, 1:18 PM
 */

package etc;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// import com.lowagie.text.pdf.codec.Base64;

/**
 * 
 * @author Administrator
 */
public class StringTool {

	static {
		useDotDecimalMark();
	}

	public final static java.text.NumberFormat ZeroFormat = new java.text.DecimalFormat("#");
	public final static java.text.DecimalFormat CommaFormat1 = new java.text.DecimalFormat("#0.0");
	public final static java.text.DecimalFormat CommaFormat2 = new java.text.DecimalFormat("#0.00");
	public final static java.text.DecimalFormat CommaFormat3 = new java.text.DecimalFormat("#0.000");
	public final static java.text.DecimalFormat CommaFormat4 = new java.text.DecimalFormat("#0.0000");
	public final static java.text.DecimalFormat CommaFormat5 = new java.text.DecimalFormat("#0.00000");
	public final static java.text.DecimalFormat CommaFormat6 = new java.text.DecimalFormat("#0.000000");
	public final static java.text.DecimalFormat CommaFormat12 = new java.text.DecimalFormat("#0.000000000000");
	public final static java.text.DecimalFormat CommaFormat24 = new java.text.DecimalFormat(
			"#0.000000000000000000000000");
	public final static java.text.DecimalFormat ZeroFormat1 = new java.text.DecimalFormat("0");
	public final static java.text.DecimalFormat ZeroFormat2 = new java.text.DecimalFormat("00");
	public final static java.text.DecimalFormat ZeroFormat3 = new java.text.DecimalFormat("000");
	public final static java.text.DecimalFormat ZeroFormat4 = new java.text.DecimalFormat("0000");
	public final static java.text.DecimalFormat ZeroFormat5 = new java.text.DecimalFormat("00000");
	public final static java.text.DecimalFormat ZeroFormat6 = new java.text.DecimalFormat("000000");
	public final static java.text.DecimalFormat ZeroFormat7 = new java.text.DecimalFormat("0000000");
	public final static java.text.DecimalFormat ZeroFormat8 = new java.text.DecimalFormat("00000000");
	public final static java.text.DecimalFormat ZeroFormat9 = new java.text.DecimalFormat("000000000");
	public final static java.text.DecimalFormat ZeroFormat10 = new java.text.DecimalFormat("0000000000");
	public final static java.text.DecimalFormat DotZeroFormat = new java.text.DecimalFormat(
			"###,###,###,###,###,###,###,###,###");
	public final static java.text.DecimalFormat DotCommaFormat4 = new java.text.DecimalFormat(
			"###,###,###,###,###,###,###,###,###,##0.0000");
	public final static java.text.DecimalFormat DotCommaFormat6 = new java.text.DecimalFormat(
			"###,###,###,###,###,###,###,###,###,##0.000000");
	public final static SimpleDateFormat DateMMMYYYY = new SimpleDateFormat("MMM yyyy");
	public final static SimpleDateFormat DateYYYYMMdd = new SimpleDateFormat("yyyyMMdd");
	public final static SimpleDateFormat DateYYYY_MM_dd = new SimpleDateFormat("yyyy.MM.dd");
	public final static SimpleDateFormat DateYYYYMMMddHHmmss = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
	public final static SimpleDateFormat DateHHmmss = new SimpleDateFormat("HH:mm:ss");
	public final static SimpleDateFormat DateYYYYMMddHHmm = new SimpleDateFormat("yyyy.MM.dd HH.mm");

	public final static String NL = System.getProperty("line.separator");
	public final static String BASE64CODES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	public static void useDotDecimalMark() {
		// Locale usLocale = Locale.of("en", "US");
		Locale usLocale = Locale.forLanguageTag("en-US");
		Locale.setDefault(usLocale);
	}

	public static boolean isCleanString(String text, String extraLegalCharacters) {
		String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890" + extraLegalCharacters;
		for (int i = 0; i < text.length(); i++) {
			if (valid.indexOf(text.charAt(i)) < 0) {
				return false;
			}
		}
		return true;
	}

	// /**
	// * Convert a byte array to base64 string
	// *
	// * @param source
	// * @return
	// * @throws IOException
	// */
	// public static String asBase64Zip(String source) throws IOException {
	// byte[] utf = source.getBytes("UTF8");
	// byte[] buf = zipStringToBytes(source);
	// String s = Base64.encodeBytes(source.getBytes("UTF-8")).toString();
	// return s;
	// }

	public static String untrunc(String source) {
		return source.replaceAll("\\r\\n", "");
	}

	/**
	 * Convert a byte array to base64 string
	 * 
	 * @param source
	 * @return
	 * @throws IOException
	 */
	// public static String asBase64Utf8(String source) throws IOException {
	// byte[] buf = source.getBytes("UTF8");
	// String s = Base64.encodeBytes(source.getBytes("UTF-8")).toString();
	// return s;
	// }

	/**
	 * Gzip the input string into a byte[].
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] zipStringToBytes(String input) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
		bufos.write(input.getBytes());
		bufos.close();
		byte[] retval = bos.toByteArray();
		bos.close();
		return retval;
	}

	/**
	 * Unzip a string out of the given gzipped byte array.
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static String unzipStringFromBytes(byte[] bytes) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		BufferedInputStream bufis = new BufferedInputStream(new GZIPInputStream(bis));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		while ((len = bufis.read(buf)) > 0) {
			bos.write(buf, 0, len);
		}
		String retval = bos.toString("UTF8");
		bis.close();
		bufis.close();
		bos.close();
		return retval;
	}

	/**
	 * Decode a UTF8 and base64 encoded string in to its original unicode form.
	 * 
	 * @param base64
	 * @return
	 * @throws IOException
	 */
	// public static String asStringFromBase64Utf8(String base64) throws
	// IOException {
	// byte buf[] = Base64.decode(base64);
	// return new String(buf, "UTF8");
	// }

	// /**
	// * Decode a zipped base64 encoded string in to its original unicode form.
	// *
	// * @param base64
	// * @return
	// * @throws IOException
	// */
	// public static String asStringFromBase64Zip(String base64) throws
	// IOException {
	// byte buf[] = Base64.decode(base64);
	// return unzipStringFromBytes(buf);
	// // string -> utf8 bytes -> zip string -> base64
	// // base64 -> zip string -> string
	// }

	public static String asRTF(String text) {
		String rtf = "";
		for (int i = 0; i < text.length(); i++) {
			Character c = text.charAt(i);
			rtf += (c > 127) ? "\\u" + (int) c + "?" : text.charAt(i);
		}
		return rtf;
	}

	public static String asGenieId(String text) {
		String id = asCleanString(text, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "");
		return id;
	}

	public static String asCleanString(String text) {
		return asCleanString(text, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "");
	}

	public static String asCleanString(String text, String legalStartCharacters, String validSpecialCharacters) {
		String id = asCleanString(text, validSpecialCharacters);
		if (legalStartCharacters.indexOf(id.substring(0, 1)) < 0) {
			id = "x" + id;
		}
		return id;
	}

	public static String asCleanString(String text, String validSpecialCharacters, char alternativeCharacter) {
		String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890" + validSpecialCharacters;
		String clean = "";
		boolean capital = false;
		for (int i = 0; i < text.length(); i++) {
			if (valid.indexOf(text.charAt(i)) < 0) {
				capital = true;
				clean += alternativeCharacter;
			} else {
				String ch = "" + text.charAt(i);
				if (capital) {
					// ch = ch.toUpperCase();
					capital = false;
				}
				clean += ch;
			}
		}
		return clean;
	}

	public static String asCleanString(String text, String validSpecialCharacters) {
		String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890" + validSpecialCharacters;
		String clean = "";
		boolean capital = false;
		for (int i = 0; i < text.length(); i++) {
			if (valid.indexOf(text.charAt(i)) < 0) {
				capital = true;
			} else {
				String ch = "" + text.charAt(i);
				if (capital) {
					// ch = ch.toUpperCase();
					capital = false;
				}
				clean += ch;
			}
		}
		return clean;
	}

	public static String asCleanStringPlus(String text) {
		String clean = "";
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (Character.isDigit(c) || Character.isLetter(c) || c == ' ' || c == '?' || c == '-' || c == '\''
					|| c == '"' || c == '&') {
				clean += c;
			}
		}
		return clean;
	}

	static public String asCapitalizedString(String text) {
		String wordbreaks = " ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String capitalized = "";
		for (int i = 0; i < text.length(); i++) {
			if (wordbreaks.indexOf(text.charAt(i)) >= 0 && capitalized.length() > 0
					&& capitalized.charAt(capitalized.length() - 1) != '_') {
				capitalized += "_";
			}
			String ch = "" + text.charAt(i);
			capitalized += ch.toUpperCase();
		}
		return capitalized;
	}

	static public String asCamelCase(String text) {
		return asCamelCase(text, "", false);
	}

	static public String asCamelCase(String text, boolean startWithCapital) {
		return asCamelCase(text, "", startWithCapital);
	}

	static public String asCamelCase(String text, String validSpecialCharacters) {
		return asCamelCase(text, validSpecialCharacters, false);
	}

	static public String asCamelCase(String text, String validSpecialCharacters, boolean startWithCapital) {
		String valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890" + validSpecialCharacters;
		String clean = "";
		boolean capital = startWithCapital;
		for (int i = 0; i < text.length(); i++) {
			if (valid.indexOf(text.charAt(i)) < 0) {
				capital = true;
			} else {
				String ch = "" + text.charAt(i);
				if (capital) {
					ch = ch.toUpperCase();
					capital = false;
				}
				clean += ch;
			}
		}
		return clean;
	}

	public static String getOrdinalSpelling(int number) {
		return number % 100 == 11 || number % 100 == 12 || number % 100 == 13 ? number + "th"
				: number + new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" }[number % 10];
	}

	public static String getTimeStamp() {
		return new Date().toString();
	}

	public static String getTimeStamp(String format) {
		// yyyyy.MMMMM.dd GGG hh:mm
		// yyyyMMdd-HHmm
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

	public static String getFilenameShort(File file) throws IOException {
		return file.getCanonicalPath().substring(0, file.getCanonicalPath().lastIndexOf('.'));
	}

	public static String getFilenameBasedOn(File file, String postfix, String extension) throws IOException {
		return file.getCanonicalPath().substring(0, file.getCanonicalPath().lastIndexOf('.')) + postfix + extension;
	}

	public static String getFilenameBasedOn(File file, String extension) throws IOException {
		return file.getCanonicalPath().substring(0, file.getCanonicalPath().lastIndexOf('.')) + extension;
	}

	public static String getFilenameBasedOn(File file, Date timestamp, String extension) throws IOException {
		return file.getCanonicalPath().substring(0, file.getCanonicalPath().lastIndexOf('.')) + " "
				+ DateYYYYMMddHHmm.format(timestamp) + extension;
	}

	public static String getFilenameBasedOn(String prefix, Date timestamp, String postfix) throws IOException {
		return prefix + " " + DateYYYYMMddHHmm.format(timestamp) + postfix;
	}

	public static File getFileBasedOn(File file, String postfix, String extension) throws IOException {
		String name = file.getCanonicalPath().substring(0, file.getCanonicalPath().lastIndexOf('.')) + postfix
				+ extension;
		name = name.replace("\\.\\.", "\\.");
		return new File(name);
	}

	public static File getFileBasedOn(String prefix, File file, String extension) throws IOException {
		String name = file.getName().substring(0, file.getName().lastIndexOf('.')) + extension;
		name = prefix + name.replace("\\.\\.", "\\.");
		return new File(file.getParentFile(), name);
	}

	public static String getFilenameBasedOn(String filename, String extension) throws IOException {
		int n = filename.lastIndexOf('.');
		if (n < 0)
			n = filename.length();
		String name = filename.substring(0, n) + extension;
		name = name.replace("\\.\\.", "\\.");
		return name;
	}

	public static File getFileBasedOn(File file, String extension) throws IOException {
		int n = file.getCanonicalPath().lastIndexOf('.');
		if (n < 0)
			n = file.getCanonicalPath().length();
		String name = file.getCanonicalPath().substring(0, n) + extension;
		name = name.replace("\\.\\.", "\\.");
		return new File(name);
	}

	public static File getFileBasedOn(File file, Date timestamp, String extension) throws IOException {
		String name = file.getCanonicalPath().substring(0, file.getCanonicalPath().lastIndexOf('.'))
				+ DateYYYYMMddHHmm.format(timestamp) + extension;
		name = name.replace("\\.\\.", "\\.");
		return new File(name);
	}

	public static File getFileBasedOnDate(File file, String dateformat) throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat(dateformat);
		String name = getFilenameOnly(file) + formatter.format(new Date()) + getFilenameExtension(file);
		name = name.replace("\\.\\.", "\\.");
		return new File(name);
	}

	public static File getFileBasedOnDate(File file, String dateformat, String extension) throws IOException {
		File datedFile = getFileBasedOnDate(file, dateformat);
		return getFileBasedOn(datedFile, extension);
	}

	public static File getFileBasedOn(String prefix, Date timestamp, String postfix) throws IOException {
		String name = getFilenameBasedOn(prefix, timestamp, postfix);
		name = name.replace("\\.\\.", "\\.");
		return new File(name);
	}

	public static String getFilenameExtension(File file) {
		return file.getName().substring(file.getName().lastIndexOf('.'));
	}

	public static String getFilenameOnly(File file) {
		int i = file.getName().lastIndexOf('.');
		if (i < 0)
			i = file.getName().length();
		return file.getName().substring(0, i);
	}

	public static String getFilenameOnlyNoDots(File file) {
		int i = file.getName().indexOf('.');
		if (i < 0)
			i = file.getName().length();
		return file.getName().substring(0, i);
	}

	public static String getQuotation(String line) {
		return getTextBetween(getTextBetweenBrackets(line), '"', '"');
	}

	public static String getTextBetweenBrackets(String line) {
		line = getTextBetween(line, '{', '}');
		line = getTextBetween(line, '(', ')');
		return line;
	}

	public static String getTextBetween(String line, char left, char right) {
		if (line.indexOf(left) >= 0 && line.lastIndexOf(right) > line.indexOf(left))
			return line.substring(line.indexOf(left) + 1, line.lastIndexOf(right));
		else
			return line;
	}

	public static String[] getQuotedStringArray(String line) {
		return getQuotation(line).split("\"[^\"]*\"");
	}

	/*
	 * public static void continueToLine(BufferedReader dis, String pattern) throws
	 * Exception { String line = ""; while (!line.startsWith("}")) { line =
	 * dis.readLine().trim(); } }
	 */

	public static String asQuotedString(String values[], String delimiter) {
		String text = "";
		for (int i = 0; i < values.length; i++) {
			text = text.concat('"' + values[i] + '"');
			if (i != values.length - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	public static String asNumeratedString(String values[], String delimiter) {
		String text = "";
		for (int i = 0; i < values.length; i++) {
			text = text.concat("(" + i + ")" + values[i]);
			if (i != values.length - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	public static String asStringFilled(char character, int repeat) {
		String text = "";
		for (int i = 0; i < repeat; i++) {
			text += character;
		}
		return text;
	}

	public static String asStringTrimmed(String values[], String delimiter) {
		String text = "";
		for (int i = 0; i < values.length; i++) {
			text = text.concat(values[i].trim());
			if (i != values.length - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	@SuppressWarnings("rawtypes")
	public static String asString(List values, String delimiter) {
		String text = "";
		for (int i = 0; i < values.size(); i++) {
			text = text.concat(values.get(i).toString());
			if (i != values.size() - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	@SuppressWarnings("rawtypes")
	public static String asString(List values, String delimiter, java.text.DecimalFormat formatter) {
		String text = "";
		for (Object value : values) {
			if (text.length() > 0)
				text = text.concat(delimiter);
			text = text.concat(formatter.format(value));
		}
		return text;
	}

	public static String asString(String values[], String delimiter) {
		String text = "";
		for (int i = 0; i < values.length; i++) {
			text = text.concat(values[i]);
			if (i != values.length - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	public static String asString(double values[], String delimiter) {
		String text = "";
		for (int i = 0; i < values.length; i++) {
			text = text.concat(Double.toString(values[i]));
			if (i != values.length - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	public static String asString(float values[], String delimiter) {
		String text = "";
		for (int i = 0; i < values.length; i++) {
			text = text.concat(Float.toString(values[i]));
			if (i != values.length - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	public static String asString(int values[], String delimiter) {
		String text = "";
		for (int i = 0; i < values.length; i++) {
			text = text.concat(Integer.toString(values[i]));
			if (i != values.length - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	public static String asString(Collection<Object> values, String delimiter, String andRepresentation,
			String emptyRepresentation) {
		String text = "";
		if (values.isEmpty())
			return emptyRepresentation;
		int i = 0;
		for (Object value : values) {
			if (i > 0 && i <= values.size() - 2)
				text += delimiter;
			text = text.concat(value.toString());
			if (i == values.size() - 2)
				text += andRepresentation;
			i++;
		}
		return text;
	}

	public static String asString(List<Object> values, String delimiter, String andRepresentation,
			String emptyRepresentation) {
		String text = "";
		if (values.isEmpty())
			return emptyRepresentation;
		for (int i = 0; i < values.size(); i++) {
			if (i > 0 && i <= values.size() - 2)
				text += delimiter;
			text = text.concat(values.get(i).toString());
			if (i == values.size() - 2)
				text += andRepresentation;
		}
		return text;
	}

	public static String asString(Set<?> values, String delimiter, String andRepresentation,
			String emptyRepresentation) {
		String text = "";
		if (values.isEmpty())
			return emptyRepresentation;
		int i = 0;
		for (Object value : values) {
			if (i > 0 && i <= values.size() - 2)
				text += delimiter;
			text = text.concat(value.toString());
			if (i == values.size() - 2)
				text += andRepresentation;
			i++;
		}
		return text;
	}

	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// public static String asString(Set<String> values, String delimiter, String
	// andRepresentation, String emptyRepresentation) {
	// return asString(new ArrayList(values), delimiter, andRepresentation,
	// emptyRepresentation);
	// }

	public static String asStringFormatted(DecimalFormat formatter, List<Double> values, String delimiter) {
		String text = "";
		for (int i = 0; i < values.size(); i++) {
			text = text.concat(formatter.format(values.get(i)));
			if (i != values.size() - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	public static String asStringFormatted(DecimalFormat formatter, double values[], String delimiter) {
		String text = "";
		for (int i = 0; i < values.length; i++) {
			text = text.concat(formatter.format(values[i]));
			if (i != values.length - 1)
				text = text.concat(delimiter);
		}
		return text;
	}

	public static String asStringFormatted(Date date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	// public static String asString(Collection<Object> values, String delimiter) {
	// Vector<Object> list = new Vector<Object>(values);
	// String text = "";
	// for (int i = 0; i < list.size(); i++) {
	// text = text.concat(list.get(i).toString());
	// if (i != list.size() - 1)
	// text = text.concat(delimiter);
	// }
	// return text;
	// }

	// public static String asString(Collection<Object> values, String delimiter,
	// String empty) {
	// return values.isEmpty() ? empty : asString(values, delimiter);
	// }

	public static String asHTML(Map<Object, Object> map, boolean htmlHeader) {
		String html = "<table>";
		for (Object key : map.keySet()) {
			html += "<tr><td>" + asString(key) + "</td><td>" + asString(map.get(key)) + "</td></tr>";
		}
		html += "</table>";
		return htmlHeader ? "<html>" + html + "</html>" : html;
	}

	public static String toHTMLColor(Color c) {
		StringBuilder sb = new StringBuilder("#");
		if (c.getRed() < 16)
			sb.append('0');
		sb.append(Integer.toHexString(c.getRed()));
		if (c.getGreen() < 16)
			sb.append('0');
		sb.append(Integer.toHexString(c.getGreen()));
		if (c.getBlue() < 16)
			sb.append('0');
		sb.append(Integer.toHexString(c.getBlue()));
		return sb.toString();
	}

	public static String asString(Object object) {
		return asString(object, ", ");
	}

	@SuppressWarnings("unchecked")
	public static String asString(Object object, String delimiter) {
		useDotDecimalMark();
		if (object instanceof BigDecimal)
			return CommaFormat2.format(object);
		// if (object instanceof SourceName)
		// return object.toString();
		// if (object instanceof ModelImplementation)
		// return ((ModelImplementation) object).getModelVersion().toString();
		if (object instanceof Double)
			return CommaFormat2.format((Double) object);
		if (object instanceof Integer)
			return Integer.toString((Integer) object);
		if (object instanceof TreeSet) {
			String text = "";
			Vector<Object> list = new Vector<Object>((TreeSet<Object>) object);
			for (int i = 0; i < list.size(); i++) {
				text = text.concat(asString(list.get(i)));
				if (i != list.size() - 1)
					text = text.concat(delimiter);
			}
			return text;
		}
		if (object instanceof String[]) {
			String text = "";
			List<Object> list = (List<Object>) object;
			for (int i = 0; i < list.size(); i++) {
				text = text.concat(asString(list.get(i)));
				if (i != list.size() - 1)
					text = text.concat(delimiter);
			}
			return text;
		}
		if (object instanceof Map)
			return asString((Map<Object, Object>) object);
		if (object instanceof List) {
			String text = "";
			List<Object> list = (List<Object>) object;
			for (int i = 0; i < list.size(); i++) {
				text = text.concat(asString(list.get(i)));
				if (i != list.size() - 1)
					text = text.concat(delimiter);
			}
			return "{" + text + "}";
		}
		return object.toString();
	}

	public static String asString(Map<Object, Object> map) {
		String text = "";
		for (Object key : map.keySet()) {
			if (!text.isEmpty())
				text += "; ";
			text += asString(key) + " = " + asString(map.get(key));
		}
		return "[" + text + "]";
	}

	public static void printMap(Map<Object, Object> map) {
		for (Object key : map.keySet()) {
			System.out.println(asString(key) + " = " + asString(map.get(key)));
		}
	}

	public static String addSpace(String var, int length) {
		String text = var;
		for (int i = var.length(); i < length; i++)
			text += " ";
		return text;
	}

	public static String addSpace(String var, String separatorPattern, int... length) {
		String text[] = var.split(separatorPattern);
		String respons = "";
		for (int i = 0; i < text.length; i++)
			respons += addSpace(text[i], length[Math.min(i, length.length - 1)]);
		return respons;
	}

	public static String addSpaceLeft(String var, int length) {
		String text = "";
		for (int i = 0; i < length - var.length(); i++)
			text += " ";
		return text + var;
	}

	public static String addText(String first, String middle, String last, int times) {
		String text = first;
		for (int i = 1; i < times - 1; i++)
			text += middle;
		text += last;
		return text;
	}

	/**
	 * 
	 * Special Characters
	 * 
	 * There are ten keyboard characters which have special meaning in LaTeX, and
	 * cannot be used on their own except for these purposes. This method translates
	 * those characters to a latex printable format.
	 */
	public static String asLatexString(String text) {
		String latex = text;
		latex = latex.replaceAll("\\\\", "\\\\textbackslash "); // \ The command
		// character
		// \textbackslash
		latex = latex.replaceAll("\\$", "\\\\\\$"); // $ Math typesetting
		// delimiter \$
		latex = latex.replaceAll("\\%", "\\\\\\%"); // % The comment character
		// \%
		latex = latex.replaceAll("\\^", "\\\\\\^"); // ^ Math superscript
		// character \^
		latex = latex.replaceAll("\\&", "\\\\\\&"); // & Tabular column
		// separator \&
		latex = latex.replaceAll("\\_", "\\\\\\_"); // _ Math superscript
		// character \_
		latex = latex.replaceAll("\\~", "\\*\\~\\*"); // ~ Actual tilda
		// character *~*
		latex = latex.replaceAll("\\#", "\\\\\\#"); // # Macro parameter symbol
		// \#
		latex = latex.replaceAll("\\{", "\\\\\\{"); // { Argument start
		// delimiter \{
		latex = latex.replaceAll("\\}", "\\\\\\}"); // } Argument end delimiter
		// \}
		// latex = latex.replaceAll("\\ï¿½", "$\neg$"); // personal negationn
		// latex = latex.replaceAll("\\{", "\\$\\\\\\{\\$"); // { Argument start
		// delimiter $\{$
		// latex = latex.replaceAll("\\}", "\\$\\\\\\}\\$"); // } Argument end
		// delimiter $\}$
		latex = latex.replaceAll("\\Î”", "{\\\\Delta}"); // Delta
		latex = latex.replaceAll("\\Î´", "{\\\\delta}"); // delta
		latex = latex.replaceAll("\\Â¬", "{\\\\lnot}"); // negation
		latex = latex.replaceAll("\\*", "\\times"); // # Multiply symbol
		return latex;
	}

	public static Vector<String> getAllBetween(String text, char left, char right, int depth) {
		int d[] = new int[text.length()];
		int currentDepth = 0;
		int iLeft = 0;
		int iRight = 0;
		Vector<String> list = new Vector<String>();
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == left) {
				d[i] = ++currentDepth;
				if (currentDepth == depth)
					iLeft = i;
			} else if (text.charAt(i) == right) {
				if (currentDepth == depth) {
					iRight = i;
					list.add(text.substring(iLeft, iRight + 1));
				}
				d[i] = currentDepth--;
			}
		}
		return list;
	}
	// private static int skipAllBetween_(String text, String left, String right) {
	//// int iLeft = text.indexOf(left) + left.length();
	//// if (iLeft < 0)
	//// return all;
	//// int iRight = text.indexOf(right, iLeft + 1);
	//// if (iRight < 0)
	//// return all;
	//// int jLeft = text.indexOf(left, iLeft + 1);
	//// if (iRight > jLeft) {
	//// getAllBetween(text.substring(iLeft), left, right);
	//// }
	//// all.add(text.substring(iLeft, iRight));
	// }

	public static String getBetween(String text, String left, String right) {
		int i = text.indexOf(left) + left.length();
		if (i < 0)
			return "";
		int j = text.indexOf(right, i + 1);
		if (j < 0 || j < i)
			return "";
		return text.substring(i, j);
	}

	public static String getBetween(String text, char left, char right) {
		int i = text.indexOf(left) + 1;
		if (i < 0)
			return "";
		int j = text.indexOf(right, i + 1);
		if (j < 0 || j < i)
			return "";
		return text.substring(i, j);
	}

	public static String getBetweenOuter(String text, String left, String right) {
		int i = text.indexOf(left) + left.length();
		if (i < 0)
			return "";
		int j = text.lastIndexOf(right);
		if (j < 0 || j < i)
			return "";
		return text.substring(i, j);
	}

	public static String getBetweenOuter(String text, char left, char right) {
		int i = text.indexOf(left) + 1;
		if (i < 0)
			return "";
		int j = text.lastIndexOf(right);
		if (j < 0 || j < i)
			return "";
		return text.substring(i, j);
	}

	public static String loadText(String filename) {
		return loadText(new File(filename));
	}

	// public static String loadText(File file) {
	// try {
	// // System.out.println("# loading: " + file.getCanonicalPath());
	// InputStreamReader isr = new InputStreamReader(new FileInputStream(file),
	// "UTF-8");
	// BufferedReader in = new BufferedReader(isr);
	// String text = "";
	// while (in.ready()) {
	// String line = in.readLine();
	// text = text.concat(line);
	// text = text.concat("\r\n");
	// }
	// in.close();
	// return text;
	// } catch (Exception e) {
	// e.toString();
	// return "";
	// }
	// }

	public static void saveText(File file, String string) {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(string);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	public static String loadText(File file) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			String text = readerToString(in);
			in.close();
			return text;
		} catch (Exception e) {
			return e.toString();
		}
	}

	public static String loadTextFastUTF8(File f) throws IOException {
		return loadTextFast(f, "UTF8");
	}

	public static String loadTextFast(File f, String charsetName) throws IOException {
		if (f.length() > Runtime.getRuntime().freeMemory()) {
			throw new IOException("Not enough memory to load " + f.getAbsolutePath());
		}
		byte[] data = new byte[(int) f.length()];
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
		in.readFully(data);
		in.close();
		return new String(data, charsetName);
	}

	public static String getTextFromURL(String urlString) throws URISyntaxException, IOException {
		URL website = new URI(urlString).toURL();
		URLConnection connection = website.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String content = readerToString(in);
		in.close();
		return content;
	}

	public static String readerToString(Reader is) throws IOException {
		StringBuffer sb = new StringBuffer();
		char[] b = new char[1024];
		int n;
		while ((n = is.read(b)) > 0) {
			sb.append(b, 0, n);
		}
		return sb.toString();
	}

	public static String inputStreamToString(InputStream is) throws IOException {
		return readerToString(new BufferedReader(new InputStreamReader(is)));
	}

	public static String asString(double p[], DecimalFormat formatter) {
		String s = "";
		for (int i = 0; i < p.length; i++) {
			s = s.concat(formatter.format(p[i]));
			if (i < p.length - 1)
				s = s.concat(",");
		}
		return s;
	}

	public static String asString(double p[]) {
		String s = "";
		for (int i = 0; i < p.length; i++) {
			s = s.concat(Double.toString(p[i]));
			if (i < p.length - 1)
				s = s.concat(",");
		}
		return s;
	}

	public static String asPrettyString(DecimalFormat formatter, double p[]) {
		String s = "";
		for (int i = 0; i < p.length; i++) {
			s = s.concat(formatter.format(p[i]));
			if (i < p.length - 1)
				s = s.concat(" ");
		}
		return s;
	}

	public static double[] asPrettyArray(DecimalFormat formatter, String array) {
		String strings[] = array.split("\\s");
		double doubles[] = new double[strings.length];
		for (int i = 0; i < strings.length; i++) {
			try {
				doubles[i] = formatter.parse(strings[i]).doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return doubles;
	}

	public static String asPrettyString(double p[]) {
		String s = "";
		for (int i = 0; i < p.length; i++) {
			s = s.concat(DotCommaFormat6.format(p[i]));
			if (i < p.length - 1)
				s = s.concat(" ");
		}
		return s;
	}

	public static String asPrettySmallString(double p[]) {
		String s = "";
		for (int i = 0; i < p.length; i++) {
			s = s.concat(CommaFormat3.format(p[i]));
			if (i < p.length - 1)
				s = s.concat(" ");
		}
		return s;
	}

	public static String asString(int p[]) {
		String s = "";
		for (int i = 0; i < p.length; i++) {
			s = s.concat(Integer.toString(p[i]));
			if (i < p.length - 1)
				s = s.concat(",");
		}
		return s;
	}

	public static String asString(Object t[], String separator) {
		String s = "";
		for (int i = 0; i < t.length; i++) {
			s = s.concat("" + (t[i] == null ? "null" : t[i].toString()) + "");
			if (i < t.length - 1)
				s = s.concat(separator);
		}
		return s;
	}

	public static String asString(Object t[]) {
		String s = "";
		for (int i = 0; i < t.length; i++) {
			s = s.concat("" + (t[i] == null ? "null" : t[i].toString()) + "");
			if (i < t.length - 1)
				s = s.concat(", ");
		}
		return s;
	}

	public static String asString(int t[][]) {
		String s = "";
		for (int i = 0; i < t.length; i++) {
			s = s.concat("[");
			for (int j = 0; j < t[i].length; j++) {
				s = s.concat("" + t[i][j] + "");
				if (j < t[i].length - 1)
					s = s.concat(", ");
			}
			s = s.concat("]");
		}
		return s;
	}

	public static String asStringHeader(Object t[]) {
		String s = "";
		for (int i = 0; i < t.length; i++) {
			s = s.concat(i + ":\"" + t[i].toString() + "\"");
			if (i < t.length - 1)
				s = s.concat("; ");
		}
		return s;
	}

	public static int[] asIntegerArray(Vector<Object> v) {
		int array[] = new int[v.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = ((Integer) v.get(i)).intValue();
		}
		return array;
	}

//	@SuppressWarnings("unused")
//	public static void main(String args[]) {
//
//		String csv = "1566469974,1566169200,1566172800,69,519,16393,\"ANN_SENSOR_v0.1\",,,,,,,,,\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\",,\"\",\"\",\"\"";
//		String csv_[] = StringTool.splitCSV(csv);
//		String csv__[] = csv.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//		System.out.println(StringTool.asString(csv_));
//		System.out.println(StringTool.asString(csv__));
//
//		String csv3 = "test,test,\"test\\\"test\",test";
//		String csv3_[] = StringTool.splitCSV(csv3);
//		String csv1 = "test,test,\"test\"";
//		String csv1_[] = StringTool.splitCSV(csv1);
//		String csv2 = "test,\"test\",test";
//		String csv2_[] = StringTool.splitCSV(csv2);
//
//		// return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//
//		String json = "{\"MonitorID\":0,\"monitorName\":\"InsightsEngineRunner-Staging\",\"runFrequencySeconds\":86400,\"alertEnabled\":false,\"slackUsers\":\"@siccop\",\"lastRunFinishEpoch\":0,\"lastRunDuration\":0}";
//
//		System.out.println(StringTool.setJSONAttribute(json, "monitorName", 123));
//		System.out.println(StringTool.setJSONAttribute(json, "monitorName", 123.0));
//		System.out.println(StringTool.setJSONAttribute(json, "monitorName", false));
//		System.out.println(StringTool.setJSONAttribute(json, "monitorName", "1231s"));
//
//		System.out.println(StringTool.removeJSONField(json, "runFrequencySeconds"));
//		System.out.println(StringTool.removeJSONField(json, "slackUsers"));
//		System.out.println(StringTool.removeJSONField(json, "MonitorID"));
//		System.out.println(StringTool.removeJSONField(json, "monitorID"));
//		System.out.println(StringTool.removeJSONField(json, "lastRunDuration"));
//
//		System.out.println(mergeLists("TreeSet((a, b,c))", "TreeSet((c,d ,e))"));
//		System.out.println(asString(getAllBetween("{sd:{d,{s,w},{x}},{q}}", '{', '}', 1), " | "));
//		System.out.println(asString(getAllBetween("{sd:{d,{s,w},{x}},{q}}", '{', '}', 2), " | "));
//		System.out.println(asString(getAllBetween("{sd:{d,{s,w},{x}},{q}}", '{', '}', 3), " | "));
//		System.out.println(asString(getAllBetween("{sd:{d,{s,w},{x}},{q}}", '{', '}', 4), " | "));
//		// String test = "TÑ�st Ã¶Ã´! GroenLinks en D66 willen serieus werk maken van
//		// een paars plus-coalitie.";
//		// try {
//		// System.out.println("test:");
//		// System.out.println(test);
//		// System.out.println();
//		// System.out.println("asRTF(test):");
//		// System.out.println(asRTF(test));
//		// System.out.println();
//		// // System.out.println("asBase64Utf8(test):");
//		// // System.out.println(asBase64Utf8(test));
//		// // System.out.println();
//		// // System.out.println("asStringFromBase64Utf8(asBase64Utf8(test)):");
//		// // System.out.println(asStringFromBase64Utf8(asBase64Utf8(test)));
//		// } catch (Exception e) {
//		// e.printStackTrace();
//		// }
//
//		System.out.println(asString(splitJSON(json)[0]));
//		System.out.println(asString(splitJSON(json)[1]));
//		json = "{\"EpochStart\": 1559910600,\"EpochEnd\": 1559910900,\"FarmID\": 36,\"HerdGroupID\": 411,\"DairyCowID\": 8263,\"Source\": \"ANNv0.3\",\"CowStatus\": \"pregnant\",\"CompletenessRatioLast5Minutes\": 0.957633,\"CompletenessRatioLast30Minutes\": 0.159606,\"CompletenessRatioLastHour\": 0.079803,\"CompletenessRatioLast2Hours\": 0.039901,\"CompletenessRatioLast3Hours\": 0.026601,\"CompletenessRatioLast6Hours\": 0.013300,\"CompletenessRatioLast12Hours\": 0.006650,\"CompletenessRatioLast24Hours\": 0.003325,\"WalkingDurationLast5Minutes\": 6.160000,\"WalkingDurationLast30Minutes\": 6.160000,\"WalkingDurationLastHour\": 6.160000,\"WalkingDurationLast2Hours\": 6.160000,\"WalkingDurationLast3Hours\": 6.160000,\"WalkingDurationLast6Hours\": 6.160000,\"WalkingDurationLast12Hours\": 6.160000,\"WalkingDurationLast24Hours\": 6.160000,\"EatingDurationLast5Minutes\": 6.160000,\"EatingDurationLast30Minutes\": 6.160000,\"EatingDurationLastHour\": 6.160000,\"EatingDurationLast2Hours\": 6.160000,\"EatingDurationLast3Hours\": 6.160000,\"EatingDurationLast6Hours\": 6.160000,\"EatingDurationLast12Hours\": 6.160000,\"EatingDurationLast24Hours\": 6.160000,\"RuminatingDurationLast5Minutes\": 6.160000,\"RuminatingDurationLast30Minutes\": 6.160000,\"RuminatingDurationLastHour\": 6.160000,\"RuminatingDurationLast2Hours\": 6.160000,\"RuminatingDurationLast3Hours\": 6.160000,\"RuminatingDurationLast6Hours\": 6.160000,\"RuminatingDurationLast12Hours\": 6.160000,\"RuminatingDurationLast24Hours\": 6.160000,\"DrinkingDurationLast5Minutes\": 0.000000,\"DrinkingDurationLast30Minutes\": 0.000000,\"DrinkingDurationLastHour\": 0.000000,\"DrinkingDurationLast2Hours\": 0.000000,\"DrinkingDurationLast3Hours\": 0.000000,\"DrinkingDurationLast6Hours\": 0.000000,\"DrinkingDurationLast12Hours\": 0.000000,\"DrinkingDurationLast24Hours\": 0.000000,\"IdleDurationLast5Minutes\": 0.000000,\"IdleDurationLast30Minutes\": 0.000000,\"IdleDurationLastHour\": 0.000000,\"IdleDurationLast2Hours\": 0.000000,\"IdleDurationLast3Hours\": 0.000000,\"IdleDurationLast6Hours\": 0.000000,\"IdleDurationLast12Hours\": 0.000000,\"IdleDurationLast24Hours\": 85.500000,\"StandingDurationLast5Minutes\": 74.010000,\"StandingDurationLast30Minutes\": 74.010000,\"StandingDurationLastHour\": 74.010000,\"StandingDurationLast2Hours\": 74.010000,\"StandingDurationLast3Hours\": 74.010000,\"StandingDurationLast6Hours\": 74.010000,\"StandingDurationLast12Hours\": 74.010000,\"StandingDurationLast24Hours\": 74.010000,\"LayingDurationLast5Minutes\": 204.390000,\"LayingDurationLast30Minutes\": 204.390000,\"LayingDurationLastHour\": 204.390000,\"LayingDurationLast2Hours\": 204.390000,\"LayingDurationLast3Hours\": 204.390000,\"LayingDurationLast6Hours\": 204.390000,\"LayingDurationLast12Hours\": 204.390000,\"LayingDurationLast24Hours\": 204.390000,\"ActivityCount\": 0,\"StandingUpCountLastHour\": 0,\"LayingDownCountLastHour\": 0,\"TransitionMatrix\": \"{Walking/Standing/60/1;Standing/Walking/60/1;Standing/Laying/60/7;Standing/Gap/60/1;Eating/Other/60/1;Laying/Standing/60/7;Laying/Gap/60/1;Other/Ruminating/60/1;Other/Gap/60/2;Ruminating/Other/60/1;Gap/Laying/60/2;Gap/Other/60/2}\",\"Meals\": \"\",\"MealCount\": 0,\"CreatedEpoch\": 1560724328}";
//		System.out.println(asString(splitJSON(json)[0]));
//		System.out.println(asString(splitJSON(json)[1]));
//
//	}

	public static String mergeLists(String firstList, String secondList) {
		if (firstList.startsWith("TreeSet((") && firstList.endsWith("))")) {
			firstList = firstList.substring(9, firstList.length() - 2);
		}
		if (secondList.startsWith("TreeSet((") && secondList.endsWith("))")) {
			secondList = secondList.substring(9, secondList.length() - 2);
		}
		TreeSet<String> elements = new TreeSet<String>();
		for (String element : firstList.split("\\s*\\,\\s*"))
			if (!element.isEmpty())
				elements.add(element);
		for (String element : secondList.split("\\s*\\,\\s*"))
			if (!element.isEmpty())
				elements.add(element);
		String list = StringTool.asString(elements);
		return list;
	}

	public static String[] splitLines(String lines) {
		return lines.split("\\r\\n|\\n\\r|\\r|\\n", -1);
	}

	public static String indent(String text, int spaces) {
		String indentedText = "";
		for (String line : text.split("\\r\\n|\\n\\r|\\r|\\n", -1)) {
			indentedText += addSpaceLeft(line, spaces) + System.lineSeparator();
		}
		return indentedText;
	}

	public static void split(String line, char separator, String columns[]) {
		int i1 = 0;
		int i2 = line.indexOf(separator);
		for (int i = 0; i < columns.length; i++) {
			columns[i] = i1 >= 0 ? (i2 >= 0 ? line.substring(i1, i2) : line.substring(i1)) : "";
			i1 = i2 + 1;
			i2 = line.indexOf(separator, i1 + 1);
		}
	}

	public static String[] splitCSV(String line) {
		int lastIndex = 0;
		boolean withinQuote = false;
		boolean escapeChar = false;
		boolean firstChar = true;
		Vector<String> values = new Vector<String>();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == ',' && !withinQuote) {
				values.add(line.substring(lastIndex, i));
				lastIndex = i + 1;
				escapeChar = false;
				firstChar = true;
			} else if (c == '"' && !escapeChar) {
				withinQuote = !withinQuote;
				escapeChar = false;
			} else if (c == '\\' && !escapeChar) {
				escapeChar = true;
			} else {
				if (firstChar && c != ' ' && line.charAt(i) != '\t')
					firstChar = false;
				escapeChar = false;
			}
		}
		values.add(line.substring(lastIndex));
		return values.toArray(new String[values.size()]);
	}

	public static String[] splitCSV(String line, int columns) {
		int lastIndex = 0;
		boolean withinQuote = false;
		boolean escapeChar = false;
		boolean firstChar = true;
		String values[] = new String[columns];
		int columnIndex = 0;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == ',' && !withinQuote) {
				values[columnIndex++] = line.substring(lastIndex, i);
				lastIndex = i + 1;
				escapeChar = false;
				firstChar = true;
			} else if (c == '"' && !escapeChar) {
				withinQuote = !withinQuote;
				escapeChar = false;
			} else if (c == '\\' && !escapeChar) {
				escapeChar = true;
			} else {
				if (firstChar && c != ' ' && line.charAt(i) != '\t')
					firstChar = false;
				escapeChar = false;
			}
		}
		values[columnIndex] = line.substring(lastIndex);
		return values;
	}

	public static String[][] splitJSON(String line) {
		String cols[] = getBetweenOuter(line, '{', '}').split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
		String values[][] = new String[2][cols.length];
		for (int i = 0; i < cols.length; i++) {
			values[0][i] = getBetween(cols[i], '"', '"');
			values[1][i] = unqoute(cols[i].substring(cols[i].indexOf(':') + 1));
		}
		return values;
	}

	public static String unqoute(String text) {
		text = text.trim();
		if (text.charAt(0) == '"' && text.charAt(text.length() - 1) == '"')
			return text.substring(1, text.length() - 1);
		else
			return text;
	}

	@SuppressWarnings("rawtypes")
	public static Vector<Object> parseCSV(Class objectClass, String csv, String separator, String dateFormat)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ParseException {
		Vector<Object> objects = new Vector<Object>();
		SimpleDateFormat DateFormatter = new SimpleDateFormat(dateFormat); // for example: "yyyy-MMM-dd HH:mm:ss"
		DecimalFormat ZeroFormat = new java.text.DecimalFormat("0");
		String lines[] = csv.split("\\n");
		String header = lines[0].trim();
		String headers[] = header.split("\\,\\s*");
		for (int j = 1; j < lines.length; j++) {
			@SuppressWarnings("unchecked")
			Object object = (Object) objectClass.getDeclaredConstructor().newInstance();
			String line = lines[j].trim();
			String values[] = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			int n = Math.min(headers.length, values.length);
			for (int i = 0; i < n; i++) {
				String fieldName = headers[i];// StringTool.asCamelCase(headers[i]);
				Field field = null;
				try {
					field = objectClass.getDeclaredField(fieldName);
				} catch (Exception e1) {
					try {
						field = objectClass.getDeclaredField(fieldName.toLowerCase());
					} catch (Exception e2) {
						// ignore
					}
				}
				if (field != null) {
					if (values.length > i) {
						Object valueType;
						valueType = field.getType();
						if (valueType.equals(Double.class)) {
							field.set(object, Double.parseDouble(values[i]));
						}
						if (valueType.equals(Long.class)) {
							field.set(object, ZeroFormat.parse(values[i]));
						}
						if (valueType.equals(Integer.class)) {
							field.set(object, Integer.parseInt(values[i]));
						}
						if (valueType.equals(String.class)) {
							field.set(object, values[i]);
						}
						if (valueType.equals(Hashtable.class)) {
							String cleanValue = values[i].substring(1, values[i].length() - 1);
							String vals[] = cleanValue.split("\\;");
							Hashtable<String, Double> map = new Hashtable<String, Double>();
							for (String val : vals) {
								if (!val.isEmpty()) {
									try {
										String cols[] = val.split("\\=");
										map.put(cols[0], Double.parseDouble(cols[1]));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							field.set(object, map);
						}
						if (valueType instanceof Date) {
							field.set(object, DateFormatter.parse(values[i]));
						}
					}
				}
			}
			objects.add(object);
		}
		return objects;
	}

	public static boolean parseBoolean(String text) {
		String value = text.toLowerCase();
		if (value.toLowerCase().startsWith("observed"))
			return true;
		if (value.toLowerCase().startsWith("true"))
			return true;
		if (value.toLowerCase().startsWith("yes"))
			return true;
		if (value.toLowerCase().startsWith("valid"))
			return true;
		if (value.toLowerCase().startsWith("present"))
			return true;
		if (value.toLowerCase().startsWith("t"))
			return true;
		if (value.toLowerCase().startsWith("y"))
			return true;
		if (value.toLowerCase().startsWith("j"))
			return true;
		if (value.toLowerCase().startsWith("1"))
			return true;
		if (value.toLowerCase().startsWith("ja"))
			return true;
		if (value.toLowerCase().startsWith("waar"))
			return true;
		if (value.toLowerCase().startsWith("geldig"))
			return true;

		if (value.toLowerCase().startsWith("unobserved"))
			return false;
		if (value.toLowerCase().startsWith("false"))
			return false;
		if (value.toLowerCase().startsWith("no"))
			return false;
		if (value.toLowerCase().startsWith("invalid"))
			return false;
		if (value.toLowerCase().startsWith("absent"))
			return false;
		if (value.toLowerCase().startsWith("f"))
			return false;
		if (value.toLowerCase().startsWith("n"))
			return false;
		if (value.toLowerCase().startsWith("0"))
			return false;
		if (value.toLowerCase().startsWith("nee"))
			return false;
		if (value.toLowerCase().startsWith("onwaar"))
			return false;
		if (value.toLowerCase().startsWith("ongeldig"))
			return false;
		if (value.toLowerCase().startsWith("not"))
			return false;
		if (value.toLowerCase().startsWith("niet"))
			return false;
		if (value.toLowerCase().startsWith("no"))
			return false;
		return true;
		// throw new RuntimeException("String \"" + value + "\" can not be
		// translated into a boolean.");
	}

	public static Long asLong(Locale locale, String text) throws ParseException {
		Number localized = NumberFormat.getNumberInstance(locale).parse(text);
		return localized.longValue();
	}

	public static Double asDouble(Locale locale, String text) throws ParseException {
		Number localized = NumberFormat.getNumberInstance(locale).parse(text);
		return localized.doubleValue();
	}

	public static Number asNumber(Locale locale, String text) throws ParseException {
		return NumberFormat.getNumberInstance(locale).parse(text);
	}

	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;

	public static String asHumanFriendlySize(final long value) {
		final long[] dividers = new long[] { T, G, M, K, 1 };
		final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
		if (value == 0)
			return "0 Bytes";
		if (value < 0)
			throw new IllegalArgumentException("Invalid size: " + value);
		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				result = format(value, divider, units[i]);
				break;
			}
		}
		return result;
	}

	public static String asMbps(final long bits) {
		long Kb = 1000;
		long Mb = Kb * Kb;
		long Gb = Mb * Kb;
		long Tb = Gb * Kb;
		final long[] dividers = new long[] { Tb, Gb, Mb, Kb, 1 };
		final String[] units = new String[] { "Tbps", "Gbps", "Mbps", "Kbps", "bps" };
		if (bits == 0)
			return "0 b";
		if (bits < 0)
			throw new IllegalArgumentException("Invalid size: " + bits);
		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (bits >= divider) {
				result = format(bits, divider, units[i]);
				break;
			}
		}
		return result;
	}

	private static String format(final long value, final long divider, final String unit) {
		final double result = divider > 1 ? (double) value / (double) divider : (double) value;
		return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}

	public static BigInteger getMD5Int(String content) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(content.getBytes("UTF-8"), 0, content.length());
		return new BigInteger(1, m.digest());
	}

	public static String getMD5Hex(String content) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		return getMD5Int(content).toString(16);
	}

	public static byte[] base64Decode(String input) {
		if (input.length() % 4 == 0) {
			byte decoded[] = new byte[((input.length() * 3) / 4)
					- (input.indexOf('=') > 0 ? (input.length() - input.indexOf('=')) : 0)];
			char[] inChars = input.toCharArray();
			int j = 0;
			int b[] = new int[4];
			for (int i = 0; i < inChars.length; i += 4) {
				// This could be made faster (but more complicated) by precomputing these index
				// locations.
				b[0] = BASE64CODES.indexOf(inChars[i]);
				b[1] = BASE64CODES.indexOf(inChars[i + 1]);
				b[2] = BASE64CODES.indexOf(inChars[i + 2]);
				b[3] = BASE64CODES.indexOf(inChars[i + 3]);
				decoded[j++] = (byte) ((b[0] << 2) | (b[1] >> 4));
				if (b[2] < 64) {
					decoded[j++] = (byte) ((b[1] << 4) | (b[2] >> 2));
					if (b[3] < 64) {
						decoded[j++] = (byte) ((b[2] << 6) | b[3]);
					}
				}
			}
			return decoded;
		} else {
			throw new IllegalArgumentException("Invalid base64 input");
		}
	}

	public static String base64Encode(StringBuffer out, byte[] in) {
		int b;
		for (int i = 0; i < in.length; i += 3) {
			b = (in[i] & 0xFC) >> 2;
			out.append(BASE64CODES.charAt(b));
			b = (in[i] & 0x03) << 4;
			if (i + 1 < in.length) {
				b |= (in[i + 1] & 0xF0) >> 4;
				out.append(BASE64CODES.charAt(b));
				b = (in[i + 1] & 0x0F) << 2;
				if (i + 2 < in.length) {
					b |= (in[i + 2] & 0xC0) >> 6;
					out.append(BASE64CODES.charAt(b));
					b = in[i + 2] & 0x3F;
					out.append(BASE64CODES.charAt(b));
				} else {
					out.append(BASE64CODES.charAt(b));
					out.append('=');
				}
			} else {
				out.append(BASE64CODES.charAt(b));
				out.append("==");
			}
		}

		return out.toString();
	}

	public static String setJSONAttribute(String json, String field, Long attribute) {
		return setJSONAttribute_(json, field, attribute.toString());
	}

	public static String setJSONAttribute(String json, String field, Integer attribute) {
		return setJSONAttribute_(json, field, attribute.toString());
	}

	public static String setJSONAttribute(String json, String field, Double attribute) {
		return setJSONAttribute_(json, field, attribute.toString());
	}

	public static String setJSONAttribute(String json, String field, Boolean attribute) {
		return setJSONAttribute_(json, field, attribute ? "true" : "false");
	}

	public static String setJSONAttribute(String json, String field, String attribute) {
		return setJSONAttribute_(json, field, "\"" + attribute + "\"");
	}

	private static String setJSONAttribute_(String json, String field, String attribute) {
		String key = "\"" + field + "\":";
		int i = json.indexOf(key);
		int j1 = json.indexOf(",", i + 1);
		int j2 = json.indexOf("}", i + 1);
		int j = Math.min(j1, j2);
		if (j == -1)
			j = Math.max(j1, j2);
		String value = attribute;
		String newjson = json.substring(0, i + key.length()) + value + json.substring(j);
		return newjson;
	}

	public static String getJSONAttribute(String json, String field) {
		String key = "\"" + field + "\":";
		int i = json.indexOf(key);
		int j1 = json.indexOf(",", i + 1);
		int j2 = json.indexOf("}", i + 1);
		int j = Math.min(j1, j2);
		if (j == -1)
			j = Math.max(j1, j2);
		String value = json.substring(i + key.length(), j);
		if (value.trim().startsWith("\"") && value.trim().endsWith("\"")) {
			value = value.trim();
			value = value.substring(1, value.length() - 1);
		} else {
			value = value.trim();
		}
		return value;
	}

	public static String removeJSONField(String json, String field) {
		String key = "\"" + field + "\":";
		int i = json.indexOf(key);
		if (i >= 0) {
			int j1 = json.indexOf(",", i + 1);
			int j2 = json.indexOf("}", i + 1) - 1;
			int j = Math.min(j1, j2);
			if (j1 == -1) {
				j = Math.max(j1, j2);
				String left = json.substring(0, i - 1);
				String right = json.substring(j + 1);
				json = left + right;
			} else {
				if (j == -1)
					j = Math.max(j1, j2);
				String left = json.substring(0, i);
				String right = json.substring(j + 1);
				json = left + right;
			}
		}
		return json;
	}

	public static boolean contains(TreeSet<String> texts, String text) {
		for (String label : texts) {
			if (label.trim().toLowerCase().equals(text.trim().toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(Vector<String> texts, String text) {
		for (String label : texts) {
			if (label.trim().toLowerCase().equals(text.trim().toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static String getPercentage(double p) {
		return CommaFormat1.format((int) (p * 100)) + "%";
	}

	public static String getPercentage(int index, int num) {
		return getPercentage(index, num, ZeroFormat);
	}

	public static String getPercentage(int index, int num, NumberFormat format) {
		double p = (double) index / (double) num;
		return format.format((int) (p * 100));
	}

	public static boolean contains(String texts[], String text) {
		for (String label : texts) {
			if (label.trim().toLowerCase().equals(text.trim().toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Integer> getIndices(String str, char delimiter) {
		ArrayList<Integer> indicesList = new ArrayList<>();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == delimiter) {
				indicesList.add(i);
			}
		}
		return indicesList;
	}

	private static void getMaximumDistanceBetweenDelimiters(String str, char delimiter, ArrayList<Integer> indices) {
		int index = 0;
		int lastPosition = 0;
		for (int position = 0; position < str.length(); position++) {
			boolean endOfLine = position + 1 == str.length();
			if (str.charAt(position) == delimiter || endOfLine) {
				if (endOfLine)
					position++;
				int width = position - lastPosition;
				if (index > indices.size() - 1) {
					indices.add(width);
					lastPosition = position + 1;
					index++;
				} else {
					indices.set(index, Math.max(width, indices.get(index++)));
					lastPosition = position + 1;
				}
			}
		}
	}

	public static String alignString(String text, char delimiter, boolean showDelimiter) {
		String[] lines = StringTool.splitLines(text);
		ArrayList<Integer> width = new ArrayList<Integer>();
		for (String line : lines) {
			getMaximumDistanceBetweenDelimiters(line, delimiter, width);
		}
		for (int i = 1; i < width.size(); i++) {
			width.set(i, width.get(i) + width.get(i - 1));
		}
		StringBuilder result = new StringBuilder();
		for (String line : lines) {
			if (!line.isBlank()) {
				int index = line.indexOf(delimiter);
				int column = 0;
				while (index >= 0) {
					String left = line.substring(0, showDelimiter ? index + 1 : index);
					String right = line.substring(index + 1).trim();
					int spaces = width.get(column) + (column + 1) * 2 - left.length();
					line = left + " ".repeat(spaces) + right + System.lineSeparator();
					index = line.indexOf(delimiter, index + 1);
					column++;
				}
				result.append(line);
			} else {
				result.append(line);
			}
		}
		return result.toString();
	}
}

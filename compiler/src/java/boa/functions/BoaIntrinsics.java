package boa.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import boa.types.Code.CodeRepository;
import boa.types.Code.Revision;
import boa.types.Diff.ChangedFile;
import boa.types.Toplevel.Project;

/**
 * Boa domain-specific functions.
 * 
 * @author rdyer
 */
public class BoaIntrinsics {
	private final static String[] fixingRegex = {
		"\\s+(fix|fixes|fixing|fixed)\\s+",
		"(bug|issue)(s)?[\\s]+(#)?\\s*[0-9]+",
		"(bug|issue)\\s+id(s)?\\s*=\\s*[0-9]+"
	};

	private final static List<Matcher> fixingMatchers = new ArrayList<Matcher>();

	static {
		for (final String s : BoaIntrinsics.fixingRegex)
			fixingMatchers.add(Pattern.compile(s).matcher(""));
	}

	/**
	 * Is a log message indicating it is a fixing revision?
	 * 
	 * @param log the revision's log message to mine
	 * @return true if the log indicates a fixing revision
	 */
	@FunctionSpec(name = "isfixingrevision", returnType = "bool", formalParameters = { "string" })
	public static boolean isfixingrevision(final String log) {
		for (final Matcher m : fixingMatchers)
			if (m.reset(log).matches())
				return true;

		return false;
	}

	/**
	 * Does a Project contain a file of the specified type? This compares based on file extension.
	 * 
	 * @param p the Project to examine
	 * @param ext the file extension to look for
	 * @return true if the Project contains at least 1 file with the specified extension
	 */
	@FunctionSpec(name = "hasfiletype", returnType = "bool", formalParameters = { "Project", "string" })
	public static boolean hasfile(final Project p, final String ext) {
		for (int i = 0; i < p.getCodeRepositoriesCount(); i++)
			if (hasfile(p.getCodeRepositories(i), ext))
				return true;
		return false;
	}

	/**
	 * Does a CodeRepository contain a file of the specified type? This compares based on file extension.
	 * 
	 * @param cr the CodeRepository to examine
	 * @param ext the file extension to look for
	 * @return true if the CodeRepository contains at least 1 file with the specified extension
	 */
	@FunctionSpec(name = "hasfiletype", returnType = "bool", formalParameters = { "CodeRepository", "string" })
	public static boolean hasfile(final CodeRepository cr, final String ext) {
		for (int i = 0; i < cr.getRevisionsCount(); i++)
			if (hasfile(cr.getRevisions(i), ext))
				return true;
		return false;
	}

	/**
	 * Does a Revision contain a file of the specified type? This compares based on file extension.
	 * 
	 * @param rev the Revision to examine
	 * @param ext the file extension to look for
	 * @return true if the Revision contains at least 1 file with the specified extension
	 */
	@FunctionSpec(name = "hasfiletype", returnType = "bool", formalParameters = { "Revision", "string" })
	public static boolean hasfile(final Revision rev, final String ext) {
		for (int i = 0; i < rev.getFilesCount(); i++)
			if (rev.getFiles(i).getName().toLowerCase().endsWith("." + ext.toLowerCase()))
				return true;
		return false;
	}

	/**
	 * Matches a FileKind enum to the given string.
	 * 
	 * @param s the string to match against
	 * @param kind the FileKind to match
	 * @return true if the string matches the given kind
	 */
	@FunctionSpec(name = "iskind", returnType = "bool", formalParameters = { "string", "FileKind" })
	public static boolean iskind(final String s, final ChangedFile.FileKind kind) {
		return kind.name().startsWith(s);
	}

	public static <T> T stack_pop(final java.util.Stack<T> s) {
		if (s.empty())
			return null;
		return s.pop();
	}

	public static <T> T stack_peek(final java.util.Stack<T> s) {
		if (s.empty())
			return null;
		return s.peek();
	}
}

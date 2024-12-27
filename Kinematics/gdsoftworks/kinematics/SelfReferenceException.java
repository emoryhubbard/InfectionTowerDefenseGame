package gdsoftworks.kinematics;
/**
 * A self-reference exception is thrown in lieu of attempting
 * to evaluate a recursive method indefinitely. Some objects created
 * with the kinematics package hold references to other objects
 * of identical type, and are not intended to hold their own reference.
 * @author EDH
 * @version 1.1
 * @see Model
 */
public class SelfReferenceException extends RuntimeException {
	private final Model part;
	/**
	 * Specified model's <code>toString()</code> is used in detail message
	 * @param model the self-referencing model
	 */
	public SelfReferenceException(Model model) {
		super("Model concatenation is implemented recursively in "
				+ "kinematics API. Models cannot be attached to themselves.");
		this.part = model;
	}
	/**
	 * Returns textual stack trace
	 * @return stack trace
	 */
	public String stackTrace() {
		String stack = "";
		for (StackTraceElement element: getStackTrace())
			stack += element+"\n";
		return stack;
	}
	public String toString() {
		return super.toString()+" Self-referenced part: "+part;
	}
}

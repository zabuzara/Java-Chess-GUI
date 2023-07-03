package www.zabuzara.com.tools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.Node;


/**
 * Abstract parent controller of a given node, usually a kind of pane.
 * @param <T> this controller's node type
 */
public abstract class ParentController<T extends Node> extends Controller<T> {

	private Map<String,Controller<?>> children;


	/**
	 * Initializes a new instance.
	 * @param node the node
	 */
	public ParentController (final T node) {
		super(node);
		this.children = new ConcurrentHashMap<>();
	}


	/**
	 * Returns the children.
	 * @return the children
	 */
	public Map<String,Controller<?>> getChildren () {
		return this.children;
	}
}

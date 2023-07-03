package www.zabuzara.com.tools;

import static java.util.Objects.requireNonNull;
import javafx.scene.Node;


/**
 * Abstract controller of a given node, usually a kind of pane.
 * @param <T> this controller's node type
 */
public abstract class Controller<T extends Node> {

	private final T node;


	/**
	 * Initializes a new instance.
	 * @param node the node
	 */
	public Controller (final T node) {
		this.node = requireNonNull(node);
	}


	/**
	 * Returns the node.
	 * @return the node
	 */
	public T getNode () {
		return this.node;
	}
}

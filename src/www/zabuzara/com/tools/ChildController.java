package www.zabuzara.com.tools;

import static java.util.Objects.requireNonNull;
import javafx.scene.Node;


/**
 * Abstract child controller of a given node, usually a kind of pane.
 * @param <P> the parent's node type
 * @param <T> this controller's node type
 */
public abstract class ChildController<P extends Node, T extends Node> extends Controller<T> {

	private ParentController<P> parent;


	/**
	 * Initializes a new instance.
	 * @param node the node
	 */
	public ChildController (final ParentController<P> parent, final T node) {
		super(node);
		this.parent = requireNonNull(parent);
	}


	/**
	 * Returns the parent.
	 * @return the parent
	 */
	public ParentController<P> getParent () {
		return this.parent;
	}
}

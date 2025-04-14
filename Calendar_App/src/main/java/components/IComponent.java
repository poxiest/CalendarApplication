package components;

/**
 * A representation of a simple component with the ability
 * to rerender itself.
 */
public interface IComponent {

  /**
   * Rerenders the component, removes
   * all nested panels and repaints.
   */
  public void rerender();

}

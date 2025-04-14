package components;

import javax.swing.JPanel;

/**
 * Abstract component which defines a rerender function that is used
 * by JPanels to clear and replace nested components.
 */
public abstract class AbstractComponent extends JPanel implements IComponent {

  protected abstract void render();

  /**
   * Rerenders the component, removing
   * all nested components and replacing
   * them. This is essential for updating
   * components.
   */
  public void rerender() {
    this.removeAll();
    this.render();
    this.revalidate();
    this.repaint();
  }

}

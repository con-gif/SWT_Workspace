package org.iMage.plugins;

/**
 * Abstract parent class for plug-ins for JMJRST
 *
 * @author Dominik Fuchss
 */
public abstract class PluginForJmjrst implements Comparable<PluginForJmjrst> {

  /**
   * Returns the name of this plug-in
   *
   * @return the name of the plug-in
   */
  public abstract String getName();

  /**
   * Returns the number of parameters of this plug-in
   *
   * @return the number of parameters of this plug-in
   */
  public abstract int getNumberOfParameters();

  /**
   * JMJRST pushes the main application to every subclass - so plug-ins are allowed to look at Main
   * as well.
   *
   * @param main
   *     JMJRST main application
   */
  public abstract void init(org.jis.Main main);

  /**
   * Runs plug-in
   */
  public abstract void run();

  /**
   * Returns whether the plug-in can be configured or not
   *
   * @return true if the plug-in can be configured.
   */
  public abstract boolean isConfigurable();

  /**
   * Open a configuration dialogue.
   */
  public abstract void configure();

  /**
   * Compare to implementation. Comparison on two levels: lexicographic by name and by number of parameters.
   * @param otherPlugin plugin to compare to.
   * @return an int less than zero if the current instance is lesser than the parameter, zero if both instances are
   * equal on both parameters and a value greater than zero otherwise.
   */
  @Override
  public int compareTo(PluginForJmjrst otherPlugin) {
    if (this.getName().compareTo(otherPlugin.getName()) == 0) {
      return Integer.compare(this.getNumberOfParameters(), otherPlugin.getNumberOfParameters());
    } else {
      return this.getName().compareTo(otherPlugin.getName());
    }
  }

  /**
   * HashCode implementation.
   * @return Standard hash generated from name and numberOfParameters parameters.
   */
  @Override
  public int hashCode() {
    return String.format("%s%d", this.getName(), this.getNumberOfParameters()).hashCode();
  }

  /**
   * Equals implementation.
   * @param obj Object to compare to.
   * @return true of the current instance and the parameter point to the same object or are equal on all parameters.
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    PluginForJmjrst toCompare = (PluginForJmjrst) obj;
    return (this.getName().equals(toCompare.getName())
            && this.getNumberOfParameters() == toCompare.getNumberOfParameters());
  }
}

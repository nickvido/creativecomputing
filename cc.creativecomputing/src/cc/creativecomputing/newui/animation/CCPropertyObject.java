package cc.creativecomputing.newui.animation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cc.creativecomputing.xml.CCXMLElement;

/**
 * <p>
 * Use this annotation to mark an object for xml serialization. Objects marked
 * with this annotation can be passed to {@linkplain CCXMLElement#addChild(Object)}
 * method.
 * </p>
 * <p>
 * Use the {@linkplain CCProperty} annotation to mark class attributes for XML 
 * serialization.
 * </p>
 * @author christianriekoff
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CCPropertyObject{
	/**
	 * Defines the name of the node in the xml document
	 * @return the name of the node
	 */
	String name() default "";
}

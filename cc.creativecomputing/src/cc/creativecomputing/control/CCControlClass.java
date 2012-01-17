package cc.creativecomputing.control;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CCControlClass {
	/**
	 * Defines the name of the editable
	 * @return the name of the editable
	 */
	String name() default "";
	
	/**
	 * Use column to define in which column the elements of the editable are placed
	 * @return returns the columns where to place the userinterface element
	 */
	int column() default 0;
}

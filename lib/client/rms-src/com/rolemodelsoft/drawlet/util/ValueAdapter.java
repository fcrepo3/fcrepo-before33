package com.rolemodelsoft.drawlet.util;

/**
 * @(#)ValueAdapter.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. RMS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.lang.reflect.*;

/**
 * This is a quick and dirty version of the Adapter pattern which observes some model
 * for changes in some aspect, and updates a target with a one argument message based
 * on the new value of that aspect.  There are a lot of other and more flexible ways to
 * accomplish similar goals.  This was an attempt to get a minimal version up and prove
 * the concept.
 *
 * @version 	1.1.6, 12/30/98
 */
public class ValueAdapter implements Observer {
	/**
	 * The model.
	 */
	Object model;

	/**
	 * The aspect.
	 */
	String aspect;

	/**
	 * The target.
	 */
	Object target;

	/**
	 * The affect.
	 */
	String affect;
	/**
	 * Answer an instance that affects its target whenever the identified aspect of
	 * the identified model changes.
	 *
	 * @param model the subject being watched.
	 * @param aspect the aspect of the subject of interest... tied to a getter message.
	 * @param target the object who will be affected when the aspect changes.
	 * @param affect the message sent to the target when the aspect changes, with the value of the aspect as its argument.
	 */
	public ValueAdapter(Observable model, String aspect, Object target, String affect) {
		this.model = model;
		model.addObserver(this);
		this.aspect = aspect;
		this.target = target;
		this.affect = affect;
	}
	/**
	 * Answer an instance that affects its target whenever the identified aspect of
	 * the identified model changes.
	 *
	 * @param model the subject being watched.
	 * @param aspect the aspect of the subject of interest... tied to a getter message.
	 * @param target the object who will be affected when the aspect changes.
	 * @param affect the message sent to the target when the aspect changes, with the value of the aspect as its argument.
	 */
	public ValueAdapter(Object model, String aspect, Object target, String affect) {
		this.model = model;
		this.aspect = aspect;
		this.target = target;
		this.affect = affect;
	}
	/**
	 * Provides the method corresponding to the parameters or null if one can't be found.
	 *
	 * @param targetClass the class which theoretically holds the method we'd like to find.
	 * @param message the name of the method we'd like to find.
	 * @param targetParameterClasses the classes of the arguments of the method we'd like to find.
	 * @return the specified Method, or null if it can't be found.
	 */
	protected Method getMessage(Class targetClass, String message, Class[] targetParameterClasses) {
		try { 
			return targetClass.getMethod(affect, targetParameterClasses);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	/**
	 * If being updated by the subject with regard to the aspect of interest, update the target.
	 *
	 * @param subject the Observable who notified us of the change.
	 * @param arg the identifier of the aspect which changed.
	 */
	public void update(Observable subject, Object arg) {
		if (arg == aspect && subject == model) {
			try {
				updateTarget();
			} catch (NoSuchMethodException e) { System.out.println(e);
			} catch (IllegalAccessException e) { System.out.println(e);
			} catch (InvocationTargetException e) { System.out.println(e);
			} 
		}
	}
	/**
	 * Send the one argument message to the target with the value as the argument.
	 *
	 * @exception NoSuchMethodException If we just can't find a method that seems to fit the criteria.
	 * @exception IllegalAccessException If access is not permitted to the method (needs to be public?).
	 * @exception InvocationTargetException If an error occurred after invoking the method.
	 */
	public void updateTarget() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method targetMessage;
		Object targetArgument = value();
		Class targetClass = target.getClass();
		Class targetParameterClass = targetArgument.getClass();
		Class targetParameterClasses[] = new Class[1];
		do {
			targetParameterClasses[0] = targetParameterClass;
			targetMessage = getMessage(targetClass, affect, targetParameterClasses);
			if (targetMessage == null) {
				Class interfaces[] = targetParameterClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					targetParameterClasses[0] = interfaces[i];
					targetMessage = getMessage(targetClass, affect, targetParameterClasses);
					if (targetMessage != null)
						break;
				}
				targetParameterClass = targetParameterClass.getSuperclass();
			}
		} while ((targetMessage == null) && (targetParameterClass != null));
		if (targetMessage == null)
			throw new NoSuchMethodException(affect + "not found in " + target.getClass().getName());
		Object targetMessageParameters[] = new Object[] {targetArgument};
		targetMessage.invoke(target, targetMessageParameters);
	}
	/**
	 * Get the value of interest from the subject.
	 *
	 * @return the value (an Object) of interest to the subject.
	 *
	 * @exception NoSuchMethodException If we just can't find a method that seems to fit the criteria.
	 * @exception IllegalAccessException If access is not permitted to the method (needs to be public?).
	 * @exception InvocationTargetException If an error occurred after invoking the method.
	 */
	public Object value() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class subjectClass = model.getClass();
		Method subjectMessage = subjectClass.getMethod(aspect, new Class[0]);
		return subjectMessage.invoke(model, new Object[0]);
	}
}

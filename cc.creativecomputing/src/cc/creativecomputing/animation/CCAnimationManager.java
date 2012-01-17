package cc.creativecomputing.animation;

import java.util.ArrayList;

/**
 * The Animation Object is the Center Class of the Animation Package.It takes
 * care of every Animation Object. The Animation class should be used in a
 * static way.
 * 
 * 
 * @author jenswunderling
 * 
 */
public final class CCAnimationManager {

	private static ArrayList<CCAnimation> _myAnimations = new ArrayList<CCAnimation>();
	private static ArrayList<CCAnimation> _myAnimationsToAdd = new ArrayList<CCAnimation>();

	/**
	 * update needs to be called in order to manage Animations
	 * 
	 * @param theDeltaTime
	 */
	public static void update(float theDeltaTime) {
		integrateAnimations();
		ArrayList<CCAnimation> finishedAnimations = new ArrayList<CCAnimation>();
		for (CCAnimation a : _myAnimations) {
			if (!a.finished()) {
				a.update(theDeltaTime);
			} else {
				finishedAnimations.add(a);
			}
		}
		for (CCAnimation a : finishedAnimations) {
			_myAnimations.remove(a);
		}
	}

	public static void integrateAnimations() {
		for (CCAnimation a : _myAnimationsToAdd) {
			addAnAnimation(a);
		}
		_myAnimationsToAdd.clear();
	}

	public static void addAnAnimation(CCAnimation animation) {
		ArrayList<CCAnimation> animationsToRemove = new ArrayList<CCAnimation>();
		for (CCAnimation a : _myAnimations) {
			if (a.getTarget() != null)

				if (a.getTarget() == animation.getTarget()
						&& a.targetValueName() == animation.targetValueName()) {
					if (a.targetValueName() != null)
						animationsToRemove.add(a);
				}
		}
		for (CCAnimation a : animationsToRemove) {
			_myAnimations.remove(a);
		}
		_myAnimations.add(animation);
	}

	public static void addAnimation(CCAnimation animation) {
		_myAnimationsToAdd.add(animation);
	}

	public static int countAnimations() {
		return _myAnimations.size();
	}
}

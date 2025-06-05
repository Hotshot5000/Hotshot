/*
 * Created by Sebastian Bugiu on 4/9/23, 10:11 PM
 * sebastian.bugiu@headwayentertainment.net
 * Last modified 6/3/16, 3:33 AM
 * Copyright (c) 2023.
 * All rights reserved.
 */

package headwayent.hotshotengine.android.view.animation;

import headwayent.hotshotengine.SystemClock;

/**
 * Defines common utilities for working with animations.
 */
public class AnimationUtils {

    /**
     * These flags are used when parsing AnimatorSet objects
     */
    private static final int TOGETHER = 0;
    private static final int SEQUENTIALLY = 1;


    /**
     * Returns the current animation time in milliseconds. This time should be used when invoking
     * {@link Animation#setStartTime(long)}. Refer to {@link android.os.SystemClock} for more
     * information about the different available clocks. The clock used by this method is
     * <em>not</em> the "wall" clock (it is not {@link System#currentTimeMillis}).
     *
     * @return the current animation time in milliseconds
     * @see android.os.SystemClock
     */
    public static long currentAnimationTimeMillis() {
        return SystemClock.uptimeMillis();
    }

    /**
     * Loads an {@link Animation} object from a resource
     *
     * @param context Application context used to access resources
     * @param id The resource id of the animation to load
     * @return The animation object reference by the specified id
     * @throws NotFoundException when the animation cannot be loaded
     */
//    public static Animation loadAnimation(Context context, int id)
//            throws NotFoundException {
//
//        XmlResourceParser parser = null;
//        try {
//            parser = context.getResources().getAnimation(id);
//            return createAnimationFromXml(context, parser);
//        } catch (XmlPullParserException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } catch (IOException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } finally {
//            if (parser != null) parser.close();
//        }
//    }

//    private static Animation createAnimationFromXml(Context c, XmlPullParser parser)
//            throws XmlPullParserException, IOException {
//
//        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
//    }

//    private static Animation createAnimationFromXml(Context c, XmlPullParser parser,
//            AnimationSet parent, AttributeSet attrs) throws XmlPullParserException, IOException {
//
//        Animation anim = null;
//
//        // Make sure we are on a start tag.
//        int type;
//        int depth = parser.getDepth();
//
//        while (((type=parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
//               && type != XmlPullParser.END_DOCUMENT) {
//
//            if (type != XmlPullParser.START_TAG) {
//                continue;
//            }
//
//            String  name = parser.getName();
//
//            if (name.equals("set")) {
//                anim = new AnimationSet(c, attrs);
//                createAnimationFromXml(c, parser, (AnimationSet)anim, attrs);
//            } else if (name.equals("alpha")) {
//                anim = new AlphaAnimation(c, attrs);
//            } else if (name.equals("scale")) {
//                anim = new ScaleAnimation(c, attrs);
//            }  else if (name.equals("rotate")) {
//                anim = new RotateAnimation(c, attrs);
//            }  else if (name.equals("translate")) {
//                anim = new TranslateAnimation(c, attrs);
//            } else {
//                throw new RuntimeException("Unknown animation name: " + parser.getName());
//            }
//
//            if (parent != null) {
//                parent.addAnimation(anim);
//            }
//        }
//
//        return anim;
//
//    }

    /**
     * Loads a {@link LayoutAnimationController} object from a resource
     *
     * @param context Application context used to access resources
     * @param id The resource id of the animation to load
     * @return The animation object reference by the specified id
     * @throws NotFoundException when the layout animation controller cannot be loaded
     */
//    public static LayoutAnimationController loadLayoutAnimation(Context context, int id)
//            throws NotFoundException {
//
//        XmlResourceParser parser = null;
//        try {
//            parser = context.getResources().getAnimation(id);
//            return createLayoutAnimationFromXml(context, parser);
//        } catch (XmlPullParserException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } catch (IOException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } finally {
//            if (parser != null) parser.close();
//        }
//    }

//    private static LayoutAnimationController createLayoutAnimationFromXml(Context c,
//            XmlPullParser parser) throws XmlPullParserException, IOException {
//
//        return createLayoutAnimationFromXml(c, parser, Xml.asAttributeSet(parser));
//    }

//    private static LayoutAnimationController createLayoutAnimationFromXml(Context c,
//            XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
//
//        LayoutAnimationController controller = null;
//
//        int type;
//        int depth = parser.getDepth();
//
//        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
//                && type != XmlPullParser.END_DOCUMENT) {
//
//            if (type != XmlPullParser.START_TAG) {
//                continue;
//            }
//
//            String name = parser.getName();
//
//            if ("layoutAnimation".equals(name)) {
//                controller = new LayoutAnimationController(c, attrs);
//            } else if ("gridLayoutAnimation".equals(name)) {
//                controller = new GridLayoutAnimationController(c, attrs);
//            } else {
//                throw new RuntimeException("Unknown layout animation name: " + name);
//            }
//        }
//
//        return controller;
//    }

    /**
     * Make an animation for objects becoming visible. Uses a slide and fade
     * effect.
     *
     * @param c Context for loading resources
     * @param fromLeft is the object to be animated coming from the left
     * @return The new animation
     */
//    public static Animation makeInAnimation(Context c, boolean fromLeft) {
//        Animation a;
//        if (fromLeft) {
//            a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_in_left);
//        } else {
//            a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_in_right);
//        }
//
//        a.setInterpolator(new DecelerateInterpolator());
//        a.setStartTime(currentAnimationTimeMillis());
//        return a;
//    }

    /**
     * Make an animation for objects becoming invisible. Uses a slide and fade
     * effect.
     *
     * @param c Context for loading resources
     * @param toRight is the object to be animated exiting to the right
     * @return The new animation
     */
//    public static Animation makeOutAnimation(Context c, boolean toRight) {
//        Animation a;
//        if (toRight) {
//            a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_out_right);
//        } else {
//            a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_out_left);
//        }
//
//        a.setInterpolator(new AccelerateInterpolator());
//        a.setStartTime(currentAnimationTimeMillis());
//        return a;
//    }


    /**
     * Make an animation for objects becoming visible. Uses a slide up and fade
     * effect.
     *
     * @param c Context for loading resources
     * @return The new animation
     */
//    public static Animation makeInChildBottomAnimation(Context c) {
//        Animation a;
//        a = AnimationUtils.loadAnimation(c, com.android.internal.R.anim.slide_in_child_bottom);
//        a.setInterpolator(new AccelerateInterpolator());
//        a.setStartTime(currentAnimationTimeMillis());
//        return a;
//    }

    /**
     * Loads an {@link Interpolator} object from a resource
     *
     * @param context Application context used to access resources
     * @param id The resource id of the animation to load
     * @return The animation object reference by the specified id
     * @throws NotFoundException
     */
//    public static Interpolator loadInterpolator(Context context, int id) throws NotFoundException {
//        XmlResourceParser parser = null;
//        try {
//            parser = context.getResources().getAnimation(id);
//            return createInterpolatorFromXml(context.getResources(), context.getTheme(), parser);
//        } catch (XmlPullParserException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } catch (IOException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } finally {
//            if (parser != null) parser.close();
//        }
//
//    }

    /**
     * Loads an {@link Interpolator} object from a resource
     *
     * @param res The resources
     * @param id The resource id of the animation to load
     * @return The interpolator object reference by the specified id
     * @throws NotFoundException
     * @hide
     */
//    public static Interpolator loadInterpolator(Resources res, Theme theme, int id) throws NotFoundException {
//        XmlResourceParser parser = null;
//        try {
//            parser = res.getAnimation(id);
//            return createInterpolatorFromXml(res, theme, parser);
//        } catch (XmlPullParserException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } catch (IOException ex) {
//            NotFoundException rnf = new NotFoundException("Can't load animation resource ID #0x" +
//                    Integer.toHexString(id));
//            rnf.initCause(ex);
//            throw rnf;
//        } finally {
//            if (parser != null)
//                parser.close();
//        }
//
//    }

//    private static Interpolator createInterpolatorFromXml(Resources res, Theme theme, XmlPullParser parser)
//            throws XmlPullParserException, IOException {
//
//        Interpolator interpolator = null;
//
//        // Make sure we are on a start tag.
//        int type;
//        int depth = parser.getDepth();
//
//        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
//                && type != XmlPullParser.END_DOCUMENT) {
//
//            if (type != XmlPullParser.START_TAG) {
//                continue;
//            }
//
//            AttributeSet attrs = Xml.asAttributeSet(parser);
//
//            String name = parser.getName();
//
//            if (name.equals("linearInterpolator")) {
//                interpolator = new LinearInterpolator();
//            } else if (name.equals("accelerateInterpolator")) {
//                interpolator = new AccelerateInterpolator(res, theme, attrs);
//            } else if (name.equals("decelerateInterpolator")) {
//                interpolator = new DecelerateInterpolator(res, theme, attrs);
//            } else if (name.equals("accelerateDecelerateInterpolator")) {
//                interpolator = new AccelerateDecelerateInterpolator();
//            } else if (name.equals("cycleInterpolator")) {
//                interpolator = new CycleInterpolator(res, theme, attrs);
//            } else if (name.equals("anticipateInterpolator")) {
//                interpolator = new AnticipateInterpolator(res, theme, attrs);
//            } else if (name.equals("overshootInterpolator")) {
//                interpolator = new OvershootInterpolator(res, theme, attrs);
//            } else if (name.equals("anticipateOvershootInterpolator")) {
//                interpolator = new AnticipateOvershootInterpolator(res, theme, attrs);
//            } else if (name.equals("bounceInterpolator")) {
//                interpolator = new BounceInterpolator();
//            } else if (name.equals("pathInterpolator")) {
//                interpolator = new PathInterpolator(res, theme, attrs);
//            } else {
//                throw new RuntimeException("Unknown interpolator name: " + parser.getName());
//            }
//
//        }
//
//        return interpolator;
//
//    }
}

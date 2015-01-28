package net.alexgraham.thesis.tests.demos.MultiSlider;

/* -------------------------------------------------------------------
 * GeoVISTA Center (Penn State, Dept. of Geography)
 * 
 * Java source file for the class MultiSliderBeanInfo
 * 
 * Copyright (c), 2002, Masahiro Takatsuka & GeoVISTA Center
 * All Rights Reserved.
 * 
 * This is a copy from jh9gpz.ui.slider.MtMultiSliderBeanInfo.java 
 * originally written by Masahiro Takatsuka
 *
 * Original Author: Masahiro Takatsuka
 * $Author: jmacgill $
 * 
 * $Date: 2003/02/28 14:54:06 $
 * 
 * Reference:           Document no:
 * ___                          ___
 * 
 * To Do:
 * ___
 * 
------------------------------------------------------------------- */

/* ------------------ Import classes (packages) ------------------- */
import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/*====================================================================
             Implementation of class MultiSliderBeanInfo              
====================================================================*/
/**
 * MultiSliderBeanInfo provides information on the MtltiSlider bean.
 * 
 * @version $Revision: 1.1.1.1 $
 * @author Masahiro Takatsuka (jh9gpz@yahoo.com)
 * @see SimpleBeanInfo
 */

public class MultiSliderBeanInfo extends SimpleBeanInfo {
    private static final Class beanClass = MultiSlider.class;

        private static String iconColor16x16Filename = "resources/MultiSlider/IconClor16.gif";
        private static String iconColor32x32Filename = "resources/MultiSlider/IconColor32.gif";
        private static String iconMono16x16Filename;
        private static String iconMono32x32Filename;

        public Image getIcon(int iconKind) {
                switch (iconKind) {
                case BeanInfo.ICON_COLOR_16x16:
                        return iconColor16x16Filename != null ? loadImage(iconColor16x16Filename) : null;
                case BeanInfo.ICON_COLOR_32x32:
                        return iconColor32x32Filename != null ? loadImage(iconColor32x32Filename) : null;
                case BeanInfo.ICON_MONO_16x16:
                        return iconMono16x16Filename != null ? loadImage(iconMono16x16Filename) : null;
                case BeanInfo.ICON_MONO_32x32:
                        return iconMono32x32Filename != null ? loadImage(iconMono32x32Filename) : null;
                }
                return null;
        }

    public MultiSliderBeanInfo() {
    }

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bd = new BeanDescriptor(beanClass);
                bd.setPreferred(true);
                bd.setShortDescription("A component that supports selecting a integer value from a range.");
                bd.setValue("hidden-state", Boolean.TRUE);
                bd.setValue("helpSetName", "edu/psu/geovista/ui/slider/resources/MultiSlider/jhelpset.hs");
                return bd;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
                try {
                        return (new PropertyDescriptor[] {
                                new PropertyDescriptor("labelTable", beanClass),
                                new PropertyDescriptor("minorTickSpacing", beanClass),
                                new PropertyDescriptor("visualUpdate", beanClass),
                                new PropertyDescriptor("majorTickSpacing", beanClass),
                                new PropertyDescriptor("orientation", beanClass),
                                new PropertyDescriptor("model", beanClass),
                                new PropertyDescriptor("paintLabels", beanClass),
                                new PropertyDescriptor("paintTrack", beanClass),
                                new PropertyDescriptor("extent", beanClass),
                                new PropertyDescriptor("inverted", beanClass),
                                new PropertyDescriptor("minimum", beanClass),
                                new PropertyDescriptor("maximum", beanClass),
                                new PropertyDescriptor("value", beanClass),
                                new PropertyDescriptor("paintTicks", beanClass),
                                new PropertyDescriptor("snapToTicks", beanClass),
                                new PropertyDescriptor("numberOfThumbs", beanClass),
                                new PropertyDescriptor("bounded", beanClass)
                        });
                } catch (Exception e) {
                        return new PropertyDescriptor[]{};
                }
    }
}


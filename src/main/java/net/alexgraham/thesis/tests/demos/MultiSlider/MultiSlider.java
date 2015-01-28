package net.alexgraham.thesis.tests.demos.MultiSlider;

/* ------------------ Import classes (packages) ------------------- */
import java.awt.Color;
import java.awt.Dimension;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;

/*
 * ====================================================================
 * Implementation of class MultiSlider
 * ====================================================================
 */
/**
 * A component that lets the user graphically select values by slding multiple
 * thumbs within a bounded interval. MultiSlider inherits all fields and methods
 * from javax.swing.JSlider.
 * 
 * @version $Revision: 1.1.1.1 $
 * @author Masahiro Takatsuka (jh9gpz@yahoo.com)
 * @see JSlider
 */

public class MultiSlider extends JSlider {

        /**
         * An array of data models that handle the numeric maximum values, minimum
         * values, and current-position values for the multi slider.
         */
        private BoundedRangeModel[] sliderModels;

        /**
         * If it is true, a thumb is bounded by adjacent thumbs.
         */
        private boolean bounded = false;

        /**
         * This is a color to paint the current thumb
         */
        private Color currentThumbColor = Color.red;

        transient private int valueBeforeStateChange;

        /**
         * Creates a slider with the specified orientation and the specified
         * mimimum, maximum, and initial values.
         * 
         * @exception IllegalArgumentException
         *                if orientation is not one of VERTICAL, HORIZONTAL
         * 
         * @see #setOrientation
         * @see #setMinimum
         * @see #setMaximum
         * @see #setValue
         */
        public MultiSlider(int orientation, int min, int max, int[] values) {
                checkOrientation(orientation);
                this.orientation = orientation;
                setNumberOfThumbs(min, max, values);
        }

        /**
         * Creates a slider with the specified orientation and the specified
         * mimimum, maximum, and the number of thumbs.
         * 
         * @exception IllegalArgumentException
         *                if orientation is not one of VERTICAL, HORIZONTAL
         * 
         * @see #setOrientation
         * @see #setMinimum
         * @see #setMaximum
         * @see #setValue
         */
        public MultiSlider(int orientation, int min, int max, int num_of_values) {
                checkOrientation(orientation);
                this.orientation = orientation;
                setNumberOfThumbs(min, max, num_of_values);
        }

        /**
         * Creates a horizontal slider with the range 0 to 100 and an intitial value
         * of 50.
         */
        public MultiSlider() {
                this(HORIZONTAL, 0, 100, null);
        }

        /**
         * Creates a slider using the specified orientation with the range 0 to 100
         * and an intitial value of 50.
         */
        public MultiSlider(int orientation) {
                this(orientation, 0, 100, null);
        }

        /**
         * Creates a horizontal slider using the specified min and max with an
         * intitial value of 50.
         */
        public MultiSlider(int min, int max) {
                this(HORIZONTAL, min, max, null);
        }

        public void setCurrentThumbColor(Color c) {
                currentThumbColor = c;
        }

        public Color getCurrentThumbColor() {
                return currentThumbColor;
        }

        public int getTrackBuffer() {
                return ((MultiSliderUI) ui).getTrackBuffer();
        }

        /**
         * Validates the orientation parameter.
         */
        private void checkOrientation(int orientation) {
                switch (orientation) {
                case VERTICAL:
                case HORIZONTAL:
                        break;
                default:
                        throw new IllegalArgumentException(
                                        "orientation must be one of: VERTICAL, HORIZONTAL");
                }
        }

        /**
         * Notification from the UIFactory that the L&F has changed. Called to
         * replace the UI with the latest version from the default UIFactory.
         * 
         * @see JComponent#updateUI
         */
        @Override
        public void updateUI() {
                updateLabelUIs();
                MultiSliderUI ui = new MultiSliderUI();
                if (sliderModels != null) {
                        ui.setThumbCount(sliderModels.length);
                }
                setUI(ui);
        }

        /**
         * Returns the number of thumbs in the slider.
         */
        public int getNumberOfThumbs() {
                return sliderModels.length;
        }

        /**
         * Sets the number of thumbs with the specified parameters.
         */
        private void setNumberOfThumbs(int min, int max, int num,
                        boolean useEndPoints) {
                int[] values = createDefaultValues(min, max, num, useEndPoints);
                setNumberOfThumbs(min, max, values);
        }

        /**
         * Sets the number of thumbs with the specified parameters.
         */
        private void setNumberOfThumbs(int min, int max, int num) {
                setNumberOfThumbs(min, max, num, false);
        }

        /**
         * Sets the number of thumbs with the specified parameters.
         */
        private void setNumberOfThumbs(int min, int max, int[] values) {
                if (values == null || values.length < 1) {
                        values = new int[] { 50 };
                }
                int num = values.length;
                sliderModels = new BoundedRangeModel[num];
                for (int i = 0; i < num; i++) {
                        sliderModels[i] = new DefaultBoundedRangeModel(values[i], 0, min,
                                        max);
                        sliderModels[i].addChangeListener(changeListener);
                }
                updateUI();
        }

        /**
         * Sets the number of thumbs.
         */
        public void setNumberOfThumbs(int num) {
                setNumberOfThumbs(num, false);
        }

        /**
         * Sets the number of thumbs.
         */
        public void setNumberOfThumbs(int num, boolean useEndPoints) {
                if (getNumberOfThumbs() != num) {
                        setNumberOfThumbs(getMinimum(), getMaximum(), num, useEndPoints);
                }
        }

        /**
         * Sets the number of thumbs by specifying the initial values.
         */
        public void setNumberOfThumbs(int[] values) {
                setNumberOfThumbs(getMinimum(), getMaximum(), values);
        }

        /**
         * creates evenly spaced values for thumbs.
         */
        private int[] createDefaultValues(int min, int max, int num_of_values,
                        boolean useEndPoints) {
                int[] values = new int[num_of_values];
                int range = max - min;

                if (!useEndPoints) {
                        int step = range / (num_of_values + 1);
                        for (int i = 0; i < num_of_values; i++) {
                                values[i] = min + (i + 1) * step;
                        }
                } else {
                        if (num_of_values < 1) {
                                return new int[0];
                        }
                        values[0] = getMinimum();
                        values[num_of_values - 1] = getMaximum();
                        int[] def = createDefaultValues(getMinimum(), getMaximum(),
                                        num_of_values - 2, false);
                        for (int i = 0; i < def.length; i++) {
                                values[i + 1] = def[i];
                        }
                }
                return values;
        }

        /**
         * Returns the index number of currently operated thumb.
         */
        public int getCurrentThumbIndex() {
                return ((MultiSliderUI) ui).getCurrentIndex();
        }

        /**
         * Returns data model that handles the sliders three fundamental properties:
         * minimum, maximum, value.
         * 
         * @see #setModel
         */
        @Override
        public BoundedRangeModel getModel() {
                return getModelAt(getCurrentThumbIndex());
        }

        /**
         * Returns data model that handles the sliders three fundamental properties:
         * minimum, maximum, value.
         * 
         * @see #setModel
         */
        public BoundedRangeModel getModelAt(int index) {
                if (sliderModels == null || index >= sliderModels.length) {
                        return null;
                }
                return sliderModels[index];
        }

        /**
         * Returns data model that handles the sliders three fundamental properties:
         * minimum, maximum, value.
         * 
         * @see #setModel
         */
        public BoundedRangeModel[] getModels() {
                return sliderModels;
        }

        /**
         * Sets the model that handles the sliders three fundamental properties:
         * minimum, maximum, value.
         * 
         * @see #getModel
         * @beaninfo bound: true description: The sliders BoundedRangeModel.
         */
        @Override
        public void setModel(BoundedRangeModel newModel) {
                setModelAt(getCurrentThumbIndex(), newModel);
        }

        /**
         * Sets the model that handles the sliders three fundamental properties:
         * minimum, maximum, value.
         * 
         * @see #getModel
         * @beaninfo bound: true description: The sliders BoundedRangeModel.
         */
        public void setModelAt(int index, BoundedRangeModel newModel) {
                BoundedRangeModel oldModel = getModelAt(index);

                if (oldModel != null) {
                        oldModel.removeChangeListener(changeListener);
                }

                sliderModels[index] = newModel;

                if (newModel != null) {
                        newModel.addChangeListener(changeListener);

                        if (accessibleContext != null) {
                                accessibleContext.firePropertyChange(
                                                AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                                                (oldModel == null ? null : new Integer(oldModel
                                                                .getValue())), (newModel == null ? null
                                                                : new Integer(newModel.getValue())));
                        }
                }

                firePropertyChange("model", oldModel, sliderModels[index]);
        }

        /**
         * Sets the models minimum property.
         * 
         * @see #getMinimum
         * @see BoundedRangeModel#setMinimum
         * @beaninfo bound: true preferred: true description: The sliders minimum
         *           value.
         */
        @Override
        public void setMinimum(int minimum) {
                int count = getNumberOfThumbs();
                int oldMin = getModel().getMinimum();
                for (int i = 0; i < count; i++) {
                        getModelAt(i).setMinimum(minimum);
                }
                firePropertyChange("minimum", new Integer(oldMin), new Integer(minimum));
        }

        /**
         * Sets the models maximum property.
         * 
         * @see #getMaximum
         * @see BoundedRangeModel#setMaximum
         * @beaninfo bound: true preferred: true description: The sliders maximum
         *           value.
         */
        @Override
        public void setMaximum(int maximum) {
                int count = getNumberOfThumbs();
                int oldMax = getModel().getMaximum();
                for (int i = 0; i < count; i++) {
                        getModelAt(i).setMaximum(maximum);
                }
                firePropertyChange("maximum", new Integer(oldMax), new Integer(maximum));
        }

        /**
         * Returns the sliders value.
         * 
         * @return the models value property
         * @see #setValue
         */
        @Override
        public int getValue() {
                return getValueAt(getCurrentThumbIndex());
        }

        /**
         * Returns the sliders value.
         * 
         * @return the models value property
         * @see #setValue
         */
        public int getValueAt(int index) {
                return getModelAt(index).getValue();
        }

        /**
         * Sets the sliders current value. This method just forwards the value to
         * the model.
         * 
         * @see #getValue
         * @beaninfo preferred: true description: The sliders current value.
         */
        @Override
        public void setValue(int n) {
                setValueAt(getCurrentThumbIndex(), n);
        }

        /**
         * Sets the sliders current value. This method just forwards the value to
         * the model.
         * 
         * @see #getValue
         * @beaninfo preferred: true description: The sliders current value.
         */
        public void setValueAt(int index, int n) {
                BoundedRangeModel m = getModelAt(index);
                int oldValue = m.getValue();
                m.setValue(n);

                if (accessibleContext != null) {
                        accessibleContext.firePropertyChange(
                                        AccessibleContext.ACCESSIBLE_VALUE_PROPERTY, new Integer(
                                                        oldValue), new Integer(m.getValue()));
                }
        }

        /**
         * True if the slider knob is being dragged.
         * 
         * @return the value of the models valueIsAdjusting property
         * @see #setValueIsAdjusting
         */
        @Override
        public boolean getValueIsAdjusting() {
                boolean result = false;
                int count = getNumberOfThumbs();
                for (int i = 0; i < count; i++) {
                        result = (result || getValueIsAdjustingAt(i));
                }
                return result;
        }

        /**
         * True if the slider knob is being dragged.
         */
        public boolean getValueIsAdjustingAt(int index) {
                return getModelAt(index).getValueIsAdjusting();
        }

        /**
         * Sets the models valueIsAdjusting property. Slider look and feel
         * implementations should set this property to true when a knob drag begins,
         * and to false when the drag ends. The slider model will not generate
         * ChangeEvents while valueIsAdjusting is true.
         * 
         * @see #getValueIsAdjusting
         * @see BoundedRangeModel#setValueIsAdjusting
         * @beaninfo expert: true description: True if the slider knob is being
         *           dragged.
         */
        @Override
        public void setValueIsAdjusting(boolean b) {
                setValueIsAdjustingAt(getCurrentThumbIndex(), b);
        }

        /**
         * Sets the models valueIsAdjusting property. Slider look and feel
         * implementations should set this property to true when a knob drag begins,
         * and to false when the drag ends. The slider model will not generate
         * ChangeEvents while valueIsAdjusting is true.
         */
        public void setValueIsAdjustingAt(int index, boolean b) {
                BoundedRangeModel m = getModelAt(index);
                boolean oldValue = m.getValueIsAdjusting();
                m.setValueIsAdjusting(b);

                if ((oldValue != b) && (accessibleContext != null)) {
                        accessibleContext.firePropertyChange(
                                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                        ((oldValue) ? AccessibleState.BUSY : null),
                                        ((b) ? AccessibleState.BUSY : null));
                }
        }

        /**
         * Sets the size of the range "covered" by the knob. Most look and feel
         * implementations will change the value by this amount if the user clicks
         * on either side of the knob.
         * 
         * @see #getExtent
         * @see BoundedRangeModel#setExtent
         * @beaninfo expert: true description: Size of the range covered by the
         *           knob.
         */
        @Override
        public void setExtent(int extent) {
                int count = getNumberOfThumbs();
                for (int i = 0; i < count; i++) {
                        getModelAt(i).setExtent(extent);
                }
        }

        /**
         * Sets a bounded attribute of a slider thumb.
         * 
         * <PRE>
         * </PRE>
         * 
         * @param b
         * @return void
         */
        public void setBounded(boolean b) {
                bounded = b;
        }

        /**
         * Returns a bounded attribute of a slider thumb.
         * 
         * <PRE>
         * </PRE>
         * 
         * @return boolean
         */
        public boolean isBounded() {
                return bounded;
        }

        public int getValueBeforeStateChange() {
                return valueBeforeStateChange;
        }

        void setValueBeforeStateChange(int v) {
                valueBeforeStateChange = v;
        }

        public static void main(String[] args) {
                JFrame frame = new JFrame("multislider");
                MultiSlider mSlider = new MultiSlider();
                mSlider.setMinimumSize(new Dimension(300, 100));
                mSlider.setNumberOfThumbs(5);
                mSlider.setMajorTickSpacing(10);
                // mSlider.setPaintTicks(true);
                mSlider.setPaintLabels(true);
                // mSlider.setPaintTrack(true);

                frame.add(mSlider);
                frame.pack();
                frame.setVisible(true);

        }
}
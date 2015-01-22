package net.alexgraham.thesis.tests.demos.mvc;


public PropertiesViewPanel(DefaultController controller) {

        this.controller = controller;

        initComponents();
        localInitialization();

    }

    // ‹editor-fold defaultstate="collapsed" desc=" Local Initialization "›

    /**
     * Used to provide local initialization of Swing components
     * outside of the NetBeans automatic code generator
     */
    public void localInitialization() {

        opacitySpinner.setModel(new SpinnerNumberModel(100, 0, 100, 1));
        opacitySlider.setModel(new DefaultBoundedRangeModel(100, 0, 0, 100));

        rotationSpinner.setModel(new SpinnerNumberModel(0, -180, 180, 1));
        rotationSlider.setModel(new DefaultBoundedRangeModel(0, 0, -180, 180));

        text.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                textDocumentChanged(e);
            }

            public void removeUpdate(DocumentEvent e) {
                textDocumentChanged(e);
            }

            public void changedUpdate(DocumentEvent e) {
                textDocumentChanged(e);
            }

        });

    }
 

    // ‹/editor-fold›
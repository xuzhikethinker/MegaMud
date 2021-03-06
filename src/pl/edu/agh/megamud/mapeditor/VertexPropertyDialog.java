package pl.edu.agh.megamud.mapeditor;

public class VertexPropertyDialog extends javax.swing.JDialog {
    GraphElements.MyVertex vertex;
    
    public VertexPropertyDialog(java.awt.Frame parent, GraphElements.MyVertex vertex) {
        super(parent, true);
        initComponents();
        this.vertex = vertex;
        setTitle("Location: " + this.vertex.toString());
        this.IDTextField.setValue(this.vertex);
        this.DescriptionTextArea.setText(this.vertex.getDescription());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        IDTextField = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        DescriptionTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edge Properties");

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });

        jLabel1.setText("Description:");

        jLabel3.setText("ID:");

        DescriptionTextArea.setColumns(20);
        DescriptionTextArea.setRows(5);
        jScrollPane1.setViewportView(DescriptionTextArea);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(329, Short.MAX_VALUE)
                .add(jButton1)
                .add(274, 274, 274))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1))
            .add(layout.createSequentialGroup()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(IDTextField)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(IDTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 344, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler
        vertex.setName(IDTextField.getText());
        vertex.setDescription(DescriptionTextArea.getText());
        dispose();
    }//GEN-LAST:event_okButtonHandler
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea DescriptionTextArea;
    private javax.swing.JFormattedTextField IDTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.doodlestack;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage; 
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Stack; 

/**
 *
 * @author eryck
 */
public class DoodleStack extends JFrame {
    
    //call stack functions
    //stack specifically for images
    private static Stack<BufferedImage> undoStack = new Stack<>();
    private static Stack<BufferedImage> redoStack = new Stack<>();
    
    //undo-redo stack menu item using JMenu
    private JMenuItem redoElement; 
    private JMenuItem undoElement; 
    
    private Canvas canvas;
    private JPanel colorPreview;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {
            DoodleStack paint = new DoodleStack();
            paint.setVisible(true); 
        });
    }
    

public DoodleStack(){
        setTitle("DoddleStack");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        canvas = new Canvas();
        add(canvas, BorderLayout.CENTER);
        
        addMenuBar();

        JPanel toolPanel = createToolPanel();
        add(toolPanel, BorderLayout.WEST);
        
        JPanel colorPalette = createColorPalette();
        add(colorPalette, BorderLayout.SOUTH);
        
        updateMenuState();
        
        pack();
        setLocationRelativeTo(null);
        
    }
    
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem clear = new JMenuItem("Clear Canvas"); 
        clear.addActionListener(e -> clrCanvas()); 

        JMenuItem open = new JMenuItem("Open Image"); 
        open.addActionListener(e -> openImg());

        JMenuItem save = new JMenuItem("Save As"); 
        save.addActionListener(e -> saveImg());
        
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        
        fileMenu.add(clear);
        fileMenu.addSeparator(); 
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        
        menuBar.add(fileMenu);
        
        JMenu editMenu = new JMenu("Edit");
        
        undoElement = new JMenuItem("Undo");// 
        //Add keyboard shortcut (Ctrl+Z)
        undoElement.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoElement.addActionListener(e -> undo());
        
        redoElement = new JMenuItem("Redo");
        // Add keyboard shortcut (Ctrl+Y)
        redoElement.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redoElement.addActionListener(e -> redo()); 

        editMenu.add(undoElement);
        editMenu.add(redoElement);
        
        menuBar.add(editMenu);
        

        setJMenuBar(menuBar);
    }
    
        private JPanel createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        
        toolPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        ButtonGroup toolGroup = new ButtonGroup();


        JToggleButton pencil = new JToggleButton(new ImageIcon(getClass().getResource("/pencil.png")));
        pencil.setToolTipText("Pencil");
        pencil.addActionListener(e -> canvas.setTool(Tool.PENCIL));
        pencil.setSelected(true);
        pencil.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPanel.add(pencil);

        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JToggleButton eraser = new JToggleButton(new ImageIcon(getClass().getResource("/eraser.png")));
        eraser.setToolTipText("Eraser");
        eraser.addActionListener(e -> canvas.setTool(Tool.ERASER));
        eraser.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPanel.add(eraser);

        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JToggleButton rectButton = new JToggleButton(new ImageIcon(getClass().getResource("/rectangle.png")));
        rectButton.setToolTipText("Rectangle");
        rectButton.addActionListener(e -> canvas.setTool(Tool.RECTANGLE));
        rectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPanel.add(rectButton);

        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JToggleButton textButton = new JToggleButton(new ImageIcon(getClass().getResource("/text.png")));
        textButton.setToolTipText("Text");
        textButton.addActionListener(e -> canvas.setTool(Tool.TEXT));
        textButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPanel.add(textButton);
        
        toolPanel.add(Box.createRigidArea(new Dimension(0, 15)));


        colorPreview = new JPanel();
        colorPreview.setToolTipText("Current Color");
        colorPreview.setPreferredSize(new Dimension(30, 30)); 
        colorPreview.setBackground(canvas.getCurrentColor());
        colorPreview.setBorder(BorderFactory.createEtchedBorder());
        colorPreview.setMaximumSize(new Dimension(40, 40)); 
        colorPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        toolPanel.add(colorPreview); 

        toolPanel.add(Box.createRigidArea(new Dimension(0, 10))); 

 
        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        sizePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel stroke = new JLabel("Size:");
        Integer[] strokeSize = {1, 2, 4, 5, 6, 8, 10, 15, 20};
        JComboBox<Integer> strokeComboBox = new JComboBox<>(strokeSize);

        strokeComboBox.setSelectedIndex(4);
        canvas.setStrokeSize(4);
        strokeComboBox.setMaximumSize(strokeComboBox.getPreferredSize()); 
        strokeComboBox.addActionListener(e -> {
            int newSize = (Integer) strokeComboBox.getSelectedItem();
            canvas.setStrokeSize(newSize);
        });
        
        sizePanel.add(stroke);
        sizePanel.add(strokeComboBox);
        sizePanel.setMaximumSize(sizePanel.getPreferredSize());
        toolPanel.add(sizePanel);
        
        return toolPanel;
    }
        
    
JPanel createColorPalette() {
       
        JPanel palettePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        palettePanel.setBorder(BorderFactory.createEtchedBorder());

     
        Color[] swatches = {
            Color.BLACK, Color.GRAY, new Color(128, 0, 0), Color.RED,
            new Color(128, 128, 0), Color.YELLOW, new Color(0, 128, 0), Color.GREEN,
            new Color(0, 128, 128), Color.CYAN, new Color(0, 0, 128), Color.BLUE,
            new Color(128, 0, 128), Color.MAGENTA, Color.DARK_GRAY, Color.LIGHT_GRAY,
            Color.WHITE, new Color(255, 165, 0) // Orange
        };

      
        for (Color color : swatches) {
            JButton swatch = new JButton();
            swatch.setBackground(color);
            swatch.setPreferredSize(new Dimension(22, 22)); 
            
            if (color.equals(Color.WHITE) || color.equals(Color.LIGHT_GRAY)) {
                swatch.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            } else {
                swatch.setBorder(BorderFactory.createEtchedBorder());
            }

            swatch.addActionListener(e -> {
                canvas.setCurrentColor(color);
                colorPreview.setBackground(color); 
            });
            palettePanel.add(swatch);
        }
        
      
        JButton customColorButton = new JButton("More...");
        customColorButton.setToolTipText("Choose a custom color");
        customColorButton.addActionListener(e -> {
          
            Color newColor = JColorChooser.showDialog(this, "Choose Color", canvas.getCurrentColor());
            if (newColor != null) {
                canvas.setCurrentColor(newColor);
                colorPreview.setBackground(newColor); 
            };
        });
        palettePanel.add(customColorButton);
     

        return palettePanel;
    }

    private void addColorPalette() {
    JPanel colorPalette = new JPanel();
    colorPalette.setLayout(new FlowLayout(FlowLayout.LEFT)); 
    colorPalette.setBorder(BorderFactory.createEtchedBorder());

    JButton blackButton = new JButton();
    blackButton.setPreferredSize(new Dimension(20, 20));
    blackButton.setBackground(Color.BLACK);
    blackButton.addActionListener(e -> canvas.setCurrentColor(Color.BLACK));
    colorPalette.add(blackButton);

    JButton whiteButton = new JButton();
    whiteButton.setPreferredSize(new Dimension(20, 20));
    whiteButton.setBackground(Color.WHITE);
    whiteButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); 
    whiteButton.addActionListener(e -> canvas.setCurrentColor(Color.WHITE));
    colorPalette.add(whiteButton);
    
    add(colorPalette, BorderLayout.SOUTH);
}

    private void clrCanvas() {
        pushToUndoStack();
        canvas.clearCanvas();
    } 

    private void openImg() {
        JFileChooser fileSelect = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images", "jpg", "jpeg", "png", "bmp", "gif");
        fileSelect.setFileFilter(filter);

        int result = fileSelect.showOpenDialog(this); 
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileSelect.getSelectedFile();
            try {
                BufferedImage loadedImg = ImageIO.read(file); 
                
                pushToUndoStack();

                canvas.clearCanvas();
                canvas.getGraphics2D().drawImage(loadedImg, 0,0, null); 
                canvas.repaint();
            } catch (IOException ex){ 
                JOptionPane.showMessageDialog(this, "Error invalid image!"); 
            }
        }
    }

    private void saveImg() {
        JFileChooser fileSaver = new JFileChooser();
        fileSaver.setDialogTitle("Save As");

        fileSaver.setSelectedFile(new File("untitled.png"));

        int result = fileSaver.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileSaver.getSelectedFile();
            try {

                BufferedImage imageToSave = canvas.getImage(); 

                ImageIO.write(imageToSave, "png", file);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image."); 
            }
        }
    }

    //UNDO & REDO Core Feature (image)
    public void pushToUndoStack(){
        undoStack.push(copyImage(canvas.getImage()));

        redoStack.clear();
        updateMenuState();
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(copyImage(canvas.getImage()));
            
            canvas.setImage(undoStack.pop()); 
            updateMenuState();
        }
    }


    private void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(copyImage(canvas.getImage()));
            
            canvas.setImage(redoStack.pop()); 
            updateMenuState();
        }
    } 


    private void updateMenuState() {
        undoElement.setEnabled(!undoStack.isEmpty());
        redoElement.setEnabled(!redoStack.isEmpty());
    }

    private static BufferedImage copyImage(BufferedImage source) { 
        if (source == null) return null;

        BufferedImage copy = new BufferedImage(
            source.getWidth(),
            source.getHeight(),
            source.getType());

        Graphics g = copy.getGraphics();

        g.drawImage(source, 0, 0, null);

        g.dispose();

        return copy;
    } 

    private enum Tool { 
        PENCIL,
        ERASER,
        RECTANGLE,
        TEXT
    }

    private class Canvas extends JPanel {

        private BufferedImage canvasImg; 

        private Graphics2D g2d;

        private Tool currentTool = Tool.PENCIL;
        private Color currentColor = Color.BLACK;
        private Stroke currentStroke = new BasicStroke(4); 

        private int oldX, oldY;
        private int currentX, currentY;

        public Canvas() {
            setBackground(Color.WHITE);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (canvasImg == null) {
                    
                        canvasImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB); 
                        g2d = canvasImg.createGraphics();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        clearCanvas();
                    }
                }
            });
            
        MouseAdapter mouseAdapter = new MouseAdapter() {
        
        @Override
        public void mousePressed(MouseEvent e) {
            oldX = e.getX();
            oldY = e.getY();
            
          
            if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
                pushToUndoStack();
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
        
            if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
                currentX = e.getX();
                currentY = e.getY();

                if (currentTool == Tool.PENCIL) {
                    g2d.setColor(currentColor);
                } else if (currentTool == Tool.ERASER) {
                    g2d.setColor(Color.WHITE);
                }

                g2d.setStroke(currentStroke);
                g2d.drawLine(oldX, oldY, currentX, currentY);
                
                oldX = currentX;
                oldY = currentY;
                
                repaint();
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
        
            if (currentTool == Tool.RECTANGLE) {
                currentX = e.getX();
                currentY = e.getY();
                
    
                pushToUndoStack();
                

                g2d.setColor(currentColor);
                g2d.setStroke(currentStroke);
                
                int x = Math.min(oldX, currentX);
                int y = Math.min(oldY, currentY);
                int width = Math.abs(oldX - currentX);
                int height = Math.abs(oldY - currentY);
                
                g2d.drawRect(x, y, width, height);
                repaint();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
 
            if (currentTool == Tool.TEXT) {
                // Get the coordinates from the click
                int x = e.getX();
                int y = e.getY();
                
             
                String text = JOptionPane.showInputDialog(
                    DoodleStack.this, 
                    "Enter text:",   
                    "Add Text",    
                    JOptionPane.PLAIN_MESSAGE
                );
                
  
                if (text != null && !text.isEmpty()) {

                    pushToUndoStack();
                    
                   
                    g2d.setColor(currentColor);
             
                    g2d.setFont(new Font("Arial", Font.PLAIN, 20)); 
                    
                    g2d.drawString(text, x, y);
                    repaint();
                }
            }
        }
    };
     
    addMouseListener(mouseAdapter);
    addMouseMotionListener(mouseAdapter);
}

        public void clearCanvas() {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            if (canvasImg != null) { 
                g.drawImage(canvasImg, 0, 0, null); 
            }
        }

        public BufferedImage getImage() {
            return canvasImg; 
        }

        public Graphics2D getGraphics2D() {
            return g2d;
        }


        public void setImage(BufferedImage img) {

            if (g2d != null) {
                g2d.dispose(); 
            }
            
            canvasImg = img;
            
            if (canvasImg != null) {
       
                g2d = canvasImg.createGraphics(); 
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
           
                g2d.setColor(currentColor);
                g2d.setStroke(currentStroke);
            }
            
            repaint(); 
        }

        public void setTool(Tool tool) {
            this.currentTool = tool;
        }

        public Color getCurrentColor() {
            return currentColor;
        }

        public void setCurrentColor(Color color) {
            this.currentColor = color;
        }

        public void setStrokeSize(int size) {
    
            this.currentStroke = new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        }
    }
}
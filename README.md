# DoodleStack

A simple Raster Graphics Program with Stack ADT implemented. Made with Java Swing using Netbeans IDE.
Completed as a Final Requirement for [CS2D] CTCC0513 - Data Structure and Algorithm.

## Features
  Main Drawing Tools: Pencil & Eraser. <br>
  Shape Tool: Draw rectangles. <br>
  Text Tool: Add text directly to the canvas via a dialog. <br>
  Multi-Level Undo/Redo: Backed by the Stack ADT. <br>
  Color Palette: A bottom panel with pre-selected color swatches. <br>
  Custom Color Chooser: A "More..." button that opens the JColorChooser for full color selection. <br>
  Stroke Size: A combo box to select the size (thickness) of the pencil, eraser, and rectangle outline. <br>
<br>
File Operations: <br>
  Save As: Save the current canvas as a .png file. <br>
  Open Image: Load an existing image onto the canvas. <br>
  Clear Canvas: Wipes the canvas clean. <br>

# Stack Core Concept
  DoodleStack uses two Stacks to manage the application's state history. <br>

```java
    // Stack for managing undo/redo history
    private static Stack<BufferedImage> undoStack = new Stack<>();
    private static Stack<BufferedImage> redoStack = new Stack<>();
    
    private JMenuItem redoElement;
    private JMenuItem undoElement;
```

  The LIFO (Last-In, First-Out) nature of a Stack is suitable, as the user always wants to "undo" the last action they performed. <br>

## How It Works:

New Drawing Action (e.g Open Image, Line stroke, rectangle): <br>
  A complete copy of the current canvas is pushed onto the undoStack. <br>
  The redoStack is cleared, as this new action invalidates all previously "undone" states. <br>

"Undo": <br>
  The current canvas state is pushed onto the redoStack (to save it, in case the user wants to "redo"). <br>
  The most recent state is popped from the undoStack and set as the new canvas. <br>
<br>
"Redo": <br>
  The current canvas state is pushed back onto the undoStack. <br>
  The most recent "undone" state is popped from the redoStack and set as the new canvas. <br>

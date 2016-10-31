package eg.edu.alexu.csd.oop.draw;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import eg.edu.alexu.csd.oop.draw.GUIController.ColorType;

@SuppressWarnings("serial")
public class MainScreen extends JFrame implements Observer {

	private static final String ICON_PATH = "icons" + File.separator;
	private final Color fillColor = Color.RED, borderColor = Color.BLACK;
	private DrawArea drawArea;
	private JComponent fillColorLabel, borderColorLabel;
	private GUIController controller;
	private JButton importShape, save, load, move, resize, delete, undo, redo;
	private JToolBar shapesBar, optionsBar, colorsBar;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainScreen frame = new MainScreen();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private MainScreen() {
		super("Vector Paint");
		Container content = this.getContentPane();
		this.setSize(900, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		content.setLayout(new BorderLayout());

		// initialize variables
		controller = new GUIController();

		// drawArea
		drawArea = new DrawArea();
		drawArea.addMouseListener(controller.getDrawAreaMouseListners());
		drawArea.addMouseMotionListener(controller.getDrawAreaMouseMotionListener());

		controller.addObserver(this);
		controller.addObserver(drawArea);

		// toolbars
		shapesBar = new JToolBar("shapes");
		optionsBar = new JToolBar("options");
		colorsBar = new JToolBar("colors", SwingConstants.VERTICAL);

		// shapesBar import Shapes
		this.update(controller, "shapesImported");

		// options buttons
		importShape = new JButton("Import shape");
		save = new JButton("Save");
		load = new JButton("Load");
		delete = new JButton("Delete");
		resize = new JButton("Resize");
		move = new JButton("move");
		undo = new JButton("undo");
		redo = new JButton("redo");

		// add action listeners from GUIController
		importShape.addActionListener((ActionListener) controller.getImportBtnListener());
		save.addActionListener((ActionListener) controller.getSaveBtnListener());
		load.addActionListener((ActionListener) controller.getLoadBtnListener());
		delete.addActionListener((ActionListener) controller.getDeleteBtnListener());
		resize.addActionListener((ActionListener) controller.getResizeBtnListener());
		move.addActionListener((ActionListener) controller.getMoveBtnListener());
		undo.addActionListener((ActionListener) controller.getUndoBtnListener());
		redo.addActionListener((ActionListener) controller.getRedoBtnListener());

		setBtnIcon(importShape);
		setBtnIcon(save);
		setBtnIcon(load);
		setBtnIcon(delete);
		setBtnIcon(resize);
		setBtnIcon(move);
		setBtnIcon(undo);
		setBtnIcon(redo);

		optionsBar.add(importShape);
		optionsBar.add(save);
		optionsBar.add(load);
		optionsBar.add(delete);
		optionsBar.add(resize);
		optionsBar.add(move);
		optionsBar.add(undo);
		optionsBar.add(redo);

		// Color Panel Chooser
		fillColorLabel = new JButton();
		fillColorLabel.setSize(100, 100);
		fillColorLabel.setBackground(fillColor);
		fillColorLabel.addMouseListener(controller.getColorActionListener(ColorType.FILL));
		borderColorLabel = new JButton();
		borderColorLabel.setSize(100, 100);
		borderColorLabel.setBackground(borderColor);
		borderColorLabel.addMouseListener(controller.getColorActionListener(ColorType.BORDER));
		colorsBar.add(fillColorLabel);
		colorsBar.add(borderColorLabel);

		// Add content to Frame
		content.add(drawArea, BorderLayout.CENTER);
		content.add(shapesBar, BorderLayout.NORTH);
		content.add(colorsBar, BorderLayout.WEST);
		content.add(optionsBar, BorderLayout.SOUTH);
	}

	private boolean setBtnIcon(JButton btn) {
		return setBtnIcon(btn, btn.getText());
	}

	private boolean setBtnIcon(JButton btn, String iconName) {
		try {
			BufferedImage master = ImageIO.read(new File(ICON_PATH + iconName.toLowerCase() + ".png"));
			Image scaled = master.getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH);
			btn.setIcon(new ImageIcon(scaled));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void update(Observable arg0, Object message) {
		if (message != null && "shapesImported".equals(message.toString())) {
			shapesBar.removeAll();
			List<Class<? extends Shape>> supportedShapes = controller.getSupportedShapes();
			for (Class<? extends Shape> shapeClass : supportedShapes) {
				String shapeName = shapeClass.getSimpleName();
				JButton tmpBtn = new JButton();
				tmpBtn.addActionListener(controller.getShapeBtnListener(shapeClass));
				tmpBtn.setToolTipText(shapeName);
				if (!setBtnIcon(tmpBtn, shapeName))
					tmpBtn.setText(shapeName);
				shapesBar.add(tmpBtn);
			}
		}
	}

	// Draw Area
	private class DrawArea extends JComponent implements Observer {
		private Image image;
		private Graphics2D canvas;

		public DrawArea() {
			setDoubleBuffered(true);
		}

		// overrides method in JComponent
		public void paintComponent(Graphics g) {
			if (image == null) {
				image = createImage(getSize().width, getSize().height);
				canvas = (Graphics2D) image.getGraphics();
				canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				clear();
			}
			g.drawImage(image, 0, 0, null);
		}

		public Graphics getGraphics() {
			return canvas;
		}

		public void clear() {
			if (canvas != null) {
				canvas.setColor(Color.WHITE);
				canvas.fillRect(0, 0, getWidth(), getHeight());
			}
		}

		public void update(Observable o, Object arg) {
			clear();
			controller.refresh(canvas);
			repaint();
		}

	}

}

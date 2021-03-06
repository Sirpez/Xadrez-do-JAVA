package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import gameControl.Bishop;
import gameControl.Controller;
import gameControl.King;
import gameControl.Knight;
import gameControl.Pawn;
import gameControl.Position;
import gameControl.Queen;
import gameControl.Rook;

public class GameView extends JPanel implements Observer {
	private int SIZE;
	private int SQUARESIDE;
	private Position checkedKingPos;
	
	private Image[][] imagesBoard = new Image[9][9];
	private Color[][] squareColor = new Color[9][9];
	
	public GameView(int screenSize)
	{
		Controller.getInstance().register(this);
		SIZE = screenSize;
		SQUARESIDE = SIZE/8;
		
		initializeImages();
	
		nullifySquareColor();
		
		addMouseListener(new MouseAdapter() {
			boolean pieceIsSelected = false;
			int i, j;
			Position selectedPiecePos;
			

            public void mouseClicked(MouseEvent e) {
            	if(Controller.getInstance().getCheckMate() == 0 && e.getButton() == MouseEvent.BUTTON1) {
	            	Position clickedPos = mapCoordToMatrix(e.getY(), e.getX());
	            	
	            	if(pieceIsSelected){
	            		int returnValue = Controller.getInstance().mouseClicked(clickedPos, selectedPiecePos, 1);
	            		
	            		if( returnValue != 1){

	            			nullifySquareColor();
	            			if(Controller.getInstance().getCheck() != 0) {
	            				squareColor[checkedKingPos.getRow()][checkedKingPos.getColumn()] = Color.YELLOW;
	            			}
	            			
	            			repaint(); 
	            		}
	            		
	            		if(returnValue == -1){
	            			i = clickedPos.getRow();
	                		j = clickedPos.getColumn();
	                		
	                		if(imagesBoard[i][j] != null){
	                			pieceIsSelected = true;
	                			selectedPiecePos = clickedPos;
	                			Controller.getInstance().mouseClicked(null, selectedPiecePos, 0);
	                		}
	            		}
	            		else
	            			pieceIsSelected = false;
	            	}
	            	else
	            	{
	            		i = clickedPos.getRow();
	            		j = clickedPos.getColumn();
	            		
	            		if(imagesBoard[i][j] != null)
	            		{
	            			
	            			selectedPiecePos = clickedPos;
	            			if(Controller.getInstance().mouseClicked(null, selectedPiecePos, 0) == 1)
	            				pieceIsSelected = true;
	            			else
	            				pieceIsSelected = false;
	            		}
	            	}
            	}
            	if(e.getButton() == MouseEvent.BUTTON3)
            	{
            		JFileChooser jfc = new JFileChooser();
    				int returnValue = jfc.showSaveDialog(null);
    				
    				if(returnValue == JFileChooser.APPROVE_OPTION)
    				{
    					String path = jfc.getSelectedFile().getPath();
    					int size = path.length();
    					if(!jfc.getSelectedFile().getPath().substring(size-3).equals("txt"))
    					{
    						path += ".txt";
    					}
    					
    					int savingReturn = Controller.getInstance().saveGame(path);
    					
    					if(savingReturn == 1)
    					{
    						//all right
    					}
    					
    				}
            	}
            }
			
        });
	}
	
	// The function used by the Observable (board) to notify this view that changes occurred
	@Override
	public void update(Observable arg0, Object arg1) {
		String descriptorTemp = (String) arg1;
		char[] descriptor = descriptorTemp.toCharArray();
		Position square;
		int i = 0;
		
		while(descriptor[i] != '\0')
		{
			switch(descriptor[i]) {
				case 'p':
					Position original = new Position(Character.getNumericValue(descriptor[i+1]), Character.getNumericValue(descriptor[i+2]));
					Position destination = new Position(Character.getNumericValue(descriptor[i+3]), Character.getNumericValue(descriptor[i+4]));
					
					imagesBoard[destination.getRow()][ destination.getColumn()] = imagesBoard[original.getRow()][original.getColumn()];
					imagesBoard[original.getRow()][original.getColumn()] = null;
					
					nullifySquareColor();
					
					break;
				case 'g':
					square = new Position(Character.getNumericValue(descriptor[i+1]), Character.getNumericValue(descriptor[i+2]));
					
					squareColor[square.getRow()][square.getColumn()] = Color.GREEN;
					
					break;
				case 'r':
					square = new Position(Character.getNumericValue(descriptor[i+1]), Character.getNumericValue(descriptor[i+2]));
					
					squareColor[square.getRow()][square.getColumn()] = Color.RED;
					break;
				case 'y':
					square = new Position(Character.getNumericValue(descriptor[i+1]), Character.getNumericValue(descriptor[i+2]));
					
					if(Controller.getInstance().getCheck() != 0)
						checkedKingPos = square;
					
					squareColor[square.getRow()][square.getColumn()] = Color.YELLOW;
					break;
				case 'X':
					JPopupMenu winPopup;
					int victoryColor = Character.getNumericValue(descriptor[i+1]);
					String winText = "";
					
					winPopup = new JPopupMenu();
                    
					if(victoryColor == 1)
						winText = "AS BRANCAS GANHARAM! PARAB�NS.";
					else if(victoryColor == -1)
						winText = "AS PRETAS GANHARAM! PARAB�NS.";
				
					winPopup.add(new JMenuItem(winText));
					winPopup.show(getRootPane( ), SIZE/2, SIZE/2);
					
					break;
				case 'D':
					JPopupMenu drawPopup = new JPopupMenu();
					drawPopup.add(new JMenuItem("Que Pena! Deu empate..."));
					drawPopup.show(getRootPane( ), SIZE/2, SIZE/2);
					
					break;
				case 'P':
					int row = Character.getNumericValue(descriptor[i+1]);
			  		int col = Character.getNumericValue(descriptor[i+2]);
			  	
			  		
					ActionListener menuListener = new ActionListener() {
						
						  public void actionPerformed(ActionEvent event) {
							  switch(event.getActionCommand())
							  {
							  	case "Cavalo":
							  		
									try {
										if(Controller.getInstance().getTurn() == -1)
											imagesBoard[row][col] = ImageIO.read(new File("assets\\b_cavalo.gif"));
										else
											imagesBoard[row][col] = ImageIO.read(new File("assets\\p_cavalo.gif"));
									} catch (IOException e) {
										e.printStackTrace();
									}
							  		
							  		break;
							  	case "Bispo":
							  		try {
										if(Controller.getInstance().getTurn() == -1)
											imagesBoard[row][col] = ImageIO.read(new File("assets\\b_bispo.gif"));
										else
											imagesBoard[row][col] = ImageIO.read(new File("assets\\p_bispo.gif"));
									} catch (IOException e) {
										e.printStackTrace();
									}
							  		
							  		break;
							  	case "Torre":
							  		try {
										if(Controller.getInstance().getTurn() == -1)
											imagesBoard[row][col] = ImageIO.read(new File("assets\\b_torre.gif"));
										else
											imagesBoard[row][col] = ImageIO.read(new File("assets\\p_torre.gif"));
									} catch (IOException e) {
										e.printStackTrace();
									}
							  		break;
							  	case "Dama":
							  		try {
										if(Controller.getInstance().getTurn() == -1)
											imagesBoard[row][col] = ImageIO.read(new File("assets\\b_dama.gif"));
										else
											imagesBoard[row][col] = ImageIO.read(new File("assets\\p_dama.gif"));
									} catch (IOException e) {
										e.printStackTrace();
									}
							  		break;
							  }
							  
							  Controller.getInstance().promotion(event.getActionCommand(), row, col);
							  repaint();
						  }
						};

					JPopupMenu popup;

					popup = new JPopupMenu();
                    // add menu items to popup
					JMenuItem knightItem = new JMenuItem("Cavalo");
					knightItem.addActionListener(menuListener);
					
					JMenuItem bishopItem = new JMenuItem("Bispo");
					bishopItem.addActionListener(menuListener);
					
					JMenuItem rookItem = new JMenuItem("Torre");
					rookItem.addActionListener(menuListener);
					
					JMenuItem queenItem = new JMenuItem("Dama");
					queenItem.addActionListener(menuListener);
					
					popup.add(knightItem);
					popup.add(bishopItem);
					popup.add(rookItem);
					popup.add(queenItem);
					
					popup.show(getRootPane( ), 0, 0);
					System.out.println("PROMOCAO");
					
					
					break;
			}
			
			i++;
		}
		
		repaint();
	}
	
	private void nullifySquareColor(){
		for(int i = 1; i < 9; i++)
			for(int j = 1; j < 9; j++) 
				squareColor[i][j] = null;
	}
	private void initializeImages(){
		
		try {
			// Filling Black Pieces at start position
			imagesBoard[1][1] = ImageIO.read(new File("assets\\p_torre.gif"));
			imagesBoard[1][2] = ImageIO.read(new File("assets\\p_cavalo.gif"));
			imagesBoard[1][3] = ImageIO.read(new File("assets\\p_bispo.gif"));
			imagesBoard[1][4] = ImageIO.read(new File("assets\\p_dama.gif"));
			imagesBoard[1][5] = ImageIO.read(new File("assets\\p_rei.gif"));
			imagesBoard[1][6] = ImageIO.read(new File("assets\\p_bispo.gif"));
			imagesBoard[1][7] = ImageIO.read(new File("assets\\p_cavalo.gif"));
			imagesBoard[1][8] = ImageIO.read(new File("assets\\p_torre.gif"));
			
			imagesBoard[2][1] = ImageIO.read(new File("assets\\p_peao.gif"));
			imagesBoard[2][2] = ImageIO.read(new File("assets\\p_peao.gif"));
			imagesBoard[2][3] = ImageIO.read(new File("assets\\p_peao.gif"));
			imagesBoard[2][4] = ImageIO.read(new File("assets\\p_peao.gif"));
			imagesBoard[2][5] = ImageIO.read(new File("assets\\p_peao.gif"));
			imagesBoard[2][6] = ImageIO.read(new File("assets\\p_peao.gif"));
			imagesBoard[2][7] = ImageIO.read(new File("assets\\p_peao.gif"));
			imagesBoard[2][8] = ImageIO.read(new File("assets\\p_peao.gif"));
			
			// Filling White Pieces at start position
			imagesBoard[8][1] = ImageIO.read(new File("assets\\b_torre.gif"));
			imagesBoard[8][2] = ImageIO.read(new File("assets\\b_cavalo.gif"));
			imagesBoard[8][3] = ImageIO.read(new File("assets\\b_bispo.gif"));
			imagesBoard[8][4] = ImageIO.read(new File("assets\\b_dama.gif"));
			imagesBoard[8][5] = ImageIO.read(new File("assets\\b_rei.gif"));
			imagesBoard[8][6] = ImageIO.read(new File("assets\\b_bispo.gif"));
			imagesBoard[8][7] = ImageIO.read(new File("assets\\b_cavalo.gif"));
			imagesBoard[8][8] = ImageIO.read(new File("assets\\b_torre.gif"));
			
			imagesBoard[7][1] = ImageIO.read(new File("assets\\b_peao.gif"));
			imagesBoard[7][2] = ImageIO.read(new File("assets\\b_peao.gif"));
			imagesBoard[7][3] = ImageIO.read(new File("assets\\b_peao.gif"));
			imagesBoard[7][4] = ImageIO.read(new File("assets\\b_peao.gif"));
			imagesBoard[7][5] = ImageIO.read(new File("assets\\b_peao.gif"));
			imagesBoard[7][6] = ImageIO.read(new File("assets\\b_peao.gif"));
			imagesBoard[7][7] = ImageIO.read(new File("assets\\b_peao.gif"));
			imagesBoard[7][8] = ImageIO.read(new File("assets\\b_peao.gif"));
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
		
		
		// Fill empty squares with null
		for(int i = 3; i < 7; i++) {
			for(int j = 1; j < 9; j++) {
				imagesBoard[i][j] = null;
			}
		}
	}
	
	public void continueGame(ArrayList<String> gamePieces)
	{
		//Clearing the imgBoard
		for(int i = 1; i < 9; i++) {
			for(int j = 1; j < 9; j++) {
				imagesBoard[i][j] = null;
			}
		}
		
		for(int i=1; i<gamePieces.size(); i++)
		{
			
			int row = Integer.parseInt(gamePieces.get(i).substring(1, 2));
			int col = Integer.parseInt(gamePieces.get(i).substring(2, 3));
			int color = Integer.parseInt(gamePieces.get(i).substring(3, 4));
			if(color==0)
				color = -1;
			
			try {
				switch(gamePieces.get(i).substring(0, 1))
				{
					case "k":
						if(color==1)
							imagesBoard[row][col] = ImageIO.read(new File("assets\\b_rei.gif"));
						else
							imagesBoard[row][col] = ImageIO.read(new File("assets\\p_rei.gif"));
						break;
					case "q":
						if(color==1)
							imagesBoard[row][col] = ImageIO.read(new File("assets\\b_dama.gif"));
						else
							imagesBoard[row][col] = ImageIO.read(new File("assets\\p_dama.gif"));
						break;
					case "r":
						if(color==1)
							imagesBoard[row][col] = ImageIO.read(new File("assets\\b_torre.gif"));
						else
							imagesBoard[row][col] = ImageIO.read(new File("assets\\p_torre.gif"));
						break;
					case "b":
						if(color==1)
							imagesBoard[row][col] = ImageIO.read(new File("assets\\b_bispo.gif"));
						else
							imagesBoard[row][col] = ImageIO.read(new File("assets\\p_bispo.gif"));
						break;
					case "n":
						if(color==1)
							imagesBoard[row][col] = ImageIO.read(new File("assets\\b_cavalo.gif"));
						else
							imagesBoard[row][col] = ImageIO.read(new File("assets\\p_cavalo.gif"));
						break;
					case "p":
						if(color==1)
							imagesBoard[row][col] = ImageIO.read(new File("assets\\b_peao.gif"));
						else
							imagesBoard[row][col] = ImageIO.read(new File("assets\\p_peao.gif"));
						break;
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	// Auxiliary function for mapping the clicked screen position to an actual integer position
	private Position mapCoordToMatrix(float x, float y)
	{
		Position p = new Position((int) x/SQUARESIDE +1, (int) y/SQUARESIDE +1);
		return p;
	}
	

	// Graphics function for painting the panel
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		int i, j;

		for(i = 1; i < 9; i++) {
			for(j = 1; j < 9; j++) {
				Rectangle2D rt = new Rectangle2D.Double((j-1)*SQUARESIDE,(i-1)*SQUARESIDE,SQUARESIDE,SQUARESIDE);
				
				if(squareColor[i][j] == null) {
					g2d.setPaint(Color.WHITE);
					if(i%2==0){
						if(j%2!=0) {
							g2d.setPaint(Color.BLACK);
						}
					}
					else{
						if(j%2==0) {
							g2d.setPaint(Color.BLACK);
						}
					}
				}
				else {
					g2d.setPaint(squareColor[i][j]);
				}
				g2d.fill(rt);
			}
		}
		
		for(i = 1; i < 9; i++) {
            for(j = 1; j < 9; j++) {
				if(imagesBoard[i][j] != null){
					if(g2d.drawImage(imagesBoard[i][j], (j - 1) * SQUARESIDE, (i - 1) * SQUARESIDE, 
					   SQUARESIDE, SQUARESIDE, null) == false) {
						System.out.println("Erro ao desenhar a seguinte imagem de posicao (" + i + ", " + j + ").");
					}
				}
			}
		}
		
		//g2d.dispose();
	}

}

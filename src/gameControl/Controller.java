package gameControl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observer;

public class Controller {
	
	private static Controller ctrl = null;
	
	private int turn = 1;
	
	private int check = 0;
	
	private int checkMate = 0;
	
	public static Controller getInstance()
	{
		if(ctrl == null)
			ctrl = new Controller();
		return ctrl;
	}
	
	public void register(Observer o) {
		Board.getBoard().addObserver(o);
	}
	
	/* Notifies the board controller that a click has happened
	*  Params:
	*     1) clickPos: The destination position of the selected piece;
	*     2) selectedPiece: The piece selected for movimentation;
	*     3) clickType: If it's the first click (0) or the second (1).
	*     
	*  Return values:
	*     1, all right
	*     0, forbidden move
	*     -1, changing the selected piece
	*/
	public int mouseClicked(Position clickPos, Position selectedPiece, int clickType)
	{
		if(checkMate == 0) {
			
			if(clickType == 1){
				if(Board.getBoard().sqrState(clickPos) != turn ){
					if(Board.getBoard().click(clickPos, selectedPiece)){
						
						return 1;
					}

					return 0;
				}
				return -1;
			}

			if(Board.getBoard().sqrState(selectedPiece) == turn )
			{
				if(Board.getBoard().selectedPiece(selectedPiece))
					return 1;
				return 0;
			}
			
			return 0;
		}
		
		return 0;
	}
	
	
	public int saveGame(String path)
	{
		try {
			FileWriter fw = new FileWriter(path);
			PrintWriter pw = new PrintWriter(fw);
			
			String gameString = Board.getBoard().getGameString();

			pw.print(gameString);

			pw.close();
			return 1;
		}catch(IOException e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	
	public void promotion(String promoteTo, int row, int col)
	{
		Board.getBoard().promotion(promoteTo, row, col);
	}
	
	public int getTurn()
	{
		return turn;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	public int getCheckMate() {
		return checkMate;
	}

	public void setCheckMate(int checkMate) {
		this.checkMate = checkMate;
	}
	
	public void setTurn(int rTurn)
	{
		turn = rTurn;
	}
	
	public void changeTurn()
	{
		turn = turn * (-1);
	}
}
